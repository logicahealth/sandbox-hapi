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

package org.hspconsortium.platform.api.fhir.multitenant.search;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.dao.IDao;
import ca.uhn.fhir.jpa.dao.ISearchBuilder;
//import ca.uhn.fhir.jpa.dao.SearchParameterMap;
import ca.uhn.fhir.jpa.dao.data.ISearchDao;
import ca.uhn.fhir.jpa.dao.data.ISearchIncludeDao;
import ca.uhn.fhir.jpa.dao.data.ISearchResultDao;
import ca.uhn.fhir.jpa.entity.*;
import ca.uhn.fhir.jpa.search.ISearchCoordinatorSvc;
import ca.uhn.fhir.jpa.search.PersistedJpaBundleProvider;
import ca.uhn.fhir.jpa.search.SearchCoordinatorSvcImpl;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.jpa.util.StopWatch;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.server.SimpleBundleProvider;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.ResourceGoneException;
import ca.uhn.fhir.rest.server.method.PageMethodBinding;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.concurrent.*;

public class MultiTenantSearchCoordinatorSvcImpl implements ISearchCoordinatorSvc {
    public static final int DEFAULT_SYNC_SIZE = 250;
    private static final Logger ourLog = LoggerFactory.getLogger(SearchCoordinatorSvcImpl.class);
    @Autowired
    private FhirContext myContext;
    @Autowired
    private DaoConfig myDaoConfig;
    @Autowired
    private EntityManager myEntityManager;
    private final ConcurrentHashMap<String, SearchTask> myIdToSearchTask = new ConcurrentHashMap();
    private Integer myLoadingThrottleForUnitTests = null;
    private long myMaxMillisToWaitForRemoteResults = 60000L;
    private boolean myNeverUseLocalSearchForUnitTests;
    @Autowired
    private ISearchDao mySearchDao;
    @Autowired
    private ISearchIncludeDao mySearchIncludeDao;
    @Autowired
    private ISearchResultDao mySearchResultDao;
    @Autowired
    private PlatformTransactionManager myManagedTxManager;
    private int mySyncSize = 250;

    public MultiTenantSearchCoordinatorSvcImpl() {
    }

    public void cancelAllActiveSearches() {
        Iterator var1 = this.myIdToSearchTask.values().iterator();

        while (var1.hasNext()) {
            MultiTenantSearchCoordinatorSvcImpl.SearchTask next = (MultiTenantSearchCoordinatorSvcImpl.SearchTask) var1.next();
            next.requestImmediateAbort();

            try {
                next.getCompletionLatch().await();
            } catch (InterruptedException var4) {
                ourLog.warn("Failed to wait for completion", var4);
            }
        }

    }

    @Transactional(
            propagation = Propagation.NEVER
    )
    public List<Long> getResources(final String theUuid, int theFrom, int theTo) {
        if (!this.myNeverUseLocalSearchForUnitTests) {
            MultiTenantSearchCoordinatorSvcImpl.SearchTask task = (MultiTenantSearchCoordinatorSvcImpl.SearchTask) this.myIdToSearchTask.get(theUuid);
            if (task != null) {
                return task.getResourcePids(theFrom, theTo);
            }
        }

        TransactionTemplate txTemplate = new TransactionTemplate(this.myManagedTxManager);
        txTemplate.setPropagationBehavior(0);
        StopWatch sw = new StopWatch();

        final Search search;
        search = (Search) txTemplate.execute(new TransactionCallback<Search>() {
            public Search doInTransaction(TransactionStatus theStatus) {
                return MultiTenantSearchCoordinatorSvcImpl.this.mySearchDao.findByUuid(theUuid);
            }
        });
        while (true) {
            if (search == null) {
                ourLog.info("Client requested unknown paging ID[{}]", theUuid);
                String msg = this.myContext.getLocalizer().getMessage(PageMethodBinding.class, "unknownSearchId", new Object[]{theUuid});
                throw new ResourceGoneException(msg);
            }

            verifySearchHasntFailedOrThrowInternalErrorException(search);
            if (search.getStatus() == SearchStatusEnum.FINISHED) {
                ourLog.info("Search entity marked as finished");
                break;
            }

            if (search.getNumFound() >= theTo) {
                ourLog.info("Search entity has {} results so far", Integer.valueOf(search.getNumFound()));
                break;
            }

            if (sw.getMillis() > this.myMaxMillisToWaitForRemoteResults) {
                throw new InternalErrorException("Request timed out after " + sw.getMillis() + "ms");
            }

            try {
                Thread.sleep(500L);
            } catch (InterruptedException var10) {
                ;
            }
        }

        final Pageable page = toPage(theFrom, theTo);
        if (page == null) {
            return Collections.emptyList();
        } else {
            List<Long> retVal = (List) txTemplate.execute(new TransactionCallback<List<Long>>() {
                public List<Long> doInTransaction(TransactionStatus theStatus) {
                    List<Long> resultPids = new ArrayList();
                    Page<Long> searchResults = MultiTenantSearchCoordinatorSvcImpl.this.mySearchResultDao.findWithSearchUuid(search, page);
                    Iterator var4 = searchResults.iterator();

                    while (var4.hasNext()) {
                        Long next = (Long) var4.next();
                        resultPids.add(next);
                    }

                    return resultPids;
                }
            });
            return retVal;
        }
    }

    private void populateBundleProvider(PersistedJpaBundleProvider theRetVal) {
        theRetVal.setContext(this.myContext);
        theRetVal.setEntityManager(this.myEntityManager);
        theRetVal.setPlatformTransactionManager(this.myManagedTxManager);
        theRetVal.setSearchDao(this.mySearchDao);
        theRetVal.setSearchCoordinatorSvc(this);
    }

    public IBundleProvider registerSearch(final IDao theCallingDao, final SearchParameterMap theParams, final String theResourceType, CacheControlDirective var4) {
        StopWatch w = new StopWatch();
        final String searchUuid = UUID.randomUUID().toString();
        Class<? extends IBaseResource> resourceTypeClass = this.myContext.getResourceDefinition(theResourceType).getImplementingClass();
        final ISearchBuilder sb = theCallingDao.newSearchBuilder();
        sb.setType(resourceTypeClass, theResourceType);
        if (theParams.isLoadSynchronous()) {
            TransactionTemplate txTemplate = new TransactionTemplate(this.myManagedTxManager);
            txTemplate.setPropagationBehavior(0);
            return (IBundleProvider) txTemplate.execute(new TransactionCallback<SimpleBundleProvider>() {
                public SimpleBundleProvider doInTransaction(TransactionStatus theStatus) {
                    List<Long> pids = new ArrayList();
                    Iterator resultIter = sb.createQuery(theParams, searchUuid);

                    while (resultIter.hasNext()) {
                        pids.add((Long) resultIter.next());
                        if (theParams.getLoadSynchronousUpTo() != null && pids.size() >= theParams.getLoadSynchronousUpTo().intValue()) {
                            break;
                        }
                    }

                    Set<Long> includedPids = new HashSet();
                    includedPids.addAll(sb.loadIncludes(MultiTenantSearchCoordinatorSvcImpl.this.myContext, MultiTenantSearchCoordinatorSvcImpl.this.myEntityManager, pids, theParams.getRevIncludes(), true, theParams.getLastUpdated(), "(synchronous)"));
                    includedPids.addAll(sb.loadIncludes(MultiTenantSearchCoordinatorSvcImpl.this.myContext, MultiTenantSearchCoordinatorSvcImpl.this.myEntityManager, pids, theParams.getIncludes(), false, theParams.getLastUpdated(), "(synchronous)"));
                    List<IBaseResource> resources = new ArrayList();
                    sb.loadResourcesByPid(pids, resources, includedPids, false, MultiTenantSearchCoordinatorSvcImpl.this.myEntityManager, MultiTenantSearchCoordinatorSvcImpl.this.myContext, theCallingDao);
                    return new SimpleBundleProvider(resources);
                }
            });
        } else {
            final String queryString = theParams.toNormalizedQueryString(this.myContext);
            if (theParams.getEverythingMode() == null && this.myDaoConfig.getReuseCachedSearchResultsForMillis() != null) {
                final Date createdCutoff = new Date(System.currentTimeMillis() - this.myDaoConfig.getReuseCachedSearchResultsForMillis().longValue());
                TransactionTemplate txTemplate = new TransactionTemplate(this.myManagedTxManager);
                PersistedJpaBundleProvider foundSearchProvider = (PersistedJpaBundleProvider) txTemplate.execute(new TransactionCallback<PersistedJpaBundleProvider>() {
                    public PersistedJpaBundleProvider doInTransaction(TransactionStatus theStatus) {
                        Search searchToUse = null;
                        int hashCode = queryString.hashCode();
                        Collection<Search> candidates = MultiTenantSearchCoordinatorSvcImpl.this.mySearchDao.find(theResourceType, hashCode, createdCutoff);
                        Iterator var5 = candidates.iterator();

                        while (var5.hasNext()) {
                            Search nextCandidateSearch = (Search) var5.next();
                            if (queryString.equals(nextCandidateSearch.getSearchQueryString())) {
                                searchToUse = nextCandidateSearch;
                            }
                        }

                        PersistedJpaBundleProvider retVal = null;
                        if (searchToUse != null) {
                            MultiTenantSearchCoordinatorSvcImpl.ourLog.info("Reusing search {} from cache", searchToUse.getUuid());
                            searchToUse.setSearchLastReturned(new Date());
                            MultiTenantSearchCoordinatorSvcImpl.this.mySearchDao.updateSearchLastReturned(searchToUse.getId().longValue(), new Date());
                            retVal = new PersistedJpaBundleProvider(searchToUse.getUuid(), theCallingDao);
                            MultiTenantSearchCoordinatorSvcImpl.this.populateBundleProvider(retVal);
                        }

                        return retVal;
                    }
                });
                if (foundSearchProvider != null) {
                    return foundSearchProvider;
                }
            }

            Search search = new Search();
            search.setUuid(searchUuid);
            search.setCreated(new Date());
            search.setSearchLastReturned(new Date());
            search.setTotalCount((Integer) null);
            search.setNumFound(0);
            search.setPreferredPageSize(theParams.getCount());
            search.setSearchType(theParams.getEverythingMode() != null ? SearchTypeEnum.EVERYTHING : SearchTypeEnum.SEARCH);
            search.setLastUpdated(theParams.getLastUpdated());
            search.setResourceType(theResourceType);
            search.setStatus(SearchStatusEnum.LOADING);
            search.setSearchQueryString(queryString);
            search.setSearchQueryStringHash(Integer.valueOf(queryString.hashCode()));
            Iterator var10 = theParams.getIncludes().iterator();

            Include next;
            while (var10.hasNext()) {
                next = (Include) var10.next();
                search.getIncludes().add(new SearchInclude(search, next.getValue(), false, next.isRecurse()));
            }

            var10 = theParams.getRevIncludes().iterator();

            while (var10.hasNext()) {
                next = (Include) var10.next();
                search.getIncludes().add(new SearchInclude(search, next.getValue(), true, next.isRecurse()));
            }

            MultiTenantSearchCoordinatorSvcImpl.SearchTask task = new MultiTenantSearchCoordinatorSvcImpl.SearchTask(search, theCallingDao, theParams, theResourceType, searchUuid);
            this.myIdToSearchTask.put(search.getUuid(), task);

            FutureTask<Void> futureTask = new FutureTask<Void>(task);
            Thread searchThread = new Thread(futureTask, "search_coord");
            searchThread.start();

            MultiTenantPersistedJpaSearchFirstPageBundleProvider retVal = new MultiTenantPersistedJpaSearchFirstPageBundleProvider(search, theCallingDao, task, sb, this.myManagedTxManager);
            this.populateBundleProvider(retVal);
            ourLog.info("Search initial phase completed in {}ms", Long.valueOf(w.getMillis()));
            return retVal;
        }
    }

    @VisibleForTesting
    void setContextForUnitTest(FhirContext theCtx) {
        this.myContext = theCtx;
    }

    @VisibleForTesting
    void setDaoConfigForUnitTest(DaoConfig theDaoConfig) {
        this.myDaoConfig = theDaoConfig;
    }

    @VisibleForTesting
    void setEntityManagerForUnitTest(EntityManager theEntityManager) {
        this.myEntityManager = theEntityManager;
    }

    @VisibleForTesting
    public void setLoadingThrottleForUnitTests(Integer theLoadingThrottleForUnitTests) {
        this.myLoadingThrottleForUnitTests = theLoadingThrottleForUnitTests;
    }

    @VisibleForTesting
    void setMaxMillisToWaitForRemoteResultsForUnitTest(long theMaxMillisToWaitForRemoteResults) {
        this.myMaxMillisToWaitForRemoteResults = theMaxMillisToWaitForRemoteResults;
    }

    @VisibleForTesting
    public void setNeverUseLocalSearchForUnitTests(boolean theNeverUseLocalSearchForUnitTests) {
        this.myNeverUseLocalSearchForUnitTests = theNeverUseLocalSearchForUnitTests;
    }

    @VisibleForTesting
    void setSearchDaoForUnitTest(ISearchDao theSearchDao) {
        this.mySearchDao = theSearchDao;
    }

    @VisibleForTesting
    void setSearchDaoIncludeForUnitTest(ISearchIncludeDao theSearchIncludeDao) {
        this.mySearchIncludeDao = theSearchIncludeDao;
    }

    @VisibleForTesting
    void setSearchDaoResultForUnitTest(ISearchResultDao theSearchResultDao) {
        this.mySearchResultDao = theSearchResultDao;
    }

    @VisibleForTesting
    public void setSyncSizeForUnitTests(int theSyncSize) {
        this.mySyncSize = theSyncSize;
    }

    @VisibleForTesting
    void setTransactionManagerForUnitTest(PlatformTransactionManager theTxManager) {
        this.myManagedTxManager = theTxManager;
    }

    static Pageable toPage(final int theFromIndex, int theToIndex) {
        int pageSize = theToIndex - theFromIndex;
        if (pageSize < 1) {
            return null;
        } else {
            int pageIndex = theFromIndex / pageSize;
            Pageable page = PageRequest.of(pageIndex, pageSize);
//            {
//                private static final long serialVersionUID = 1L;
//
//                public int getOffset() {
//                    return theFromIndex;
//                }
//            };
            return page;
        }
    }

    static void verifySearchHasntFailedOrThrowInternalErrorException(Search theSearch) {
        if (theSearch.getStatus() == SearchStatusEnum.FAILED) {
            Integer status = theSearch.getFailureCode();
            status = (Integer) ObjectUtils.defaultIfNull(status, Integer.valueOf(500));
            String message = theSearch.getFailureMessage();
            throw BaseServerResponseException.newInstance(status.intValue(), message);
        }
    }

    public class SearchTask implements Callable<Void> {
        private boolean myAbortRequested;
        private final IDao myCallingDao;
        private final CountDownLatch myCompletionLatch;
        private int myCountSaved = 0;
        private final CountDownLatch myInitialCollectionLatch = new CountDownLatch(1);
        private final SearchParameterMap myParams;
        private final String myResourceType;
        private final Search mySearch;
        private final ArrayList<Long> mySyncedPids = new ArrayList();
        private final ArrayList<Long> myUnsyncedPids = new ArrayList();
        private String mySearchUuid;

        public SearchTask(Search theSearch, IDao theCallingDao, SearchParameterMap theParams, String theResourceType, String theSearchUuid) {
            this.mySearch = theSearch;
            this.myCallingDao = theCallingDao;
            this.myParams = theParams;
            this.myResourceType = theResourceType;
            this.myCompletionLatch = new CountDownLatch(1);
            this.mySearchUuid = theSearchUuid;
        }

        public void awaitInitialSync() {
            MultiTenantSearchCoordinatorSvcImpl.ourLog.trace("Awaiting initial sync");

            do {
                try {
                    if (this.myInitialCollectionLatch.await(250L, TimeUnit.MILLISECONDS)) {
                        break;
                    }
                } catch (InterruptedException var2) {
                    throw new InternalErrorException(var2);
                }
            } while (this.mySearch.getStatus() == SearchStatusEnum.LOADING);

            MultiTenantSearchCoordinatorSvcImpl.ourLog.trace("Initial sync completed");
        }

        public Void call() throws Exception {
            StopWatch sw = new StopWatch();

            try {
                this.saveSearch();
                TransactionTemplate txTemplate = new TransactionTemplate(MultiTenantSearchCoordinatorSvcImpl.this.myManagedTxManager);
                txTemplate.setPropagationBehavior(3);
                txTemplate.execute(new TransactionCallbackWithoutResult() {
                    protected void doInTransactionWithoutResult(TransactionStatus theStatus) {
                        MultiTenantSearchCoordinatorSvcImpl.SearchTask.this.doSearch();
                    }
                });
                MultiTenantSearchCoordinatorSvcImpl.ourLog.info("Completed search for {} resources in {}ms", Integer.valueOf(this.mySyncedPids.size()), Long.valueOf(sw.getMillis()));
            } catch (Throwable var7) {
                boolean logged = false;
                if (var7 instanceof BaseServerResponseException) {
                    BaseServerResponseException exception = (BaseServerResponseException) var7;
                    if (exception.getStatusCode() >= 400 && exception.getStatusCode() < 500) {
                        logged = true;
                        MultiTenantSearchCoordinatorSvcImpl.ourLog.warn("Failed during search due to invalid request: {}", var7.toString());
                    }
                }

                if (!logged) {
                    MultiTenantSearchCoordinatorSvcImpl.ourLog.error("Failed during search loading after {}ms", Long.valueOf(sw.getMillis()), var7);
                }

                this.myUnsyncedPids.clear();
                Throwable rootCause = ExceptionUtils.getRootCause(var7);
                rootCause = (Throwable) ObjectUtils.defaultIfNull(rootCause, var7);
                String failureMessage = rootCause.getMessage();
                int failureCode = 500;
                if (var7 instanceof BaseServerResponseException) {
                    failureCode = ((BaseServerResponseException) var7).getStatusCode();
                }

                this.mySearch.setFailureMessage(failureMessage);
                this.mySearch.setFailureCode(Integer.valueOf(failureCode));
                this.mySearch.setStatus(SearchStatusEnum.FAILED);
                this.saveSearch();
            }

            MultiTenantSearchCoordinatorSvcImpl.this.myIdToSearchTask.remove(this.mySearch.getUuid());
            this.myCompletionLatch.countDown();
            return null;
        }

        private void doSaveSearch() {
            if (this.mySearch.getId() == null) {
                MultiTenantSearchCoordinatorSvcImpl.this.mySearchDao.save(this.mySearch);
                Iterator var1 = this.mySearch.getIncludes().iterator();

                while (var1.hasNext()) {
                    SearchInclude next = (SearchInclude) var1.next();
                    MultiTenantSearchCoordinatorSvcImpl.this.mySearchIncludeDao.save(next);
                }
            } else {
                MultiTenantSearchCoordinatorSvcImpl.this.mySearchDao.save(this.mySearch);
            }

        }

        private void doSearch() {
            Class<? extends IBaseResource> resourceTypeClass = MultiTenantSearchCoordinatorSvcImpl.this.myContext.getResourceDefinition(this.myResourceType).getImplementingClass();
            ISearchBuilder sb = this.myCallingDao.newSearchBuilder();
            sb.setType(resourceTypeClass, this.myResourceType);

            Iterator theResultIter;
            for (theResultIter = sb.createQuery(this.myParams, this.mySearchUuid); theResultIter.hasNext(); Validate.isTrue(!this.myAbortRequested, "Abort has been requested", new Object[0])) {
                this.myUnsyncedPids.add((Long) theResultIter.next());
                if (this.myUnsyncedPids.size() >= MultiTenantSearchCoordinatorSvcImpl.this.mySyncSize) {
                    this.saveUnsynced(theResultIter);
                }

                if (MultiTenantSearchCoordinatorSvcImpl.this.myLoadingThrottleForUnitTests != null) {
                    try {
                        Thread.sleep((long) MultiTenantSearchCoordinatorSvcImpl.this.myLoadingThrottleForUnitTests.intValue());
                    } catch (InterruptedException var5) {
                        ;
                    }
                }
            }

            this.saveUnsynced(theResultIter);
        }

        public CountDownLatch getCompletionLatch() {
            return this.myCompletionLatch;
        }

        public List<Long> getResourcePids(int theFromIndex, int theToIndex) {
            MultiTenantSearchCoordinatorSvcImpl.ourLog.info("Requesting search PIDs from {}-{}", Integer.valueOf(theFromIndex), Integer.valueOf(theToIndex));
            CountDownLatch latch = null;
            ArrayList retVal = this.mySyncedPids;
            synchronized (this.mySyncedPids) {
                if (this.mySyncedPids.size() < theToIndex && this.mySearch.getStatus() == SearchStatusEnum.LOADING) {
                    int latchSize = theToIndex - this.mySyncedPids.size();
                    MultiTenantSearchCoordinatorSvcImpl.ourLog.trace("Registering latch to await {} results (want {} total)", Integer.valueOf(latchSize), Integer.valueOf(theToIndex));
                    latch = new CountDownLatch(latchSize);
                }
            }

            if (latch != null) {
                while (latch.getCount() > 0L && this.mySearch.getStatus() == SearchStatusEnum.LOADING) {
                    try {
                        MultiTenantSearchCoordinatorSvcImpl.ourLog.trace("Awaiting latch with {}", Long.valueOf(latch.getCount()));
                        latch.await(500L, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException var9) {
                        ;
                    }
                }
            }

            retVal = new ArrayList();
            ArrayList var12 = this.mySyncedPids;
            synchronized (this.mySyncedPids) {
                MultiTenantSearchCoordinatorSvcImpl.verifySearchHasntFailedOrThrowInternalErrorException(this.mySearch);
                int toIndex = theToIndex;
                if (this.mySyncedPids.size() < theToIndex) {
                    toIndex = this.mySyncedPids.size();
                }

                for (int i = theFromIndex; i < toIndex; ++i) {
                    retVal.add(this.mySyncedPids.get(i));
                }

                return retVal;
            }
        }

        public void requestImmediateAbort() {
            this.myAbortRequested = true;
        }

        private void saveSearch() {
            TransactionTemplate txTemplate = new TransactionTemplate(MultiTenantSearchCoordinatorSvcImpl.this.myManagedTxManager);
            txTemplate.setPropagationBehavior(3);
            txTemplate.execute(new TransactionCallbackWithoutResult() {
                protected void doInTransactionWithoutResult(TransactionStatus theArg0) {
                    MultiTenantSearchCoordinatorSvcImpl.SearchTask.this.doSaveSearch();
                }
            });
        }

        private void saveUnsynced(final Iterator<Long> theResultIter) {
            TransactionTemplate txTemplate = new TransactionTemplate(MultiTenantSearchCoordinatorSvcImpl.this.myManagedTxManager);
            txTemplate.setPropagationBehavior(0);
            txTemplate.execute(new TransactionCallbackWithoutResult() {
                protected void doInTransactionWithoutResult(TransactionStatus theArg0) {
                    if (MultiTenantSearchCoordinatorSvcImpl.SearchTask.this.mySearch.getId() == null) {
                        MultiTenantSearchCoordinatorSvcImpl.SearchTask.this.doSaveSearch();
                    }

                    List<SearchResult> resultsToSave = Lists.newArrayList();
                    Iterator var3 = MultiTenantSearchCoordinatorSvcImpl.SearchTask.this.myUnsyncedPids.iterator();

                    while (var3.hasNext()) {
                        Long nextPid = (Long) var3.next();
                        SearchResult nextResult = new SearchResult(MultiTenantSearchCoordinatorSvcImpl.SearchTask.this.mySearch);
                        nextResult.setResourcePid(nextPid);
                        nextResult.setOrder(MultiTenantSearchCoordinatorSvcImpl.SearchTask.this.myCountSaved++);
                        resultsToSave.add(nextResult);
                    }
                    for (SearchResult searchResult: resultsToSave) {
                        MultiTenantSearchCoordinatorSvcImpl.this.mySearchResultDao.save(searchResult);
                    }

                    synchronized (MultiTenantSearchCoordinatorSvcImpl.SearchTask.this.mySyncedPids) {
                        int numSyncedThisPass = MultiTenantSearchCoordinatorSvcImpl.SearchTask.this.myUnsyncedPids.size();
                        MultiTenantSearchCoordinatorSvcImpl.ourLog.trace("Syncing {} search results", Integer.valueOf(numSyncedThisPass));
                        MultiTenantSearchCoordinatorSvcImpl.SearchTask.this.mySyncedPids.addAll(MultiTenantSearchCoordinatorSvcImpl.SearchTask.this.myUnsyncedPids);
                        MultiTenantSearchCoordinatorSvcImpl.SearchTask.this.myUnsyncedPids.clear();
                        if (!theResultIter.hasNext()) {
                            MultiTenantSearchCoordinatorSvcImpl.SearchTask.this.mySearch.setStatus(SearchStatusEnum.FINISHED);
                            MultiTenantSearchCoordinatorSvcImpl.SearchTask.this.mySearch.setTotalCount(Integer.valueOf(MultiTenantSearchCoordinatorSvcImpl.SearchTask.this.myCountSaved));
                        }
                    }

                    MultiTenantSearchCoordinatorSvcImpl.SearchTask.this.mySearch.setNumFound(MultiTenantSearchCoordinatorSvcImpl.SearchTask.this.myCountSaved);
                    MultiTenantSearchCoordinatorSvcImpl.SearchTask.this.doSaveSearch();
                }
            });
            this.myInitialCollectionLatch.countDown();
        }
    }
}