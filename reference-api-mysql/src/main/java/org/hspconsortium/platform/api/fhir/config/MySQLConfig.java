/**
 *  * #%L
 *  *
 *  * %%
 *  * Copyright (C) 2014-2019 Healthcare Services Platform Consortium
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

package org.hspconsortium.platform.api.fhir.config;

import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.search.LuceneSearchMappingFactory;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import ca.uhn.fhir.rest.server.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.util.Version;
//import org.flywaydb.core.Flyway;
import org.hibernate.cfg.Environment;
import org.hspconsortium.platform.api.fhir.DatabaseProperties;
import org.hspconsortium.platform.api.fhir.util.TAR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
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

    @Value("${hspc.platform.api.fhir.hibernate.indexSourceUrl:}")
    private String indexSourceUrl;

    @Value("${hspc.platform.api.fhir.allowExternalReferences:true}")
    private boolean allowExternalReferences;

//    @Value("${flyway.locations}")
//    private String flywayLocations;

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
        retVal.setReuseCachedSearchResultsForMillis(null);
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
        DataSource dataSource = factory.build();

        if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getPoolProperties().setTestOnBorrow(true);
            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getPoolProperties().setValidationQuery("SELECT 1");
        }

        // migrate the database manually because of a circular bean problem
        // with multi-tenant datasources
//        Flyway flyway = new Flyway();
//        flyway.setLocations(flywayLocations);
//        flyway.setDataSource(dataSource);
//        flyway.migrate();

        return dataSource;
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

    @Bean
    @Autowired
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean retVal = new LocalContainerEntityManagerFactoryBean();
        retVal.setDataSource(dataSource);
        retVal.setPackagesToScan("ca.uhn.fhir.jpa.entity");
        retVal.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
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
        hibernateProps.put(Environment.HBM2DDL_AUTO, "none");
        hibernateProps.put(Environment.STATEMENT_BATCH_SIZE, "20");
        hibernateProps.put(Environment.USE_MINIMAL_PUTS, "false");
        hibernateProps.put(Environment.ORDER_INSERTS, "false");
        hibernateProps.put(Environment.ORDER_UPDATES, "false");
        hibernateProps.put(Environment.USE_QUERY_CACHE, "false");
        hibernateProps.put(Environment.USE_SECOND_LEVEL_CACHE, "false");
        hibernateProps.put(Environment.USE_STRUCTURED_CACHE, "false");
        hibernateProps.put(Environment.DIALECT, hibernateDialect);
        hibernateProps.put(Environment.USE_MINIMAL_PUTS, "false");
        hibernateProps.put("hibernate.search.model_mapping", LuceneSearchMappingFactory.class.getName());
        hibernateProps.put("hibernate.search.default.indexBase", luceneBase);
        hibernateProps.put("hibernate.search.lucene_version", Version.LATEST);
        hibernateProps.put("hibernate.search.default.directory_provider", "filesystem");

        loadIndexFiles();

        return hibernateProps;
    }

    private void loadIndexFiles() {
        LOGGER.info("loadIndexFiles()");
        // download the index files if they don't exist already
        if (StringUtils.isNotEmpty(luceneBase) && StringUtils.isNotEmpty(indexSourceUrl)) {
            String fromFile = indexSourceUrl;
            String[] parts = indexSourceUrl.split("/");
            String fromFileName = parts[parts.length-1];
            String toFile = luceneBase + "/" + fromFileName;
            String tarFile = luceneBase + "/indexes.tar";
            // download the index files if they don't exist already
            File previousFile = new File(toFile);
            if (!previousFile.exists()) {
                // fetch
                try {
                    LOGGER.warn(toFile + " has not been loaded, proceeding with load");
                    //connectionTimeout, readTimeout = 120 seconds
                    int timeout = 120 * 10000;
                    LOGGER.warn("Downloading " + fromFile);
                    FileUtils.copyURLToFile(new URL(fromFile), new File(toFile), timeout, timeout);
                    LOGGER.warn("Downloading " + fromFile + " complete");

                    LOGGER.warn("Unzipping " + toFile );
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
                    LOGGER.warn("Unzipping " + toFile + " complete");

                    LOGGER.warn("Untarring " + tarFile + " to " + luceneBase);
                    TAR.decompress(tarFile, new File(luceneBase));
                    LOGGER.warn("Untarring " + tarFile + " complete");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                LOGGER.warn(toFile + " has already been loaded, aborting load");
            }
        }
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
