package org.hspconsortium.platform.api.fhir.multitenant;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.flywaydb.core.Flyway;
import org.hspconsortium.platform.api.model.DataSet;
import org.hspconsortium.platform.api.model.Sandbox;
import org.hspconsortium.platform.api.service.SandboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DataSourceRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceRepository.class);

    private MultitenantDatabaseProperties multitenancyProperties;

    private GuavaCache datasourceCache;

    private SandboxService sandboxService;

    @Autowired
    public DataSourceRepository(MultitenantDatabaseProperties multitenancyProperties, SandboxService sandboxService) {
        this.multitenancyProperties = multitenancyProperties;
        this.sandboxService = sandboxService;

        Cache<Object, Object> cacheBuilder =
                CacheBuilder
                        .newBuilder()
                        .maximumSize(this.multitenancyProperties.getDataSourceCacheSize())
                        .build();

        datasourceCache = new GuavaCache("datasourceCache", cacheBuilder);
    }

    public DataSource getDataSource(String hspcSchemaVersion, String tenantIdentifier) {
        String key = tenantIdentifier + "~" + hspcSchemaVersion;

        org.springframework.cache.Cache.ValueWrapper valueWrapper = datasourceCache.get(key);
        if (valueWrapper != null) {
            return (DataSource) valueWrapper.get();
        }

        DataSource dataSource = createDataSource(hspcSchemaVersion, tenantIdentifier);

        if (dataSource != null) {
            LOGGER.info(String.format("Tenant '%s' maps to '%s' database url.", tenantIdentifier
                    , ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getPoolProperties().getUrl()));
        }

        if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getPoolProperties().setTestOnBorrow(true);
            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getPoolProperties().setValidationQuery("SELECT 1");
        }

        datasourceCache.put(key, dataSource);

        return dataSource;
    }

    private DataSource createDataSource(String hspcSchemaVersion, String tenant) {
        final DataSourceProperties dataSourceProperties = this.multitenancyProperties.getDataSource(hspcSchemaVersion, tenant);
        DataSourceBuilder factory = DataSourceBuilder
                .create(this.multitenancyProperties.getDb().getClassLoader())
                .driverClassName(this.multitenancyProperties.getDb().getDriverClassName())
                .username(dataSourceProperties.getUsername())
                .password(dataSourceProperties.getPassword())
                .url(dataSourceProperties.getUrl());

        DataSource dataSource = factory.build();
        Connection conn = null;
        try {
            //verify for a valid datasource
            conn = dataSource.getConnection();
            conn.isValid(2);

        } catch (SQLException e) {
            // if we are trying to retrieve the default tenant, but the schema doesn't exist
            if (tenant.equals(multitenancyProperties.getDefaultTenantId())) {
                sandboxService.save(new Sandbox(tenant, hspcSchemaVersion, true), DataSet.NONE);
                return createDataSource(hspcSchemaVersion, tenant);
            }

            LOGGER.error(String.format("Connection couldn't be established for tenant '%s' with '%s' database url."
                    , tenant
                    , dataSourceProperties.getUrl()));
            dataSource = null;
        } finally {
            // Always make sure result sets and statements are closed, and the connection is returned to the pool
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.error("Error closing connection pool", e);
                }
            }
        }
        return dataSource;
    }
}
