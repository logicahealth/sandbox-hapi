package org.hspconsortium.platform.api.search;

import ca.uhn.fhir.jpa.dao.IDao;
import ca.uhn.fhir.jpa.dao.ISearchBuilder;
import ca.uhn.fhir.jpa.dao.SearchBuilderFactory;
import ca.uhn.fhir.jpa.entity.Search;
import ca.uhn.fhir.jpa.model.cross.ResourcePersistentId;
import ca.uhn.fhir.jpa.model.search.SearchStatusEnum;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.api.ResourceMetadataKeyEnum;
import ca.uhn.fhir.model.valueset.BundleEntrySearchModeEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LogicaPersistedJpaSearchFirstPageBundleProvider extends LogicaPersistedJpaBundleProvider {
    private static final Logger ourLog = LoggerFactory.getLogger(LogicaPersistedJpaSearchFirstPageBundleProvider.class);
    private LogicaSearchCoordinatorSvcImpl.SearchTask mySearchTask;
    private ISearchBuilder mySearchBuilder;
    private Search mySearch;
    private PlatformTransactionManager myTxManager;

    // TODO KHS too many collaborators.  This should be a prototype bean
    public LogicaPersistedJpaSearchFirstPageBundleProvider(Search theSearch, IDao theDao, SearchBuilderFactory theSearchBuilderFactory, LogicaSearchCoordinatorSvcImpl.SearchTask theSearchTask, ISearchBuilder theSearchBuilder, PlatformTransactionManager theTxManager, RequestDetails theRequest) {
        super(theRequest, theSearch.getUuid(), theDao, theSearchBuilderFactory);
        setSearchEntity(theSearch);
        mySearchTask = theSearchTask;
        mySearchBuilder = theSearchBuilder;
        mySearch = theSearch;
        myTxManager = theTxManager;
    }

    @Nonnull
    @Override
    public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
        LogicaSearchCoordinatorSvcImpl.verifySearchHasntFailedOrThrowInternalErrorException(mySearch);

        mySearchTask.awaitInitialSync();

        ourLog.trace("Fetching search resource PIDs from task: {}", mySearchTask.getClass());
        final List<ResourcePersistentId> pids = mySearchTask.getResourcePids(theFromIndex, theToIndex);
        ourLog.trace("Done fetching search resource PIDs");

        TransactionTemplate txTemplate = new TransactionTemplate(myTxManager);
        txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        List<IBaseResource> retVal = txTemplate.execute(theStatus -> toResourceList(mySearchBuilder, pids));

        long totalCountWanted = theToIndex - theFromIndex;
        long totalCountMatch = (int) retVal
                .stream()
                .filter(t -> !isInclude(t))
                .count();

        if (totalCountMatch < totalCountWanted) {
            if (mySearch.getStatus() == SearchStatusEnum.PASSCMPLET) {

                /*
                 * This is a bit of complexity to account for the possibility that
                 * the consent service has filtered some results.
                 */
                Set<String> existingIds = retVal
                        .stream()
                        .map(t -> t.getIdElement().getValue())
                        .filter(t -> t != null)
                        .collect(Collectors.toSet());

                long remainingWanted = totalCountWanted - totalCountMatch;
                long fromIndex = theToIndex - remainingWanted;
                List<IBaseResource> remaining = super.getResources((int) fromIndex, theToIndex);
                remaining.forEach(t -> {
                    if (!existingIds.contains(t.getIdElement().getValue())) {
                        retVal.add(t);
                    }
                });
            }
        }
        ourLog.trace("Loaded resources to return");

        return retVal;
    }

    private boolean isInclude(IBaseResource theResource) {
        if (theResource instanceof IAnyResource) {
            return "include".equals(ResourceMetadataKeyEnum.ENTRY_SEARCH_MODE.get(((IAnyResource) theResource)));
        }
        BundleEntrySearchModeEnum searchMode = ResourceMetadataKeyEnum.ENTRY_SEARCH_MODE.get(((IResource) theResource));
        return BundleEntrySearchModeEnum.INCLUDE.equals(searchMode);
    }

    @Override
    public Integer size() {
        ourLog.trace("Waiting for initial sync");
        Integer size = mySearchTask.awaitInitialSync();
        ourLog.trace("Finished waiting for local sync");

        LogicaSearchCoordinatorSvcImpl.verifySearchHasntFailedOrThrowInternalErrorException(mySearch);
        if (size != null) {
            return size;
        }
        return super.size();
    }
}
