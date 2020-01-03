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

package org.hspconsortium.platform.api.conformance;

import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.dstu3.JpaConformanceProviderDstu3;
import ca.uhn.fhir.rest.server.RestfulServer;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CapabilityStatement;
import org.hl7.fhir.dstu3.model.Meta;
import org.hspconsortium.platform.api.controller.MultitenantHapiFhirController;
import org.hspconsortium.platform.api.fhir.repository.MetadataRepositoryStu3;

import javax.servlet.http.HttpServletRequest;

public class HspcConformanceProviderStu3 extends JpaConformanceProviderDstu3 {

    private MetadataRepositoryStu3 metadataRepository;

    public HspcConformanceProviderStu3(RestfulServer theRestfulServer, IFhirSystemDao<Bundle, Meta> theSystemDao, DaoConfig theDaoConfig, MetadataRepositoryStu3 metadataRepository) {
        super(theRestfulServer, theSystemDao, theDaoConfig);
        this.metadataRepository = metadataRepository;
    }

    public void setMetadataRepository(MetadataRepositoryStu3 metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    @Override
    public CapabilityStatement getServerConformance(HttpServletRequest request) {
        CapabilityStatement capabilityStatement = super.getServerConformance(request);
        if (request.getRequestURI().split("/")[2].equals("data")) { // If someone can think of something better, please implement
            return this.metadataRepository.addCapabilityStatement(capabilityStatement);
        }
        return capabilityStatement;
    }
}
