package org.hspconsortium.platform.api.conformance;

import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.r4.JpaConformanceProviderR4;
import ca.uhn.fhir.rest.server.RestfulServer;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.Meta;
import org.hspconsortium.platform.api.fhir.repository.MetadataRepositoryR4;

import javax.servlet.http.HttpServletRequest;

public class HspcConformanceProviderR4 extends JpaConformanceProviderR4 {
    private MetadataRepositoryR4 metadataRepository;

    public HspcConformanceProviderR4(RestfulServer theRestfulServer, IFhirSystemDao<Bundle, Meta> theSystemDao, DaoConfig theDaoConfig, MetadataRepositoryR4 metadataRepository) {
        super(theRestfulServer, theSystemDao, theDaoConfig);
        this.metadataRepository = metadataRepository;
    }

    public void setMetadataRepository(MetadataRepositoryR4 metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    @Override
    public CapabilityStatement getServerConformance(HttpServletRequest request) {
        CapabilityStatement capabilityStatement = super.getServerConformance(request);
        return this.metadataRepository.addCapabilityStatement(capabilityStatement);
    }
}
