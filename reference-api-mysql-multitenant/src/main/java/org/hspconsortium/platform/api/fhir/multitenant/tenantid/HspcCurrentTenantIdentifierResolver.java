package org.hspconsortium.platform.api.fhir.multitenant.tenantid;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hspconsortium.platform.api.fhir.multitenant.MultitenantDatabaseProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

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
