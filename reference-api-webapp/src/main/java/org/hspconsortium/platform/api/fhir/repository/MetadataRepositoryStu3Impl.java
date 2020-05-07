/**
 *  * #%L
 *  *
 *  * %%
 *  * Copyright (C) 2014-2020 Healthcare Services Platform Consortium
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

import org.hl7.fhir.dstu3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Lazy
@Component
public class MetadataRepositoryStu3Impl implements MetadataRepositoryStu3 {

    @Autowired
    private MetadataRepositoryConfig metadataRepositoryConfig;

    @SuppressWarnings("Duplicates")
    @Override
    public CapabilityStatement addCapabilityStatement(CapabilityStatement capabilityStatement) {
        if (metadataRepositoryConfig.isSecured()) {
            List<CapabilityStatement.CapabilityStatementRestComponent> restList = capabilityStatement.getRest();

            CapabilityStatement.CapabilityStatementRestComponent rest = restList.get(0);
            CapabilityStatement.CapabilityStatementRestSecurityComponent restSecurity = rest.getSecurity();

            Extension conformanceExtension = new Extension(metadataRepositoryConfig.getUrisEndpointExtensionUrl());
            conformanceExtension.addExtension(new Extension("authorize", new UriType(metadataRepositoryConfig.getAuthorizeUrl())));
            conformanceExtension.addExtension(new Extension("token", new UriType(metadataRepositoryConfig.getTokenUrl())));
            conformanceExtension.addExtension(new Extension("register", new UriType(metadataRepositoryConfig.getRegistrationEndpointUrl())));
            conformanceExtension.addExtension(new Extension("launch-registration", new UriType(metadataRepositoryConfig.getLaunchRegistrationUrl())));

            restSecurity.addExtension(conformanceExtension);
            CodeableConcept codeableConcept = new CodeableConcept();
            Coding smartOnFhirCoding = new Coding("http://hl7.org/fhir/restful-security-service", "SMART-on-FHIR", "SMART-on-FHIR");
            codeableConcept.getCoding().add(smartOnFhirCoding);
            codeableConcept.setText("OAuth2 using SMART-on-FHIR profile (see http://docs.smarthealthit.org)");
            restSecurity.getService().add(codeableConcept);
        }

        return capabilityStatement;
    }
}
