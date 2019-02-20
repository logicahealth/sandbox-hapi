package org.hspconsortium.platform.api.fhir.config;

import ca.uhn.fhir.jpa.config.BaseJavaConfigDstu3;
import ca.uhn.fhir.jpa.dao.DaoMethodOutcome;
import ca.uhn.fhir.jpa.dao.IFhirResourceDaoValueSet;
import ca.uhn.fhir.jpa.dao.dstu3.FhirResourceDaoValueSetDstu3;
import ca.uhn.fhir.jpa.search.ISearchCoordinatorSvc;
import ca.uhn.fhir.jpa.util.SubscriptionsRequireManualActivationInterceptorDstu3;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.hspconsortium.platform.api.fhir.multitenant.search.MultiTenantSearchCoordinatorSvcImpl;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Profile("stu3")
@Configuration
public class HapiJpaConfigStu3 extends BaseJavaConfigDstu3 {
    @Bean(autowire = Autowire.BY_TYPE)
    public IServerInterceptor subscriptionSecurityInterceptor() {
        return new SubscriptionsRequireManualActivationInterceptorDstu3();
    }

    @Bean(autowire = Autowire.BY_TYPE)
    @Override
    public ISearchCoordinatorSvc searchCoordinatorSvc() {
        return new MultiTenantSearchCoordinatorSvcImpl();
    }

    // Temporary workaround until HAPI fixes ValueSets
    @Bean(name = "myValueSetDaoDstu3", autowire = Autowire.BY_NAME)
    @Lazy
    public IFhirResourceDaoValueSet<org.hl7.fhir.dstu3.model.ValueSet, Coding, CodeableConcept> daoValueSetDstu3() {
        MyFhirResourceDaoValueSetDstu3 retVal = new MyFhirResourceDaoValueSetDstu3();
        retVal.setResourceType(org.hl7.fhir.dstu3.model.ValueSet.class);
        retVal.setContext(fhirContextDstu3());
        return retVal;
    }

    public static class MyFhirResourceDaoValueSetDstu3 extends FhirResourceDaoValueSetDstu3  {
        @Autowired
        private PlatformTransactionManager myTxManager;

        @Override
        public DaoMethodOutcome create(ValueSet theResource, RequestDetails theRequestDetails) {
            return new TransactionTemplate(myTxManager).execute(t -> {
                return super.create(theResource, theRequestDetails);
            });
        }

        @Override
        public DaoMethodOutcome create(ValueSet theResource, String theIfNoneExist, RequestDetails theRequestDetails) {
            return new TransactionTemplate(myTxManager).execute(t -> {
                return super.create(theResource, theIfNoneExist, theRequestDetails);
            });
        }
    }
}
