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

package org.hspconsortium.platform.api.fhir;

import org.hspconsortium.platform.api.fhir.DatabaseProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import java.util.Collections;

//@ConfigurationProperties("hspc.platform.api.fhir")
@Primary
@Configuration
@PropertySource("classpath:config/mysql.properties")
@ConfigurationProperties(prefix = "hspc.platform.api.fhir")
@Profile("multitenant")
@Qualifier("MultitenantDatabaseProperties")
public class MultitenantDatabaseProperties extends DatabaseProperties {

//	@Value("${hspc.platform.api.fhir.datasource.cache.size:40}")
	private int dataSourceCacheSize;

//	@Value("${hspc.platform.api.fhir.defaultTenantId}")
	private String defaultTenantId;

//	@Value("${hspc.platform.api.fhir.templateSandboxes}")
	private String[] templateSandboxes;

	public int getDataSourceCacheSize() {
		return dataSourceCacheSize;
	}

	public String getDefaultTenantId() {
		return defaultTenantId;
	}

	public String[] getTemplateSandboxes() { return templateSandboxes; }

	public DataSourceProperties getDataSource(String hspcSchemaVersion, String tenant) {
		// override the database url and schema
		DataSourceProperties baseProperties = super.getDb();
		final String schema = String.format(SANDBOX_SCHEMA_NAMING, SANDBOX_SCHEMA_PREFIX, hspcSchemaVersion, tenant);
		String url = getDb().getUrl().replace(baseProperties.getSchema().get(0), schema);
		baseProperties.setUrl(url);
		baseProperties.setSchema(Collections.singletonList(schema));
		return baseProperties;
	}

	public DataSourceProperties getInformationSchemaProperties() {
		DataSourceProperties baseProperties = super.getDb();
		String url = getDb().getUrl().replace(baseProperties.getSchema().get(0), "information_schema");
		baseProperties.setUrl(url);
		baseProperties.setSchema(Collections.singletonList("information_schema"));
		return baseProperties;
	}

	public void setDataSourceCacheSize(int dataSourceCacheSize) {
		this.dataSourceCacheSize = dataSourceCacheSize;
	}

	public void setDefaultTenantId(String defaultTenantId) {
		this.defaultTenantId = defaultTenantId;
	}

	public void setTemplateSandboxes(String[] templateSandboxes) {
		this.templateSandboxes = templateSandboxes;
	}
}