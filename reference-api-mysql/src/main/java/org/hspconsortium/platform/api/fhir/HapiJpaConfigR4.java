package org.hspconsortium.platform.api.fhir;


import ca.uhn.fhir.jpa.config.BaseJavaConfigR4;
import ca.uhn.fhir.jpa.search.ISearchCoordinatorSvc;
import ca.uhn.fhir.jpa.subscription.resthook.SubscriptionRestHookInterceptor;
import ca.uhn.fhir.jpa.util.SubscriptionsRequireManualActivationInterceptorR4;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import org.hspconsortium.platform.api.fhir.multitenant.search.MultiTenantSearchCoordinatorSvcImpl;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("r4")
@Configuration
public class HapiJpaConfigR4 extends BaseJavaConfigR4 {
    @Bean(autowire = Autowire.BY_TYPE)
    public IServerInterceptor subscriptionSecurityInterceptor() {
        return new SubscriptionsRequireManualActivationInterceptorR4();
    }

    @Bean
    public SubscriptionRestHookInterceptor subscriptionRestHookInterceptor() {
        return new SubscriptionRestHookInterceptor();
    }

    @Bean(autowire = Autowire.BY_TYPE)
    @Override
    public ISearchCoordinatorSvc searchCoordinatorSvc() {
        return new MultiTenantSearchCoordinatorSvcImpl();
    }
}
