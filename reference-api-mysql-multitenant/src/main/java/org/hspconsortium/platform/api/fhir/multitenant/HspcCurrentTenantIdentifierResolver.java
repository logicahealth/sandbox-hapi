package org.hspconsortium.platform.api.fhir.multitenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
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
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

		if (requestAttributes != null) {
			String identifier = (String) requestAttributes.getAttribute(MultitenantDatabaseProperties.CURRENT_TENANT_IDENTIFIER, RequestAttributes.SCOPE_REQUEST);
			if (identifier != null) {
				return identifier;
			}
		}
		return multitenantDatabaseProperties.getDefaultTenantId();
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}
}
