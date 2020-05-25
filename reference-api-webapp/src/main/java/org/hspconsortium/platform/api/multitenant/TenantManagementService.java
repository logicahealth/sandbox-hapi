/**
 *  * #%L
 *  *
 *  * %%
 *  * Copyright (C) 2014-2020 Healthcare Services Platform Consortium
 *  * %%
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  * #L%
 */

package org.hspconsortium.platform.api.multitenant;

import org.hspconsortium.platform.api.multitenant.db.DataSourceRepository;
import org.hspconsortium.platform.api.util.ScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Service
public class TenantManagementService {

    private DataSourceRepository dataSourceRepository;
    private String schemaPrefix;

    @Autowired
    public TenantManagementService(@Value("${hspc.platform.api.fhir.datasource.schemaPrefix}") String schemaPrefix, DataSourceRepository dataSourceRepository) {
        this.schemaPrefix = schemaPrefix;
        this.dataSourceRepository = dataSourceRepository;
    }

    private static final String SCHEMA_INIT_PATH = "db/empty_schema.sql";

    public boolean createTenant(String tenantName) {
        return createSchema(tenantName) && initializeSchema(tenantName);
    }

    public boolean deleteTenant(String tenantName) {
        // remove cached connection
        dataSourceRepository.deleteDataSourceIfExists(tenantName);

        // remove schema in database
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSourceRepository.getDefaultDataSource().getConnection();
            statement = connection.createStatement();
            statement.execute("DROP DATABASE IF EXISTS `" + schemaPrefix + tenantName + "`");
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error dropping schema: " + tenantName, e);
        } finally {
            closeConnection(connection);
        }
    }

    public boolean isValidExistingTenant(String tenantName) {
        try {
            dataSourceRepository.getDataSource(tenantName);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public boolean isValidTenantName(String tenantName) {
        if (tenantName == null || tenantName.isEmpty()) {
            return false;
        }

        return tenantName.matches("([a-z0-9-_])*");
    }

    public List<String> findAll() {
        List<String> tenants = new ArrayList<>();

        String query = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME LIKE '" + schemaPrefix + "%';";

        DataSource defaultDs = dataSourceRepository.getDefaultDataSource();
        Connection connection = null;
        try {
            connection = defaultDs.getConnection();
            ResultSet rs = connection.createStatement().executeQuery(query);
            while (rs.next()) {
                String curTenant = rs.getString("SCHEMA_NAME");
                tenants.add(curTenant.substring(schemaPrefix.length()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                closeConnection(connection);
            }
        }

        return tenants;
    }

    private boolean createSchema(String tenantName) {
        DataSource defaultDs = dataSourceRepository.getDefaultDataSource();
        Connection connection = null;
        try {
            connection = defaultDs.getConnection();
            connection.createStatement().executeUpdate("CREATE DATABASE `" + schemaPrefix + tenantName + "`;");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                closeConnection(connection);
            }
        }
        return true;
    }

    private boolean initializeSchema(String tenantName) {
        // Read schema generating sql script
        final String dataFileName = SCHEMA_INIT_PATH;
        Reader reader = null;
        try {
            ClassPathResource classPathResource = new ClassPathResource(dataFileName);
            InputStream inputStream = classPathResource.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error creating initial dataset. Data file reference '%s'", dataFileName), e);
        }

        // Run script against database connection
        Connection connection = null;
        try {
            connection = dataSourceRepository.getDataSource(tenantName).getConnection();
            ScriptRunner scriptRunner = new ScriptRunner(connection, true, true, true);
            scriptRunner.runScript(reader);
            return true;
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Error loading initial dataset for tenant: " + tenantName, e);
        } finally {
            closeConnection(connection);
        }
    }

    private void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            // who cares
        }
    }
}
