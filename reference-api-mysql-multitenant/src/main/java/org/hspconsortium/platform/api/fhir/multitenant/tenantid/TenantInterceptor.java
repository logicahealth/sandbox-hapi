package org.hspconsortium.platform.api.fhir.multitenant.tenantid;

import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class TenantInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse res, Object handler)
            throws Exception {
        
        Map<String, Object> requestAttributes = (Map<String, Object>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (requestAttributes.containsKey("tenant")) {
            TenantContextHolder.setTenant(requestAttributes.get("tenant").toString());
        }

        return true;
    }
}
