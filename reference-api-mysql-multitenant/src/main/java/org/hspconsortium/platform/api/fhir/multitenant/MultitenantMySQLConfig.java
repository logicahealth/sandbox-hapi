package org.hspconsortium.platform.api.fhir.multitenant;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hspconsortium.platform.api.fhir.DatabaseProperties;
import org.hspconsortium.platform.api.fhir.MySQLConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement()
@PropertySource({"classpath:/config/mysql.properties", "classpath:/config/multitenant-mysql.properties"})
@EnableConfigurationProperties({JpaProperties.class, MultitenantDatabaseProperties.class})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@Profile("multitenant")
public class MultitenantMySQLConfig extends MySQLConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultitenantMySQLConfig.class);

    @Autowired
    private MultiTenantConnectionProvider multiTenantConnectionProvider;

    @Autowired
    private CurrentTenantIdentifierResolver currentTenantIdentifierResolver;

    @Override
    @Resource(type = MultitenantDatabaseProperties.class)
    public MySQLConfig setDatabaseProperties(DatabaseProperties databaseProperties) {
        return super.setDatabaseProperties(databaseProperties);
    }

    @Bean(name = {"noSchemaDataSource"})
    public DataSource noSchemaDataSource() {
        // create a datasource that doesn't have a schema in the url
        DataSourceProperties db = getDatabaseProperties().getDb();

        String urlNoSchema = null;
        for (String schema : db.getSchema()) {
            if (db.getUrl().contains(schema.toLowerCase())) {
                urlNoSchema = db.getUrl().substring(0, db.getUrl().indexOf(schema.toLowerCase()));
                break;
            }
        }

        if (urlNoSchema == null) {
            throw new RuntimeException("Unable to create noSchemaDataSource for " + db.getUrl());
        }

        DataSourceBuilder factory = DataSourceBuilder
                .create(db.getClassLoader())
                .driverClassName(db.getDriverClassName())
                .username(db.getUsername())
                .password(db.getPassword())
                .url(urlNoSchema);
        DataSource dataSource = factory.build();

        if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getPoolProperties().setTestOnBorrow(true);
            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getPoolProperties().setValidationQuery("SELECT 1");
        }

        // try it out
        try {
            Connection connection = dataSource.getConnection();
        } catch (SQLException e) {
            LOGGER.error("Error creating noSchemaDataSource", e);
            throw new RuntimeException(e);
        }
        return dataSource;
    }

    @Autowired
    protected Properties jpaProperties(DataSource dataSource) {
        Properties multitenantProperties = super.jpaProperties(dataSource);
        multitenantProperties.put(Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
        multitenantProperties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        multitenantProperties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);
        return multitenantProperties;
    }

}
