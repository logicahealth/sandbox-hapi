package org.hspconsortium.platform.api.fhir;

import org.apache.commons.lang3.Validate;
import org.hspconsortium.platform.api.fhir.model.TenantInfo;
import org.hspconsortium.platform.api.fhir.util.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@PropertySource("classpath:/config/mysql.properties")
public class DatabaseManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);

    private static final String TENANT_INFO_TABLE = "hspc_tenant_info";

    private static final int MAX_SCHEMA_NAME_LENGTH = 64;

    // need a connection that isn't tied to a schema
    @Autowired(required = true)
    @Qualifier("noSchemaDataSource")
    private DataSource noSchemaDataSource;

    @Autowired
    private SnapshotStrategy snapshotStrategy;

    @Value("${hspc.platform.api.fhir.db.username}")
    private String username;

    @Value("${hspc.platform.api.fhir.db.password}")
    private String password;

    DataSource getNoSchemaDataSource() {
        return noSchemaDataSource;
    }

    public Set<String> getSnapshotsForSchema(String schemaName) {
        Set<String> snapshotSchemaNames = getSchemasLike(schemaName +
                DatabaseProperties.SANDBOX_SCHEMA_SNAPSHOT_DELIMITER +
                "%"
        );

        Set<String> snapshotNames = snapshotSchemaNames
                .stream()
                .map(schema -> schema.split("\\" + DatabaseProperties.SANDBOX_SCHEMA_SNAPSHOT_DELIMITER)[1])
                .collect(Collectors.toSet());

        return snapshotNames;
    }

    public String takeSnapshot(String schema, String snapshotKey) {
        return snapshotStrategy.takeSnapshot(schema, snapshotKey);
    }

    public String restoreSnapshot(String schema, String snapshotKey) {
        return snapshotStrategy.restoreSnapshot(schema, snapshotKey);
    }


    public String deleteSnapshot(String schema, String snapshotKey) {
        return snapshotStrategy.deleteSnapshot(schema, snapshotKey);
    }

    public Set<String> getSchemasLike(String schemaSearch) {
        return getSchemasLike(schemaSearch, null);
    }

    public Set<String> getSchemasLike(String schemaSearch, String notLike) {
        Set<String> results = new HashSet<>();

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = noSchemaDataSource.getConnection();
            statement = connection.createStatement();
            StringBuffer queryBuffer = new StringBuffer();
            queryBuffer.append("SELECT DISTINCT schema_name ");
            queryBuffer.append("FROM information_schema.schemata ");
            queryBuffer.append("WHERE schema_name LIKE '");
            queryBuffer.append(schemaSearch);
            queryBuffer.append("' ");
            if (notLike != null) {
                queryBuffer.append("AND schema_name NOT LIKE '" + notLike + "'");
            }
            resultSet = statement.executeQuery(queryBuffer.toString());
            while (resultSet.next()) {
                results.add(resultSet.getString(1));
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding schemas", e);
        } finally {
            closeErDown(connection, statement, resultSet);
        }
    }

    List<String> getTablesLike(String schema, String tableSearch) {
        List<String> results = new ArrayList<>();

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = noSchemaDataSource.getConnection();
            useDatabase(connection, schema);
            statement = connection.createStatement();

            resultSet = statement.executeQuery(
                    "SELECT TABLE_NAME " +
                            "FROM INFORMATION_SCHEMA.TABLES " +
                            "WHERE TABLE_SCHEMA='" + schema + "' " +
                            "AND TABLE_TYPE='BASE TABLE' " +
                            "AND TABLE_NAME LIKE '" + tableSearch + "' ");
            while (resultSet.next()) {
                results.add(resultSet.getString(1));
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding tables", e);
        } finally {
            closeErDown(connection, statement, resultSet);
        }
    }

    public TenantInfo createAndInitializeSchema(String schemaName, TenantInfo tenantInfo) {
        // create the sandbox schema
        createSchemaIfNotExist(schemaName);

        // create the tenant info table
        createTenantInfoTableIfNotExist(schemaName);

        // create the sandbox metadata
        return save(schemaName, tenantInfo);
    }

    boolean createSchemaIfNotExist(String schema) {
        if (!validateSchemaName(schema)) {
            throw new RuntimeException("Invalid schema name: " + schema);
        }
        Set<String> existingSchemas = getSchemasLike(schema);
        if (existingSchemas.isEmpty()) {
            LOGGER.info("Creating schema: " + schema);
            return executeUpdateNoSchema("CREATE DATABASE " + schema);
        }
        return false;
    }

    boolean createTenantInfoTableIfNotExist(String schema) {
        List<String> tables = getTablesLike(schema, TENANT_INFO_TABLE);
        boolean result = false;
        LOGGER.info("Tenant tables empty?: " + tables.isEmpty());
        if (tables.isEmpty()) {
            LOGGER.info("Creating tenant info: " + schema);
            result = executeUpdateWithSchema(schema,
                    "CREATE TABLE " + TENANT_INFO_TABLE + " (" +
                            "tenant_id varchar(255) NOT NULL PRIMARY KEY, " +
                            "hspc_schema_version VARCHAR(10) NOT NULL, " +
                            "allow_open_endpoint VARCHAR(1) NOT NULL, " +
                            "baseline_date DATE NULL " +
                            ")");
            result = addPropertiesToTenantInfoTable(schema);
        }
        return result;
    }

    private boolean addPropertiesToTenantInfoTable(String schema) {
        LOGGER.info("addPropertiesToTenantInfoTable: " + schema);
        return executeUpdateWithSchema(schema,
                "ALTER TABLE " + TENANT_INFO_TABLE + " " +
                        "ADD (properties VARCHAR(4096) NULL)");
    }

    public TenantInfo getTenantInfo(String schema) {
        return getTenantInfo(schema, new LinkedList<>());
    }

    private TenantInfo getTenantInfo(String schema, List<Class> upgradesPerformed) {
        try {
            return getTenantInfoUpgradable(schema);
        } catch (DatabaseUpgradeRequiredException e) {
            if (e instanceof TenantInfoPropertiesColumnMissingException) {
                if (upgradesPerformed.contains(TenantInfoPropertiesColumnMissingException.class)) {
                    // already tried this upgrade
                    throw new RuntimeException("Circular upgrade detected for TenantInfoProperties");
                }
                upgradesPerformed.add(TenantInfoPropertiesColumnMissingException.class);
                addPropertiesToTenantInfoTable(schema);
            }
        }
        // try again with upgrade support
        return getTenantInfo(schema);
    }

    private TenantInfo getTenantInfoUpgradable(String schema) throws DatabaseUpgradeRequiredException {
        LOGGER.info("Retrieving tenant info for: " + schema);
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = noSchemaDataSource.getConnection();
            useDatabase(connection, schema);
            statement = connection.createStatement();
            // there is only one tenant in a schema
            resultSet = statement.executeQuery(
                    "SELECT tenant_id, hspc_schema_version, allow_open_endpoint, baseline_date, properties " +
                            "FROM hspc_tenant_info "
            );
            if (resultSet.next()) {
                TenantInfo tenantInfo = new TenantInfo(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        toBoolean(resultSet.getString(3))
                );
                tenantInfo.setBaselineDate(resultSet.getDate(4) != null ? resultSet.getDate(4).toLocalDate() : null);
                tenantInfo.setProperties(resultSet.getString(5));
                LOGGER.info("Returning tenant info: " + tenantInfo);
                return tenantInfo;
            }
            return null;
        } catch (SQLException e) {
            // detect any database upgrades that are required
            if (e.getMessage().contains("Unknown column 'properties' in 'field list'")) {
                throw new TenantInfoPropertiesColumnMissingException();
            }
            throw new RuntimeException("Error loading tenant info for: " + schema, e);
        } finally {
            closeErDown(connection, statement, resultSet);
        }
    }

    public TenantInfo save(@NotNull String schema, @NotNull TenantInfo tenantInfo) {
        Validate.notNull(tenantInfo);
        Validate.notNull(tenantInfo.getTenantId());
        Validate.notNull(tenantInfo.getHspcSchemaVersion());
        Validate.notNull(tenantInfo.getTenantId());

        LOGGER.info("saving schema: " + schema + ", tenantInfo: " + tenantInfo);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String query;
        TenantInfo existing = getTenantInfo(schema);
        if (existing != null) {
            Validate.isTrue(tenantInfo.getHspcSchemaVersion().equals(existing.getHspcSchemaVersion()),
                    "Tenant schema version can not change");
            query = "UPDATE hspc_tenant_info " +
                    "SET " +
                    " tenant_id='" + tenantInfo.getTenantId() + "' " +
                    ", hspc_schema_version='" + tenantInfo.getHspcSchemaVersion() + "' " +
                    ", allow_open_endpoint='" + toTOrF(tenantInfo.isAllowOpenEndpoint()) + "' " +
                    ", baseline_date=" + (tenantInfo.getBaselineDate() != null ? "'" + formatter.format(tenantInfo.getBaselineDate()) + "'" : "NULL") + " " +
                    ", properties=" + (tenantInfo.getProperties().isEmpty() ? "NULL" : "'" + tenantInfo.getPropertiesAsString() + "'") + " ";
        } else {
            query = "INSERT INTO hspc_tenant_info (tenant_id, hspc_schema_version, allow_open_endpoint, baseline_date, properties) " +
                    "VALUES ( " +
                    "'" + tenantInfo.getTenantId() + "' " +
                    ", '" + tenantInfo.getHspcSchemaVersion() + "' " +
                    ", '" + toTOrF(tenantInfo.isAllowOpenEndpoint()) + "' " +
                    ", " + (tenantInfo.getBaselineDate() != null ? "'" + formatter.format(tenantInfo.getBaselineDate()) + "' " : "NULL ") +
                    ", " + (tenantInfo.getProperties().isEmpty() ? "NULL" : "'" + tenantInfo.getPropertiesAsString() + "' ") +
                    ") ";
        }
        executeUpdateWithSchema(schema, query);

        return getTenantInfo(schema);
    }

    public boolean loadInitialDataset(String schema, Reader reader) {
        LOGGER.info("Loading Initial Dataset for: " + schema);
        Connection connection = null;
        try {
            connection = noSchemaDataSource.getConnection();
            useDatabase(connection, schema);
            ScriptRunner scriptRunner = new ScriptRunner(connection, true, true, true);
            scriptRunner.runScript(reader);
            return true;
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Error loading initial dataset for schema: " + schema, e);
        } finally {
            closeErDown(connection, null, null);
        }
    }

    void useDatabase(Connection connection, String schema) throws SQLException {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("USE " + schema);
        } finally {
            if (statement != null) {
                statement.close();
            }
            // don't close the connection
        }
    }

    boolean executeUpdateNoSchema(String updateSql) {
        return executeUpdateWithSchema(null, updateSql);
    }

    boolean executeUpdateWithSchema(String schema, String updateSql) {
        Connection connection = null;
        Statement createTableStatement = null;
        try {
            connection = noSchemaDataSource.getConnection();
            if (schema != null) {
                useDatabase(connection, schema);
            }
            createTableStatement = connection.createStatement();
            createTableStatement.executeUpdate(updateSql);
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error executing update in schema: " + schema + ", SQL: " + updateSql, e);
        } finally {
            closeErDown(connection, createTableStatement, null);
        }
    }

    public boolean dropSchema(String schema) {
        LOGGER.info("Dropping schema: " + schema);
        Connection connection = null;
        Statement statement = null;
        try {
            connection = noSchemaDataSource.getConnection();
            statement = connection.createStatement();
            statement.execute("DROP DATABASE IF EXISTS " + schema);
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error dropping schema: " + schema, e);
        } finally {
            closeErDown(connection, statement, null);
        }
    }

    void closeErDown(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            LOGGER.error("Error closing connection", e);
        }
    }

    private boolean toBoolean(String tOrF) {
        return (tOrF != null && tOrF.equalsIgnoreCase("T"));
    }

    private String toTOrF(boolean value) {
        return (value ? "T" : "F");
    }

    private boolean validateSchemaName(String schema) {
        return schema.length() < MAX_SCHEMA_NAME_LENGTH;
    }
}
