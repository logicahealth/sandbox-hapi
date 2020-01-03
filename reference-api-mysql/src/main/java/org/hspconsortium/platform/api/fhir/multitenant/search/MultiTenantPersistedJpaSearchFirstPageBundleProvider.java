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

package org.hspconsortium.platform.api.fhir.multitenant.search;

import ca.uhn.fhir.jpa.dao.IDao;
import ca.uhn.fhir.jpa.dao.ISearchBuilder;
import ca.uhn.fhir.jpa.entity.Search;
import ca.uhn.fhir.jpa.search.PersistedJpaBundleProvider;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

public class MultiTenantPersistedJpaSearchFirstPageBundleProvider extends PersistedJpaBundleProvider {
    private MultiTenantSearchCoordinatorSvcImpl.SearchTask mySearchTask;
    private ISearchBuilder mySearchBuilder;
    private Search mySearch;
    private PlatformTransactionManager myTxManager;

    public MultiTenantPersistedJpaSearchFirstPageBundleProvider(Search theSearch, IDao theDao, MultiTenantSearchCoordinatorSvcImpl.SearchTask theSearchTask, ISearchBuilder theSearchBuilder, PlatformTransactionManager theTxManager) {
        super(theSearch.getUuid(), theDao);
        this.setSearchEntity(theSearch);
        this.mySearchTask = theSearchTask;
        this.mySearchBuilder = theSearchBuilder;
        this.mySearch = theSearch;
        this.myTxManager = theTxManager;
    }

    public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
        MultiTenantSearchCoordinatorSvcImpl.verifySearchHasntFailedOrThrowInternalErrorException(this.mySearch);
        final List<Long> pids = this.mySearchTask.getResourcePids(theFromIndex, theToIndex);
        TransactionTemplate txTemplate = new TransactionTemplate(this.myTxManager);
        txTemplate.setPropagationBehavior(0);
        return (List) txTemplate.execute(new TransactionCallback<List<IBaseResource>>() {
            public List<IBaseResource> doInTransaction(TransactionStatus theStatus) {
                return MultiTenantPersistedJpaSearchFirstPageBundleProvider.this.toResourceList(MultiTenantPersistedJpaSearchFirstPageBundleProvider.this.mySearchBuilder, pids);
            }
        });
    }

    public Integer size() {
        this.mySearchTask.awaitInitialSync();
        MultiTenantSearchCoordinatorSvcImpl.verifySearchHasntFailedOrThrowInternalErrorException(this.mySearch);
        return super.size();
    }
}


