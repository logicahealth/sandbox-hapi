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

package org.hspconsortium.platform.api.multitenant.db;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hspconsortium.platform.api.util.TAR;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class DataSourceRepository {

    private Log log = LogFactory.getLog(DataSourceRepository.class);

    private ConcurrentMap<String, DataSourceCacheWrapper> datasourceCache;

    @Value("${hspc.platform.api.fhir.datasource.cache.size:5}")
    private int dataSourceCacheSize;

    @Value("${hspc.platform.api.fhir.datasource.url}")
    private String jdbcUrl;

    @Value("${hspc.platform.api.fhir.datasource.username}")
    private String dbUsername;

    @Value("${hspc.platform.api.fhir.datasource.password}")
    private String dbPassword;

    @Value("${hspc.platform.api.fhir.datasource.defaultTenant}")
    private String defaultTenant;

    @Value("${hspc.platform.api.fhir.datasource.schemaPrefix}")
    private String schemaPrefix;

    @Value("${hspc.platform.api.fhir.datasource.minimumIdle}")
    private Integer minimumIdle;

    @Value("${hspc.platform.api.fhir.datasource.maximumPoolSize}")
    private Integer maximumPoolSize;

    @Value("${hspc.platform.api.fhir.datasource.idleTimeout}")
    private Integer idleTimeout;

    @Value("${hspc.platform.api.fhir.datasource.connectionTimeout}")
    private Integer connectionTimeout;

    @Value("${hspc.platform.api.fhir.datasource.leakDetectionThreshold}")
    private Integer leakDetectionThreshold;

    @Value("${hibernate.search.default.indexBase}")
    private String luceneBase;

    @Value("${hspc.platform.api.fhir.hibernate.indexSourceUrl:}")
    private String indexSourceUrl;

    private DataSource defaultDataSource;

    public DataSourceRepository() {
        datasourceCache = new ConcurrentHashMap<>(dataSourceCacheSize);
    }

    public DataSourceRepository(ConcurrentMap<String, DataSourceCacheWrapper> datasourceCache) {
        this.datasourceCache = datasourceCache;
    }

    public DataSource getDefaultDataSource() {
        if (defaultDataSource == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(dbUsername);
            config.setPassword(dbPassword);
            config.setMinimumIdle(minimumIdle);
            config.setMaximumPoolSize(maximumPoolSize);
            config.setIdleTimeout(idleTimeout);
            config.setConnectionTimeout(connectionTimeout);
            config.setLeakDetectionThreshold(leakDetectionThreshold);

            defaultDataSource = new HikariDataSource(config);
        }

        return defaultDataSource;
    }

    public DataSource getDataSource(String tenantIdentifier) {

        if (datasourceCache.containsKey(tenantIdentifier)) {
            DataSourceCacheWrapper dsWrapper = datasourceCache.get(tenantIdentifier);
            dsWrapper.setLastUsedDate(LocalDateTime.now());
            return dsWrapper.getDataSource();
        }

        if (datasourceCache.size() >= dataSourceCacheSize) {
            removeLastUsedFromCache();
        }

        DataSource ds = createDataSource(tenantIdentifier);
        datasourceCache.put(tenantIdentifier, new DataSourceCacheWrapper(ds, LocalDateTime.now()));
        log.warn("datasourceCache size: " + datasourceCache.size());
        return ds;
    }

    public void deleteDataSourceIfExists(String tenantIdentifier) {
        if (datasourceCache.containsKey(tenantIdentifier)) {
            DataSourceCacheWrapper dsWrapper = datasourceCache.get(tenantIdentifier);
            try {
                dsWrapper.getDataSource().getConnection().close();
            } catch (SQLException e) {
                log.info("Could not close connection when deleting tenant " + tenantIdentifier);
            }
            datasourceCache.remove(tenantIdentifier);
        }
    }

    private DataSource createDataSource(String tenant) throws HikariPool.PoolInitializationException {
        HikariDataSource ds;

        ds = new HikariDataSource(hikariConfig(tenant));

        Connection conn = null;
        try {
            //verify for a valid datasource
            conn = ds.getConnection();
            conn.isValid(2);
            log.warn("Creating datasource: " + tenant);
        } catch (SQLException e) {
            log.error(String.format("Connection couldn't be established for tenant '%s'.", tenant));
            ds = null;
        } finally {
            // Always make sure result sets and statements are closed, and the connection is returned to the pool
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("Error closing connection pool", e);
                }
            }
        }
        return ds;
    }

    private HikariConfig hikariConfig(String tenant) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl + "/" + schemaPrefix + tenant);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setMinimumIdle(minimumIdle);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setIdleTimeout(idleTimeout);
        config.setConnectionTimeout(connectionTimeout);
        config.setLeakDetectionThreshold(leakDetectionThreshold);

        loadIndexFiles();

        return config;
    }

    private void removeLastUsedFromCache() {
        LocalDateTime oldestDate = null;
        String oldestTenant = null;
        for (String key : datasourceCache.keySet()) {
            LocalDateTime date = datasourceCache.get(key).getLastUsedDate();
            if (oldestDate == null || date.isBefore(oldestDate)) {
                oldestDate = date;
                oldestTenant = key;
            }
        }
        log.warn("Evicting datasource: " + oldestTenant);

        //drop all connections before removing datasource
        HikariDataSource oldestDataSource = (HikariDataSource) datasourceCache.get(oldestTenant).getDataSource();
        oldestDataSource.close();

        datasourceCache.remove(oldestTenant);
    }

    private void loadIndexFiles() {
        log.info("loadIndexFiles()");
        // download the index files if they don't exist already
        if (StringUtils.isNotEmpty(luceneBase) && StringUtils.isNotEmpty(indexSourceUrl)) {
            String fromFile = indexSourceUrl;
            String[] parts = indexSourceUrl.split("/");
            String fromFileName = parts[parts.length - 1];
            String toFile = luceneBase + "/" + fromFileName;
            String tarFile = luceneBase + "/indexes.tar";
            // download the index files if they don't exist already
            File previousFile = new File(toFile);
            if (!previousFile.exists()) {
                // fetch
                try {
                    log.warn(toFile + " has not been loaded, proceeding with load");
                    //connectionTimeout, readTimeout = 120 seconds
                    int timeout = 120 * 10000;
                    log.warn("Downloading " + fromFile);
                    FileUtils.copyURLToFile(new URL(fromFile), new File(toFile), timeout, timeout);
                    log.warn("Downloading " + fromFile + " complete");

                    log.warn("Unzipping " + toFile);
                    InputStream fin = Files.newInputStream(Paths.get(toFile));
                    BufferedInputStream in = new BufferedInputStream(fin);
                    OutputStream out = Files.newOutputStream(Paths.get(tarFile));
                    GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
                    final byte[] buffer = new byte[1024];
                    int n = 0;
                    while (-1 != (n = gzIn.read(buffer))) {
                        out.write(buffer, 0, n);
                    }
                    out.close();
                    gzIn.close();
                    log.warn("Unzipping " + toFile + " complete");

                    log.warn("Untarring " + tarFile + " to " + luceneBase);
                    TAR.decompress(tarFile, new File(luceneBase));
                    log.warn("Untarring " + tarFile + " complete");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                log.warn(toFile + " has already been loaded, aborting load");
            }
        }
    }

//    public HashMap<String, Double> memoryAllSandboxes(List<String> activeSandboxIds) {
//        HashMap<String, Double> sandboxMemorySizes = new HashMap<>();
//
//        final DataSourceProperties dataSourceProperties = this.multitenancyProperties.getInformationSchemaProperties();
//        DataSourceBuilder factory = DataSourceBuilder
//                .create(this.multitenancyProperties.getDataSource().getClassLoader())
////                .driverClassName(this.multitenancyProperties.getDataSource().getDriverClassName())
//                .username(dataSourceProperties.getUsername())
//                .password(dataSourceProperties.getPassword())
//                .url(dataSourceProperties.getUrl());
//
//        DataSource dataSource = factory.build();
//        Connection conn = null;
//        try {
//            //verify for a valid datasource
//            if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
//                ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getPoolProperties().setMaxActive(5);
//            }
//            conn = dataSource.getConnection();
//            conn.isValid(2);
//            Statement stmt = conn.createStatement();
//            String query = "select table_schema, sum((data_length+index_length)/1024/1024) AS MB from information_schema.tables group by 1;";
//            ResultSet rs = stmt.executeQuery(query);
//            while (rs.next()) {
//                if (activeSandboxIds.contains(rs.getString(1))) {
//                    sandboxMemorySizes.put(rs.getString(1), Double.parseDouble(rs.getString(2)));
//                }
//
//            }
//            return sandboxMemorySizes;
//        } catch (SQLException e) {
//
//        } finally {
//            // Always make sure result sets and statements are closed, and the connection is returned to the pool
//            if (conn != null) {
//                try {
//                    conn.close();
//
//                } catch (SQLException e) {
//                    LOGGER.error("Error closing connection pool", e);
//                }
//            }
//        }
//        return null;
//    }
//
//    public HashMap<String, Double> memoryAllSandboxesOfUser(List<String> sandboxIds) {
//        HashMap<String, Double> sandboxMemorySizes = new HashMap<>();
//
//        final DataSourceProperties dataSourceProperties = this.multitenancyProperties.getInformationSchemaProperties();
//        DataSourceBuilder factory = DataSourceBuilder
//                .create(this.multitenancyProperties.getDataSource().getClassLoader())
////                .driverClassName(this.multitenancyProperties.getDataSource().getDriverClassName())
//                .username(dataSourceProperties.getUsername())
//                .password(dataSourceProperties.getPassword())
//                .url(dataSourceProperties.getUrl());
//
//        DataSource dataSource = factory.build();
//        Connection conn = null;
//        try {
//            //verify for a valid datasource
//            if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
//                ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getPoolProperties().setMaxActive(5);
//            }
//            conn = dataSource.getConnection();
//            conn.isValid(2);
//            Statement stmt = conn.createStatement();
//            for (String id : sandboxIds) {
//                String query = "select table_schema, sum((data_length+index_length)/1024/1024) AS MB from information_schema.tables " +
//                        "WHERE table_schema REGEXP 'hspc_[0-9]_" + id + "' group by 1;";
//                ResultSet rs = stmt.executeQuery(query);
//                while (rs.next()) {
//                    sandboxMemorySizes.put(rs.getString(1), Double.parseDouble(rs.getString(2)));
//                }
//            }
//
//            return sandboxMemorySizes;
//        } catch (SQLException e) {
//
//        } finally {
//            // Always make sure result sets and statements are closed, and the connection is returned to the pool
//            if (conn != null) {
//                try {
//                    conn.close();
//
//                } catch (SQLException e) {
//                    LOGGER.error("Error closing connection pool", e);
//                }
//            }
//        }
//        return null;
//    }

}