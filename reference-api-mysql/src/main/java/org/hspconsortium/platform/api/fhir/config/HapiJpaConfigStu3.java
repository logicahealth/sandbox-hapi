/**
 *  * #%L
 *  *
 *  * %%
 *  * Copyright (C) 2014-2019 Healthcare Services Platform Consortium
 *  * %%
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  * #L%
 */

package org.hspconsortium.platform.api.fhir.config;

import ca.uhn.fhir.jpa.config.BaseJavaConfigDstu3;
import ca.uhn.fhir.jpa.search.ISearchCoordinatorSvc;
import ca.uhn.fhir.jpa.util.SubscriptionsRequireManualActivationInterceptorDstu3;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hspconsortium.platform.api.fhir.multitenant.search.MultiTenantSearchCoordinatorSvcImpl;
import org.opencds.cqf.providers.FHIRMeasureResourceProvider;
import org.opencds.cqf.providers.JpaDataProvider;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.util.List;

//import org.hspconsortium.platform.api.fhir.util.HspcFhirInstanceValidator;

@Profile("stu3")
@Configuration
public class HapiJpaConfigStu3 extends BaseJavaConfigDstu3 {
    @Bean(autowire = Autowire.BY_TYPE)
    public IServerInterceptor subscriptionSecurityInterceptor() {
        return new SubscriptionsRequireManualActivationInterceptorDstu3();
    }

    @Bean(autowire = Autowire.BY_TYPE)
//    @Override
    public ISearchCoordinatorSvc searchCoordinatorSvc() {
        return new MultiTenantSearchCoordinatorSvcImpl();
    }

//    @Override
//    @Bean(
//            name = {"myInstanceValidatorDstu3"}
//    )
//    @Lazy
//    public IValidatorModule instanceValidatorDstu3() {
//        FhirInstanceValidator val = new HspcFhirInstanceValidator();
//        val.setBestPracticeWarningLevel(IResourceValidator.BestPracticeWarningLevel.Warning);
//        val.setValidationSupport(this.validationSupportChainDstu3());
//        return val;
//    }

//    @Bean(name="myMeasureRpDstu3")
//    @Override
//    @Lazy
//    public ca.uhn.fhir.jpa.rp.dstu3.MeasureResourceProvider rpMeasureDstu3() {
////        MeasureResourceProviderHspc retVal;
////        retVal = new MeasureResourceProviderHspc();
////        retVal.setContext(fhirContextDstu3());
////        retVal.setDao(daoMeasureDstu3());
////        return retVal;
//        WebApplicationContext myAppCtx = HapiFhirServletContextHolder.getInstance().getMyAppCtx();
//        myAppCtx.refresh();
//        List<IResourceProvider> beans = myAppCtx.getBean("myResourceProvidersDstu3", List.class);
//        JpaDataProvider provider = new JpaDataProvider(beans);
//        FHIRMeasureResourceProvider retVal;
//        retVal = new FHIRMeasureResourceProvider(provider);
//        retVal.setContext(fhirContextDstu3());
//        retVal.setDao(daoMeasureDstu3());
//        return retVal;
//    }
}
