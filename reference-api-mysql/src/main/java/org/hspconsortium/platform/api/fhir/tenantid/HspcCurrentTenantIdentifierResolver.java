package org.hspconsortium.platform.api.fhir.tenantid;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hspconsortium.platform.api.fhir.MultitenantDatabaseProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HspcCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Autowired
    private MultitenantDatabaseProperties multitenantDatabaseProperties;

    @Override
    public String resolveCurrentTenantIdentifier() {

        if (TenantContextHolder.getTenant() != null) {
            String tenantId = TenantContextHolder.getTenant();
            if (tenantId != null) {
                return tenantId;
            }
        }

        return multitenantDatabaseProperties.getDefaultTenantId();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
