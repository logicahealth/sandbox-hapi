package org.hspconsortium.platform.api.fhir;

import org.hspconsortium.platform.api.fhir.model.TenantInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.sql.*;
import java.util.List;

@Component
public class SqlDatabaseSnapshotStrategy implements SnapshotStrategy {

    private final int OFF = 0;
    private final int ON = 1;

    @Autowired
    @Lazy
    private DatabaseManager databaseManager;

    @Override
    public TenantInfo takeSnapshot(String sourceSchema, String snapshotKey) {
        Assert.notNull(sourceSchema);
        Assert.notNull(snapshotKey);

        TenantInfo tenantInfo = databaseManager.getTenantInfo(sourceSchema);
        if (tenantInfo.getSnapshots().contains(snapshotKey)) {
            throw new RuntimeException("Snapshot already exists for " + tenantInfo.toString());
        }

        String snapshotSchema = sourceSchema + DatabaseProperties.SANDBOX_SCHEMA_SNAPSHOT_DELIMITER + snapshotKey;

        cloneSchema(sourceSchema, snapshotSchema);

        tenantInfo.getSnapshots().add(snapshotKey);
        return databaseManager.save(sourceSchema, tenantInfo, true);
    }

    @Override
    public TenantInfo restoreSnapshot(String sourceSchema, String snapshotKey) {
        Assert.notNull(sourceSchema);
        Assert.notNull(snapshotKey);

        TenantInfo sourceTenantInfo = databaseManager.getTenantInfo(sourceSchema);
        if (!sourceTenantInfo.getSnapshots().contains(snapshotKey)) {
            throw new RuntimeException("Snapshot does not exist for " + sourceTenantInfo.toString());
        }

        String snapshotSchema = sourceSchema + DatabaseProperties.SANDBOX_SCHEMA_SNAPSHOT_DELIMITER + snapshotKey;

        // make sure the snapshotSchema exists
        if (databaseManager.getSchemasLike(snapshotSchema).size() != 1) {
            throw new RuntimeException("Snapshot does not exist for " + snapshotSchema);
        }

        // drop the source schema
        databaseManager.dropSchema(sourceSchema);

        // clone the snapshot into the source
        cloneSchema(snapshotSchema, sourceSchema);

        // update the new tenant info (from the restored sandbox)
        // to have the sandbox list from the original tenant info
        TenantInfo restoredTenantInfo = databaseManager.getTenantInfo(sourceSchema);
        restoredTenantInfo.setSnapshots(sourceTenantInfo.getSnapshots());
        return databaseManager.save(sourceSchema, restoredTenantInfo, true);
    }

    @Override
    public TenantInfo deleteSnapshot(String sourceSchema, String snapshotKey) {
        Assert.notNull(sourceSchema);
        Assert.notNull(snapshotKey);

        TenantInfo tenantInfo = databaseManager.getTenantInfo(sourceSchema);
        // don' fail, just try to delete anyway
//        if (!tenantInfo.getSnapshots().contains(snapshotKey)) {
//            throw new RuntimeException("Snapshot does not exist for " + tenantInfo.toString());
//        }

        String snapshotSchema = sourceSchema + DatabaseProperties.SANDBOX_SCHEMA_SNAPSHOT_DELIMITER + snapshotKey;

        // make sure the snapshotSchema exists
        if (databaseManager.getSchemasLike(snapshotSchema).size() == 1) {
            // drop the source schema
            databaseManager.dropSchema(snapshotSchema);
        }

        if (tenantInfo.getSnapshots() != null) {
            tenantInfo.getSnapshots().remove(snapshotKey);
        }
        return databaseManager.save(sourceSchema, tenantInfo, true);
    }

    private boolean cloneSchema(String sourceSchema, String targetSchema) {
        // make sure the snapshotSchema doesn't exist
        if (!databaseManager.getSchemasLike(targetSchema).isEmpty()) {
            throw new RuntimeException("Schema already exists for " + targetSchema);
        }

        // create the new schema
        databaseManager.createSchemaIfNotExist(targetSchema);

        List<String> tables = databaseManager.getTablesLike(sourceSchema, "%");

        Connection targetSchemaConnection = null;
        PreparedStatement createTableStatement = null;
        try {
            targetSchemaConnection = databaseManager.getNoSchemaDataSource().getConnection();
            databaseManager.useDatabase(targetSchemaConnection, targetSchema);

            createTableStatement = targetSchemaConnection.prepareStatement("SET UNIQUE_CHECKS=" + OFF);
            createTableStatement.executeUpdate();
            createTableStatement.close();

            createTableStatement = targetSchemaConnection.prepareStatement("SET FOREIGN_KEY_CHECKS=" + OFF);
            createTableStatement.executeUpdate();
            createTableStatement.close();

            // create all tables
            for (String tableName : tables) {
                String createTableSql = generateCreateTableSql(sourceSchema, tableName);

                createTableStatement = targetSchemaConnection.prepareStatement(createTableSql);
                createTableStatement.executeUpdate();
                createTableStatement.close();
            }

            // insert all data
            for (String tableName : tables) {
                createTableStatement = targetSchemaConnection.prepareStatement(
                        "INSERT INTO `" + tableName + "` SELECT * FROM `" + sourceSchema + "`.`" + tableName + "`"
                );
                createTableStatement.executeUpdate();
                createTableStatement.close();
            }

            createTableStatement = targetSchemaConnection.prepareStatement("SET UNIQUE_CHECKS=" + ON);
            createTableStatement.executeUpdate();
            createTableStatement.close();

            createTableStatement = targetSchemaConnection.prepareStatement("SET FOREIGN_KEY_CHECKS=" + ON);
            createTableStatement.executeUpdate();
            createTableStatement.close();

            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error cloning schema: " + sourceSchema + " into: " + targetSchema, e);
        } finally {
            databaseManager.closeErDown(targetSchemaConnection, createTableStatement, null);
        }
    }

    private String generateCreateTableSql(String schema, String tableName) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = databaseManager.getNoSchemaDataSource().getConnection();
            databaseManager.useDatabase(connection, schema);
            statement = connection.createStatement();

            resultSet = statement.executeQuery(
                    "SHOW CREATE TABLE `" + schema + "`.`" + tableName + "`"
            );
            if (resultSet.next()) {
                return resultSet.getString(2);
            } else {
                throw new SQLException("Unable to generate create table for " + schema + "." + tableName);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding tables", e);
        } finally {
            databaseManager.closeErDown(connection, statement, resultSet);
        }
    }
}
