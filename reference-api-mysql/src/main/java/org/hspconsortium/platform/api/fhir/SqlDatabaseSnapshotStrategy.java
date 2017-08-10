package org.hspconsortium.platform.api.fhir;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.sql.*;
import java.util.List;
import java.util.Set;

@Component
public class SqlDatabaseSnapshotStrategy implements SnapshotStrategy {

    @Autowired
    @Lazy
    private DatabaseManager databaseManager;

    @Override
    public String takeSnapshot(String sourceSchema, String snapshotKey) {
        Assert.notNull(sourceSchema, "sourceSchema is required");
        Assert.notNull(snapshotKey, "sandboxKey is required");

        Set<String> snapshots = databaseManager.getSnapshotsForSchema(sourceSchema);

        if (snapshots.contains(snapshotKey)) {
            throw new RuntimeException("Snapshot already exists for " + sourceSchema);
        }

        String snapshotSchema = sourceSchema + DatabaseProperties.SANDBOX_SCHEMA_SNAPSHOT_DELIMITER + snapshotKey;

        cloneSchema(sourceSchema, snapshotSchema);

        return snapshotKey;
    }

    @Override
    public String restoreSnapshot(String sourceSchema, String snapshotKey) {
        Assert.notNull(sourceSchema, "sourceSchema is required");
        Assert.notNull(snapshotKey, "sandboxKey is required");

        Set<String> snapshots = databaseManager.getSnapshotsForSchema(sourceSchema);

        if (!snapshots.contains(snapshotKey)) {
            throw new RuntimeException("Snapshot does not exist for " + sourceSchema);
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

        return snapshotKey;
    }

    @Override
    public String deleteSnapshot(String sourceSchema, String snapshotKey) {
        Assert.notNull(sourceSchema, "sourceSchema is required");
        Assert.notNull(snapshotKey, "sandboxKey is required");

        String snapshotSchema = sourceSchema + DatabaseProperties.SANDBOX_SCHEMA_SNAPSHOT_DELIMITER + snapshotKey;

        // make sure the snapshotSchema exists
        if (databaseManager.getSchemasLike(snapshotSchema).size() == 1) {
            // drop the source schema
            databaseManager.dropSchema(snapshotSchema);
        }

        return snapshotKey;
    }

    private boolean cloneSchema(String sourceSchema, String targetSchema) {
        final int OFF = 0;
        final int ON = 1;

        // make sure the targetSchema doesn't exist
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
