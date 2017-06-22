package org.hspconsortium.platform.api.conformance;

import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.dstu3.JpaConformanceProviderDstu3;
import ca.uhn.fhir.rest.server.RestfulServer;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CapabilityStatement;
import org.hl7.fhir.dstu3.model.Meta;
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
        return this.metadataRepository.addCapabilityStatement(capabilityStatement);
    }
}
