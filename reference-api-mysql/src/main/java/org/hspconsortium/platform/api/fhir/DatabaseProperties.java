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

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("hspc.platform.api.fhir")
public class DatabaseProperties {
	public static final String SANDBOX_SCHEMA_DELIMITER = "_";
	// in MySQL, '_' is a wildcard matching 1 char so it must be escaped
	public static final String SANDBOX_SCHEMA_DELIMITER_ESCAPED = "\\" + SANDBOX_SCHEMA_DELIMITER;
	public static final String SANDBOX_SCHEMA_SNAPSHOT_DELIMITER = "$";
	public static final String SANDBOX_SCHEMA_NAMING = "%s" + SANDBOX_SCHEMA_DELIMITER + "%s" + SANDBOX_SCHEMA_DELIMITER + "%s";
	public static final String SANDBOX_SCHEMA_PREFIX = "hspc";
	public static final String DEFAULT_HSPC_SCHEMA_VERSION = "8";

	@NestedConfigurationProperty
	private DataSourceProperties dataSource;

//	@Value("${flyway.locations}")
//	private String flywayLocations;
//
//	public String getFlywayLocations() {
//		return flywayLocations;
//	}

	public DataSourceProperties getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSourceProperties dataSource) {
		this.dataSource = dataSource;
	}

	public DataSourceProperties getDataSourceInfo() {
		DataSourceProperties dataSourceProperties = new DataSourceProperties();
		dataSourceProperties.setUrl(dataSource.getUrl());
		dataSourceProperties.setUsername(dataSource.getUsername());
		dataSourceProperties.setPassword(dataSource.getPassword());
		dataSourceProperties.setSchema(dataSource.getSchema());
		dataSourceProperties.setData((dataSource.getData()));
		dataSourceProperties.setBeanClassLoader(dataSource.getClassLoader());
		dataSourceProperties.setDriverClassName(dataSource.getDriverClassName());
		dataSourceProperties.setPlatform(dataSource.getPlatform());

		return dataSourceProperties;
	}
}