package org.hspconsortium.platform.api.oauth2;

import org.apache.commons.lang3.Validate;
import org.hspconsortium.platform.api.fhir.repository.MetadataRepositoryConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Configuration
@EnableResourceServer
@Profile("default")
public class OAuth2ResourceConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private MetadataRepositoryConfig metadataRepositoryConfig;

    public static final String SECURITY_MODE_OPEN = "open";

    public static final String SECURITY_MODE_SECURED = "secured";

    public static final String SECURITY_MODE_MOCK = "mock";

    public static final String NO_ENDPOINT = "none";

    @Value("${hspc.platform.api.security.mode}")
    private String securityMode;

    @Value("${hspc.platform.api.fhir.contextPath:data}")
    private String fhirContextPath;

    @Value("${hspc.platform.api.fhir.openContextPath:" + NO_ENDPOINT + "}")
    private String openContextPath;

    /**
     * Additional ANT patters to permit in Spring security in the format:
     * /endpoint1/*|PUT,/endpoint2/**|GET
     * If all HttpMethods are permissible, use HttpMethod value of empty: [/endpoint1|,...]
     */
    @Value("${hspc.platform.api.fhir.additionalPermittedEndpointPairs:" + NO_ENDPOINT + "}")
    private String[] additionalPermittedEndpointPairs;

    public String getSecurityMode() {
        return securityMode;
    }

    public String getFhirContextPath() {
        return fhirContextPath;
    }

    public String getOpenContextPath() {
        return openContextPath;
    }

    @Bean
    public AccessTokenConverter accessTokenConverter() {
        return new HspcAccessTokenConverter();
    }

    @Bean
    public ResourceServerTokenServices remoteTokenServices(
            final @Value("${hspc.platform.api.oauth2.clientId}") String clientId,
            final @Value("${hspc.platform.api.oauth2.clientSecret}") String clientSecret) {

        if (getSecurityMode().equalsIgnoreCase(SECURITY_MODE_MOCK)) {
            return null;
        }
        final RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        remoteTokenServices.setCheckTokenEndpointUrl(metadataRepositoryConfig.getTokenCheckUrl());
        remoteTokenServices.setClientId(clientId);
        remoteTokenServices.setClientSecret(clientSecret);
        remoteTokenServices.setAccessTokenConverter(accessTokenConverter());
        return remoteTokenServices;
    }

    @Bean
    SecurityFilterChainPostProcessor securityFilterChainPostProcessor() {
        return new SecurityFilterChainPostProcessor();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public InvalidMediaTypeFilter invalidMediaTypeFilter() {
        return new InvalidMediaTypeFilter();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public CorsFilter customCorsFilter() {
        return new CorsFilter();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        Validate.isTrue(fhirContextPath != null, "Fhir context path not specified");

        // add the corsFilter before the ChannelProcessingFilter
        CorsFilter corsFilter = customCorsFilter();
        http.addFilterBefore(corsFilter, ChannelProcessingFilter.class);

        // add the invalidMediaTypeFilter before the CorsFilter
        // (otherwise the CorsFilter will throw an exception for invalid media type)
        InvalidMediaTypeFilter invalidMediaTypeFilter = invalidMediaTypeFilter();
        http.addFilterBefore(invalidMediaTypeFilter, CorsFilter.class);

        configureHttpEndpoints(http);
    }

    protected void configureHttpEndpoints(HttpSecurity http) throws Exception {
        configureCustomPaths(http);

        configureSystemEndpoints(http);

        configureSandboxEndpoints(http);

        configureFhirContextPath(http);

        configureOpenContextPath(http);

        if (getSecurityMode().equalsIgnoreCase(SECURITY_MODE_MOCK)) {
            List<RequestMatcher> requestMatchers = new ArrayList<RequestMatcher>();
            requestMatchers.add(new AntPathRequestMatcher("/**"));

            http
                    .requestMatcher(new OrRequestMatcher(requestMatchers))
                    .authorizeRequests()
                    .antMatchers("/**").permitAll();

        } else {

            // after configuring the open endpoints, all other endpoints require authentication
            http.authorizeRequests()
                    .anyRequest()
                    .authenticated();
        }
    }

    protected void configureCustomPaths(HttpSecurity http) throws Exception {
        // do nothing here, but allow for extensions to provide custom behavior
        // that occurs first
    }

    protected void configureSystemEndpoints(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/health").permitAll()
                .requestMatchers(
                        new AntPathRequestMatcher("/system/**", null),
                        // terminology proxy
                        new AntPathRequestMatcher("/terminology*", "GET"),
                        new AntPathRequestMatcher("/terminology/**", "GET")
                        )
                .permitAll();
        if (additionalPermittedEndpointPairs != null && additionalPermittedEndpointPairs.length > 0) {
            if (!NO_ENDPOINT.equals(additionalPermittedEndpointPairs[0])) {
                for (String endpointMethodPair : additionalPermittedEndpointPairs) {
                    String[] endpointMethod = endpointMethodPair.split("\\|");
                    switch (endpointMethod.length) {
                        case 1:
                            http
                                    .authorizeRequests()
                                    .antMatchers(endpointMethod[0])
                                    .permitAll();
                            break;
                        case 2:
                            http
                                    .authorizeRequests()
                                    .antMatchers(HttpMethod.valueOf(endpointMethod[1]), endpointMethod[0])
                                    .permitAll();
                            break;
                        default:
                            throw new RuntimeException("Value [" + endpointMethodPair + "] is not in the required format of [endpoint|HttpMethod] ex: [http://example.com|GET]");
                    }
                }
            }
        }
    }

    protected void configureSandboxEndpoints(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/sandbox/**")
                .permitAll();
    }

    protected void configureFhirContextPath(HttpSecurity http) throws Exception {
        if (fhirContextPath != null && fhirContextPath.length() > 0) {
            switch (getSecurityMode()) {
                case (SECURITY_MODE_OPEN):
                    configureOpenFHIRServer(http, fhirContextPath);
                    break;
                case (SECURITY_MODE_SECURED):
                    configureSecuredFHIRServer(http, fhirContextPath);
                    break;
                case (SECURITY_MODE_MOCK):
                    configureOpenFHIRServer(http, fhirContextPath);
                    break;
                default:
                    throw new RuntimeException("Security mode must be either open or secured");
            }
        }
    }

    protected void configureOpenContextPath(HttpSecurity http) throws Exception {
        if (openContextPath != null && openContextPath.length() > 0 && !NO_ENDPOINT.equals(openContextPath)) {
            configureOpenFHIRServer(http, openContextPath);
        }
    }

    protected void configureOpenFHIRServer(HttpSecurity http, String fhirPath) throws Exception {
        if (fhirPath != null) {
            permitRegex(http, "\\/" + fhirPath, null);
            permitRegex(http, "\\/" + fhirPath + "\\/.*", null);
        }
    }

    protected void configureSecuredFHIRServer(HttpSecurity http, String fhirPath) throws Exception {
        if (fhirPath != null && fhirPath.length() > 0) {
            // This level of security says that any other requests (all requests for FHIR resources)
            // must be authenticated.  It does not determine if the user has access to the specific
            // data according to scope and user role. That more granular level of provisioning should
            // be handled by an interceptor
            permitRegex(http, "\\/" + fhirPath + "\\/metadata", "GET");
            permitRegex(http, "\\/" + fhirPath + "\\/metadata.*", "GET");
            permitRegex(http, "\\/" + fhirPath + "\\/_services\\/smart\\/.*", null);
        }
    }

    public void permitAntPath(HttpSecurity http, String antPath, String httpMethod) throws Exception {
        http
                .authorizeRequests()
                .requestMatchers(
                        new AntPathRequestMatcher(antPath, httpMethod)
                )
                .permitAll();
    }

    public void permitRegex(HttpSecurity http, String pathExpression, String httpMethod) throws Exception {
        http
                .authorizeRequests()
                .requestMatchers(
                        new RegexRequestMatcher(pathExpression, httpMethod)
                )
                .permitAll();
    }

    static class SecurityFilterChainPostProcessor implements BeanPostProcessor {
        private static final String SECURITY_MODE_MOCK = "mock";

        @Value("${hspc.platform.api.security.mode}")
        private String securityMode;

        private String getSecurityMode() {
            return securityMode;
        }

        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

            if (getSecurityMode().equalsIgnoreCase(SECURITY_MODE_MOCK) && beanName.equals("springSecurityFilterChain")) {
                FilterChainProxy proxy = (FilterChainProxy) bean;
                List<SecurityFilterChain> chains = proxy.getFilterChains();
                for (SecurityFilterChain fc : chains) {
                    DefaultSecurityFilterChain dfc = (DefaultSecurityFilterChain) fc;
                    List<Filter> filters = dfc.getFilters();
                    Iterator i = filters.iterator();

                    while (i.hasNext()) {
                        if (i.next() instanceof OAuth2AuthenticationProcessingFilter) {
                            i.remove();
                        }
                    }
                }
            }
            return bean;
        }

        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }
    }
}