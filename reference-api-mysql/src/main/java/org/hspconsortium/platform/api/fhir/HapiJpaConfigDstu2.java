package org.hspconsortium.platform.api.fhir;

import ca.uhn.fhir.jpa.config.BaseJavaConfigDstu2;
import ca.uhn.fhir.jpa.search.ISearchCoordinatorSvc;
import org.hspconsortium.platform.api.fhir.multitenant.search.MultiTenantSearchCoordinatorSvcImpl;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dstu2")
public class HapiJpaConfigDstu2 extends BaseJavaConfigDstu2 {
    // this is broken in the current release of HAPI.
//    @Bean(autowire = Autowire.BY_TYPE)
//    public IServerInterceptor subscriptionSecurityInterceptor() {
//        return new SubscriptionsRequireManualActivationInterceptorDstu2();
//    }

    @Bean(autowire = Autowire.BY_TYPE)
    @Override
    public ISearchCoordinatorSvc searchCoordinatorSvc() {
        return new MultiTenantSearchCoordinatorSvcImpl();
    }
}
