package org.hspconsortium.platform.api.fhir.multitenant.config;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hspconsortium.platform.api.fhir.DatabaseProperties;
import org.hspconsortium.platform.api.fhir.MySQLConfig;
import org.hspconsortium.platform.api.fhir.multitenant.MultitenantDatabaseProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;
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

    @Autowired
    protected Properties jpaProperties(DataSource dataSource) {
        Properties multitenantProperties = super.jpaProperties(dataSource);
        multitenantProperties.put(Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
        multitenantProperties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        multitenantProperties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);
        return multitenantProperties;
    }

}
