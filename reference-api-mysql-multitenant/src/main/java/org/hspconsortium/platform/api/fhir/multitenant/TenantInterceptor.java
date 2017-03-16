package org.hspconsortium.platform.api.fhir.multitenant;

import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class TenantInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler)
			throws Exception {
		Map<String, Object> requestAttributes = (Map<String, Object>) req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		
		if (requestAttributes.containsKey("tenant")) {
			req.setAttribute(MultitenantDatabaseProperties.CURRENT_TENANT_IDENTIFIER, requestAttributes.get("tenant"));
		}

		if (requestAttributes.containsKey("schema_version")) {
			req.setAttribute(MultitenantDatabaseProperties.HSPC_SCHEMA_VERSION, requestAttributes.get("schema_version"));
		}
		return true;
	}
}
