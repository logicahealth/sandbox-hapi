package org.hspconsortium.platform.api.search;

import ca.uhn.fhir.jpa.entity.TermCodeSystem;
import ca.uhn.fhir.jpa.entity.TermConcept;
import ca.uhn.fhir.jpa.entity.TermConceptParentChildLink;
import ca.uhn.fhir.jpa.model.entity.ResourceTable;
import ca.uhn.fhir.jpa.term.api.ITermDeferredStorageSvc;
import org.hl7.fhir.r4.model.ConceptMap;
import org.hl7.fhir.r4.model.ValueSet;

import java.util.List;

public class MockDstu2TermDeferredStorageSvc implements ITermDeferredStorageSvc {
    @Override
    public void saveDeferred() {
        throw new UnsupportedOperationException("This class is not implemented in DSTU2");
    }

    @Override
    public boolean isStorageQueueEmpty() {
        return true;
    }

    @Override
    public void setProcessDeferred(boolean theProcessDeferred) {
        throw new UnsupportedOperationException("This class is not implemented in DSTU2");
    }

    @Override
    public void addConceptToStorageQueue(TermConcept theConcept) {
        throw new UnsupportedOperationException("This class is not implemented in DSTU2");
    }

    @Override
    public void addConceptLinkToStorageQueue(TermConceptParentChildLink theConceptLink) {
        throw new UnsupportedOperationException("This class is not implemented in DSTU2");
    }

    @Override
    public void addConceptMapsToStorageQueue(List<ConceptMap> theConceptMaps) {
        throw new UnsupportedOperationException("This class is not implemented in DSTU2");
    }

    @Override
    public void addValueSetsToStorageQueue(List<ValueSet> theValueSets) {
        throw new UnsupportedOperationException("This class is not implemented in DSTU2");
    }

    @Override
    public void deleteCodeSystem(TermCodeSystem termCodeSystem) {
        throw new UnsupportedOperationException("This class is not implemented in DSTU2");
    }

    @Override
    public void deleteCodeSystemForResource(ResourceTable resourceTable) {
        throw new UnsupportedOperationException("This class is not implemented in DSTU2");
    }

    @Override
    public void saveAllDeferred() {
        throw new UnsupportedOperationException("This class is not implemented in DSTU2");
    }

    @Override
    public void logQueueForUnitTest() {
        throw new UnsupportedOperationException("This class is not implemented in DSTU2");
    }
}
