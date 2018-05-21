package org.hspconsortium.platform.api.security;

import org.hspconsortium.platform.api.oauth2.OAuth2ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@Configuration
@EnableResourceServer
@Profile("multitenant")
public class MultitenantOAuth2ResourceConfig extends OAuth2ResourceConfig {

    private static final String TENANT_PATTERN = "\\w+\\/";

    @Autowired
    private TenantInfoRequestMatcher tenantInfoRequestMatcher;

    @Override
    protected void configureSandboxEndpoints(HttpSecurity http) throws Exception {
        permitRegex(http, "\\/" + TENANT_PATTERN + "sandbox", null);
        permitRegex(http, "\\/" + TENANT_PATTERN + "sandbox\\/.*", null);
    }

    @Override
    protected void configureOpenContextPath(HttpSecurity http) throws Exception {
        tenantInfoRequestMatcher.setOpenContextPath(super.getOpenContextPath());
        http
                .authorizeRequests()
                .requestMatchers(tenantInfoRequestMatcher)
                .permitAll();
    }

    @Override
    protected void configureOpenFHIRServer(HttpSecurity http, String nonTenantFhirContextPath) throws Exception {
        if (nonTenantFhirContextPath != null && nonTenantFhirContextPath.length() > 0) {
            // for example, /team1/data/metadata
            String multitenantFhirPath = TENANT_PATTERN + nonTenantFhirContextPath;
            super.configureOpenFHIRServer(http, multitenantFhirPath);
        }
    }

    @Override
    protected void configureSecuredFHIRServer(HttpSecurity http, String nonTenantFhirContextPath) throws Exception {
        if (nonTenantFhirContextPath != null && nonTenantFhirContextPath.length() > 0) {
            // for example, /team1/data/metadata
            String multitenantFhirPath = TENANT_PATTERN + nonTenantFhirContextPath;
            super.configureSecuredFHIRServer(http, multitenantFhirPath);
        }
    }
}