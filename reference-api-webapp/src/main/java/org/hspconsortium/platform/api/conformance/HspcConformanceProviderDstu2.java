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

package org.hspconsortium.platform.api.conformance;

import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.JpaConformanceProviderDstu2;
import ca.uhn.fhir.model.dstu2.composite.MetaDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Conformance;
import ca.uhn.fhir.rest.server.RestfulServer;
import org.hspconsortium.platform.api.fhir.repository.MetadataRepositoryDstu2;

import javax.servlet.http.HttpServletRequest;

public class HspcConformanceProviderDstu2 extends JpaConformanceProviderDstu2 {
    private MetadataRepositoryDstu2 metadataRepository;

    public HspcConformanceProviderDstu2(RestfulServer theRestfulServer, IFhirSystemDao<Bundle, MetaDt> theSystemDao, DaoConfig theDaoConfig, MetadataRepositoryDstu2 metadataRepository) {
        super(theRestfulServer, theSystemDao, theDaoConfig);
        this.metadataRepository = metadataRepository;
    }

    public void setMetadataRepository(MetadataRepositoryDstu2 metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    @Override
    public Conformance getServerConformance(HttpServletRequest request) {
        Conformance conformance = super.getServerConformance(request);
        return this.metadataRepository.addConformance(conformance);
    }
}