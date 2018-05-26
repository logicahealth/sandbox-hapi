package org.hspconsortium.platform.api.fhir;

import org.springframework.beans.factory.annotation.Value;
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
	public static final String DEFAULT_HSPC_SCHEMA_VERSION = "5";

	@NestedConfigurationProperty
	private DataSourceProperties db;

	@Value("${flyway.locations}")
	private String flywayLocations;

	public String getFlywayLocations() {
		return flywayLocations;
	}

	public DataSourceProperties getDb() {
		return db;
	}

	public void setDb(DataSourceProperties db) {
		this.db = db;
	}

	public DataSourceProperties getDataSource() {
		DataSourceProperties dataSourceProperties = new DataSourceProperties();
		dataSourceProperties.setUrl(db.getUrl());
		dataSourceProperties.setUsername(db.getUsername());
		dataSourceProperties.setPassword(db.getPassword());
		dataSourceProperties.setSchema(db.getSchema());
		dataSourceProperties.setData((db.getData()));
		dataSourceProperties.setBeanClassLoader(db.getClassLoader());
		dataSourceProperties.setDriverClassName(db.getDriverClassName());
		dataSourceProperties.setPlatform(db.getPlatform());

		return dataSourceProperties;
	}
}