package org.hspconsortium.platform.api.fhir.multitenant;

import org.apache.commons.lang3.Validate;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Component
public class ConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    @Autowired
    private DataSourceRepository hspcDataSourceRepository;

    @Autowired
    private MultitenantDatabaseProperties multitenantDatabaseProperties;

    @Autowired(required = true)
    @Qualifier("noSchemaDataSource")
    public DataSource noSchemaDataSource;

    @Override
    protected DataSource selectAnyDataSource() {
        return noSchemaDataSource;
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        Validate.notNull(tenantIdentifier);
        DataSource result = hspcDataSourceRepository.getDataSource(MultitenantDatabaseProperties.DEFAULT_HSPC_SCHEMA_VERSION, tenantIdentifier);
        return result;
    }
}
