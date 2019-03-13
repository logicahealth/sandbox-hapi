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

package org.hspconsortium.platform.api.fhir.repository;

import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.dstu2.composite.BoundCodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.Conformance;
import ca.uhn.fhir.model.dstu2.valueset.RestfulSecurityServiceEnum;
import ca.uhn.fhir.model.primitive.UriDt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Lazy
@Component
public class MetadataRepositoryDstu2Impl implements MetadataRepositoryDstu2 {

    @Autowired
    private MetadataRepositoryConfig metadataRepositoryConfig;

    @SuppressWarnings("Duplicates")
    @Override
    public Conformance addConformance(Conformance conformance){

        if (metadataRepositoryConfig.isSecured()) {
            List<Conformance.Rest> restList = conformance.getRest();
            Conformance.Rest rest = restList.get(0);
            Conformance.RestSecurity restSecurity = rest.getSecurity();

            ExtensionDt conformanceExtension = new ExtensionDt(false, metadataRepositoryConfig.getUrisEndpointExtensionUrl());
            conformanceExtension.addUndeclaredExtension(new ExtensionDt(false, "authorize", new UriDt(metadataRepositoryConfig.getAuthorizeUrl())));
            conformanceExtension.addUndeclaredExtension(new ExtensionDt(false, "token", new UriDt(metadataRepositoryConfig.getTokenUrl())));
            conformanceExtension.addUndeclaredExtension(new ExtensionDt(false, "register", new UriDt(metadataRepositoryConfig.getRegistrationEndpointUrl())));
            conformanceExtension.addUndeclaredExtension(new ExtensionDt(false, "launch-registration", new UriDt(metadataRepositoryConfig.getLaunchRegistrationUrl())));
            restSecurity.addUndeclaredExtension(conformanceExtension);

            BoundCodeableConceptDt<RestfulSecurityServiceEnum> boundCodeableConceptDt =
                    new BoundCodeableConceptDt<>(
                            RestfulSecurityServiceEnum.VALUESET_BINDER, RestfulSecurityServiceEnum.SMART_ON_FHIR);
            boundCodeableConceptDt.setText("OAuth2 using SMART-on-FHIR profile (see http://docs.smarthealthit.org)");
            restSecurity.getService().add(boundCodeableConceptDt);
        }

        return conformance;
    }
}
