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

///**
// *  * #%L
// *  *
// *  * %%
// *  * Copyright (C) 2014-2020 Healthcare Services Platform Consortium
// *  * %%
// *  * Licensed under the Apache License, Version 2.0 (the "License");
// *  * you may not use this file except in compliance with the License.
// *  * You may obtain a copy of the License at
// *  *
// *  *      http://www.apache.org/licenses/LICENSE-2.0
// *  *
// *  * Unless required by applicable law or agreed to in writing, software
// *  * distributed under the License is distributed on an "AS IS" BASIS,
// *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  * See the License for the specific language governing permissions and
// *  * limitations under the License.
// *  * #L%
// */
//
//package org.hspconsortium.platform.api.fhir;
//
//import org.hspconsortium.platform.api.model.DataSet;
//import org.hspconsortium.platform.api.model.Sandbox;
//import org.hspconsortium.platform.api.service.SandboxService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//
//@Component
//public class DataSourceRepository {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceRepository.class);
//
//    private MultitenantDatabaseProperties multitenancyProperties;
//
//    private ConcurrentMap<String, DataSource> datasourceCache;
//
//    private SandboxService sandboxService;
//
//    @Autowired
//    public DataSourceRepository(MultitenantDatabaseProperties multitenancyProperties, SandboxService sandboxService) {
//        this.multitenancyProperties = multitenancyProperties;
//        this.sandboxService = sandboxService;
//        datasourceCache = new ConcurrentHashMap<>(this.multitenancyProperties.getDataSourceCacheSize());
//
////        LoadingCache<String, Object> cacheBuilder =
////                Caffeine
////                        .newBuilder()
////                        .maximumSize(this.multitenancyProperties.getDataSourceCacheSize())
////                        .build();
//
////        datasourceCache = new CaffeineCache("datasourceCache", cacheBuilder);
//    }
//
////    public DataSource getDataSource(String hspcSchemaVersion, String tenantIdentifier) {
////        String key = tenantIdentifier + "~" + hspcSchemaVersion;
////
//////        org.springframework.cache.Cache.ValueWrapper valueWrapper = datasourceCache.get(key);
////        if (datasourceCache.containsKey(key)) {
////            return datasourceCache.get(key);
////        }
////
////        DataSource dataSource = createDataSource(hspcSchemaVersion, tenantIdentifier);
////
////        if (dataSource != null) {
////            LOGGER.info(String.format("Tenant '%s' maps to '%s' database url.", tenantIdentifier
////                    , ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getPoolProperties().getUrl()));
////        }
////
////        if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
////            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getPoolProperties().setTestOnBorrow(true);
////            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getPoolProperties().setValidationQuery("SELECT 1");
////        }
////
////        datasourceCache.put(key, dataSource);
////
////        //TODO: see if there's a better place to put this method call
////        createTemplateDataSources(hspcSchemaVersion, tenantIdentifier);
////
////        return dataSource;
////    }
//
////    private DataSource createDataSource(String hspcSchemaVersion, String tenant) {
////        final DataSourceProperties dataSourceProperties = this.multitenancyProperties.getDataSource(hspcSchemaVersion, tenant);
////        DataSourceBuilder factory = DataSourceBuilder
////                .create(this.multitenancyProperties.getDataSource().getClassLoader())
//////                .driverClassName(this.multitenancyProperties.getDataSource().getDriverClassName())
////                .username(dataSourceProperties.getUsername())
////                .password(dataSourceProperties.getPassword())
////                .url(dataSourceProperties.getUrl());
////
////        DataSource dataSource = factory.build();
////        Connection conn = null;
////        try {
////            //verify for a valid datasource
//////            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getPoolProperties().setMaxActive(6);
////            conn = dataSource.getConnection();
////            conn.isValid(2);
////
////        } catch (SQLException e) {
////            // if we are trying to retrieve the default tenant, but the schema doesn't exist
////            if (tenant.equals(multitenancyProperties.getDefaultTenantId())) {
////                sandboxService.save(new Sandbox(tenant, hspcSchemaVersion, true), DataSet.NONE);
////                return createDataSource(hspcSchemaVersion, tenant);
////            }
////
//////            if (Arrays.asList(multitenancyProperties.getTemplateSandboxes()).contains(tenant)) {
//////                if (tenant.endsWith("Empty")) {
//////                    sandboxService.save(new Sandbox(tenant, hspcSchemaVersion, false), DataSet.NONE);
//////                    return createDataSource(hspcSchemaVersion, tenant);
//////                } else {
//////                    sandboxService.save(new Sandbox(tenant, hspcSchemaVersion, false), DataSet.DEFAULT);
//////                    return createDataSource(hspcSchemaVersion, tenant);
//////                }
//////            }
////
////            LOGGER.error(String.format("Connection couldn't be established for tenant '%s' with '%s' database url."
////                    , tenant
////                    , dataSourceProperties.getUrl()));
////            dataSource = null;
////        } finally {
////            // Always make sure result sets and statements are closed, and the connection is returned to the pool
////            if (conn != null) {
////                try {
////                    conn.close();
////                } catch (SQLException e) {
////                    LOGGER.error("Error closing connection pool", e);
////                }
////            }
////        }
////        return dataSource;
////    }
//
////    public void createTemplateDataSources(String hspcSchemaVersion, String mainTenant) {
////        List<String> defaultTenants = new ArrayList<>();
////        switch(mainTenant) {
////            case "hspc8":
////                defaultTenants.add("MasterDstu2Empty");
////                defaultTenants.add("MasterDstu2Smart");
////                createSpecifiedDataSources(hspcSchemaVersion, defaultTenants);
////                return;
////            case "hspc9":
////                defaultTenants.add("MasterStu3Empty");
////                defaultTenants.add("MasterStu3Smart");
////                createSpecifiedDataSources(hspcSchemaVersion, defaultTenants);
////                return;
////            case "hspc10":
////                defaultTenants.add("MasterR4Empty");
////                defaultTenants.add("MasterR4Smart");
////                createSpecifiedDataSources(hspcSchemaVersion, defaultTenants);
////                return;
////        }
////
////    }
////    private void createSpecifiedDataSources(String hspcSchemaVersion, List<String> defaultTenants) {
////        for (String tenant: defaultTenants) {
////            final DataSourceProperties dataSourceProperties = this.multitenancyProperties.getDataSource(hspcSchemaVersion, tenant);
////            DataSourceBuilder factory = DataSourceBuilder
////                    .create(this.multitenancyProperties.getDataSource().getClassLoader())
//////                    .driverClassName(this.multitenancyProperties.getDataSource().getDriverClassName())
////                    .username(dataSourceProperties.getUsername())
////                    .password(dataSourceProperties.getPassword())
////                    .url(dataSourceProperties.getUrl());
////
////            DataSource dataSource = factory.build();
////            Connection conn = null;
////            try {
////                //verify for a valid datasource
////                if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
////                    ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getPoolProperties().setMaxActive(5);
////                }
////                conn = dataSource.getConnection();
////                conn.isValid(2);
////            } catch (SQLException e) {
////                if (tenant.endsWith("Empty")) {
////                    sandboxService.save(new Sandbox(tenant, hspcSchemaVersion, false), DataSet.NONE);
////                } else {
////                    sandboxService.save(new Sandbox(tenant, hspcSchemaVersion, false), DataSet.DEFAULT);
////                }
////            } finally {
////                // Always make sure result sets and statements are closed, and the connection is returned to the pool
////                if (conn != null) {
////                    try {
////                        conn.close();
////                    } catch (SQLException e) {
////                        LOGGER.error("Error closing connection pool", e);
////                    }
////                }
////            }
////
////        }
////
////    }
//
////    public HashMap<String, Double> memoryAllSandboxes(List<String> activeSandboxIds) {
////        HashMap<String, Double> sandboxMemorySizes = new HashMap<>();
////
////        final DataSourceProperties dataSourceProperties = this.multitenancyProperties.getInformationSchemaProperties();
////        DataSourceBuilder factory = DataSourceBuilder
////                .create(this.multitenancyProperties.getDataSource().getClassLoader())
//////                .driverClassName(this.multitenancyProperties.getDataSource().getDriverClassName())
////                .username(dataSourceProperties.getUsername())
////                .password(dataSourceProperties.getPassword())
////                .url(dataSourceProperties.getUrl());
////
////        DataSource dataSource = factory.build();
////        Connection conn = null;
////        try {
////            //verify for a valid datasource
////            if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
////                ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getPoolProperties().setMaxActive(5);
////            }
////            conn = dataSource.getConnection();
////            conn.isValid(2);
////            Statement stmt = conn.createStatement();
////            String query = "select table_schema, sum((data_length+index_length)/1024/1024) AS MB from information_schema.tables group by 1;" ;
////            ResultSet rs = stmt.executeQuery(query);
////            while (rs.next()) {
////                if (activeSandboxIds.contains(rs.getString(1))) {
////                    sandboxMemorySizes.put(rs.getString(1), Double.parseDouble(rs.getString(2)));
////                }
////
////            }
////            return sandboxMemorySizes;
////        } catch (SQLException e) {
////
////        } finally {
////            // Always make sure result sets and statements are closed, and the connection is returned to the pool
////            if (conn != null) {
////                try {
////                    conn.close();
////
////                } catch (SQLException e) {
////                    LOGGER.error("Error closing connection pool", e);
////                }
////            }
////        }
////        return null;
////    }
//
////    public HashMap<String, Double> memoryAllSandboxesOfUser(List<String> sandboxIds) {
////        HashMap<String, Double> sandboxMemorySizes = new HashMap<>();
////
////        final DataSourceProperties dataSourceProperties = this.multitenancyProperties.getInformationSchemaProperties();
////        DataSourceBuilder factory = DataSourceBuilder
////                .create(this.multitenancyProperties.getDataSource().getClassLoader())
//////                .driverClassName(this.multitenancyProperties.getDataSource().getDriverClassName())
////                .username(dataSourceProperties.getUsername())
////                .password(dataSourceProperties.getPassword())
////                .url(dataSourceProperties.getUrl());
////
////        DataSource dataSource = factory.build();
////        Connection conn = null;
////        try {
////            //verify for a valid datasource
////            if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
////                ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getPoolProperties().setMaxActive(5);
////            }
////            conn = dataSource.getConnection();
////            conn.isValid(2);
////            Statement stmt = conn.createStatement();
////            for (String id: sandboxIds) {
////                String query = "select table_schema, sum((data_length+index_length)/1024/1024) AS MB from information_schema.tables " +
////                        "WHERE table_schema REGEXP 'hspc_[0-9]_" + id + "' group by 1;";
////                ResultSet rs = stmt.executeQuery(query);
////                while (rs.next()) {
////                    sandboxMemorySizes.put(rs.getString(1), Double.parseDouble(rs.getString(2)));
////                }
////            }
////
////            return sandboxMemorySizes;
////        } catch (SQLException e) {
////
////        } finally {
////            // Always make sure result sets and statements are closed, and the connection is returned to the pool
////            if (conn != null) {
////                try {
////                    conn.close();
////
////                } catch (SQLException e) {
////                    LOGGER.error("Error closing connection pool", e);
////                }
////            }
////        }
////        return null;
////    }
//}
