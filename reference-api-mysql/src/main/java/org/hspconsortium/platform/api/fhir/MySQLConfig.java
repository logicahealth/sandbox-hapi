package org.hspconsortium.platform.api.fhir;

import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import ca.uhn.fhir.rest.server.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import org.apache.lucene.util.Version;
import org.flywaydb.core.Flyway;
import org.hibernate.cfg.Environment;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:/config/mysql.properties")
@EnableConfigurationProperties({JpaProperties.class, DatabaseProperties.class})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@Profile("default")
public class MySQLConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MySQLConfig.class);

    private DatabaseProperties databaseProperties;

    @Value("${hspc.platform.api.fhir.hibernate.dialect}")
    private String hibernateDialect;

    @Value("${hibernate.search.default.indexBase}")
    private String luceneBase;

    @Value("${hspc.platform.api.fhir.allowExternalReferences:true}")
    private boolean allowExternalReferences;

    @Value("${hspc.platform.messaging.subscriptionSupport.enabled:false}")
    private boolean subscriptionEnabled;

    @Value("${flyway.locations}")
    private String flywayLocations;

    @Autowired
    private JpaProperties jpaProperties;

    @Resource(type = DatabaseProperties.class)
    public MySQLConfig setDatabaseProperties(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
        return this;
    }

    protected DatabaseProperties getDatabaseProperties() {
        return databaseProperties;
    }

    /**
     * Configure FHIR properties around the the JPA server via this bean
     */
    @Bean()
    public DaoConfig daoConfig() {
        DaoConfig retVal = new DaoConfig();
        retVal.setAllowMultipleDelete(true);
        retVal.setAllowExternalReferences(allowExternalReferences);
        return retVal;
    }

    @Primary
    @Bean//(name = {"dataSource"})
    @Profile("default")
    public DataSource dataSource() {
        DataSourceProperties db = getDatabaseProperties().getDb();
        DataSourceBuilder factory = DataSourceBuilder
                .create(db.getClassLoader())
                .driverClassName(db.getDriverClassName())
                .username(db.getUsername())
                .password(db.getPassword())
                .url(db.getUrl());
        DataSource dataSource =  factory.build();

        if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getPoolProperties().setTestOnBorrow(true);
            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getPoolProperties().setValidationQuery("SELECT 1");
        }

        // migrate the database manually because of a circular bean problem
        // with multi-tenant datasources
        Flyway flyway = new Flyway();
        flyway.setLocations(flywayLocations);
        flyway.setDataSource(dataSource);
        flyway.migrate();

        return dataSource;
    }

    @Bean
    @Autowired
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean retVal = new LocalContainerEntityManagerFactoryBean();
        retVal.setDataSource(dataSource);
        retVal.setPackagesToScan("ca.uhn.fhir.jpa.entity");
        retVal.setPersistenceProvider(new HibernatePersistenceProvider());
        retVal.setJpaProperties(jpaProperties(dataSource));
        retVal.afterPropertiesSet();
        return retVal;
    }

    @Autowired
    protected Properties jpaProperties(DataSource dataSource) {
        Properties hibernateProps = new Properties();
        // defaults
        hibernateProps.put(Environment.SHOW_SQL, "false");
        hibernateProps.put(Environment.FORMAT_SQL, "true");
        hibernateProps.put(Environment.STATEMENT_BATCH_SIZE, "20");
        hibernateProps.put(Environment.USE_MINIMAL_PUTS, "false");
        hibernateProps.put(Environment.ORDER_INSERTS, "false");
        hibernateProps.put(Environment.ORDER_UPDATES, "false");
        hibernateProps.put(Environment.USE_QUERY_CACHE, "false");
        hibernateProps.put(Environment.USE_SECOND_LEVEL_CACHE, "false");
        hibernateProps.put(Environment.USE_STRUCTURED_CACHE, "false");
        hibernateProps.put(Environment.DIALECT, hibernateDialect);
        hibernateProps.put(Environment.USE_MINIMAL_PUTS, "false");
        hibernateProps.put("hibernate.search.default.indexBase", luceneBase);
        hibernateProps.put("hibernate.search.lucene_version", Version.LATEST);
        hibernateProps.put("hibernate.search.default.directory_provider", "filesystem");
        // overrides
        hibernateProps.putAll(jpaProperties.getHibernateProperties(dataSource));
        return hibernateProps;
    }

    /**
     * Do some fancy logging to create a nice access log that has details about each incoming request.
     */
    public IServerInterceptor loggingInterceptor() {
        LoggingInterceptor retVal = new LoggingInterceptor();
        retVal.setLoggerName("fhirtest.access");
        retVal.setMessageFormat(
                "Path[${servletPath}] Source[${requestHeader.x-forwarded-for}] Operation[${operationType} ${operationName} ${idOrResourceName}] UA[${requestHeader.user-agent}] Params[${requestParameters}] ResponseEncoding[${responseEncodingNoDefault}]");
        retVal.setLogExceptions(true);
        retVal.setErrorMessageFormat("ERROR - ${requestVerb} ${requestUrl}");
        return retVal;
    }

    /**
     * This interceptor adds some pretty syntax highlighting in responses when a browser is detected
     */
    @Bean(autowire = Autowire.BY_TYPE)
    public IServerInterceptor responseHighlighterInterceptor() {
        return new ResponseHighlighterInterceptor();
    }

    @Bean()
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager retVal = new JpaTransactionManager();
        retVal.setEntityManagerFactory(entityManagerFactory);
        return retVal;
    }
}
