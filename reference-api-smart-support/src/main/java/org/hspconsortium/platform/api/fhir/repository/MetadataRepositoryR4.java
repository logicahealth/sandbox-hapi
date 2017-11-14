package org.hspconsortium.platform.api.fhir.repository;

import org.hl7.fhir.r4.model.CapabilityStatement;

public interface MetadataRepositoryR4 {
    CapabilityStatement addCapabilityStatement(CapabilityStatement capabilityStatement);
}
