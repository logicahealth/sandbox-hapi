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

package org.hspconsortium.platform.api.multitenant.db;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class SchemaPerTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private Log log = LogFactory.getLog(SchemaPerTenantConnectionProvider.class);

    private DataSourceRepository dataSourceRepository;

    @Autowired
    public SchemaPerTenantConnectionProvider(DataSourceRepository dataSourceRepository) {
        this.dataSourceRepository = dataSourceRepository;
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return dataSourceRepository.getDefaultDataSource();
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        Validate.notNull(tenantIdentifier);
        return dataSourceRepository.getDataSource(tenantIdentifier);
    }
}