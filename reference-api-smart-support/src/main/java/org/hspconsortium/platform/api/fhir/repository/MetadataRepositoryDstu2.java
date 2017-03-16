package org.hspconsortium.platform.api.fhir.repository;

import ca.uhn.fhir.model.dstu2.resource.Conformance;

public interface MetadataRepositoryDstu2 {
    Conformance addConformance(Conformance conformance);
}
