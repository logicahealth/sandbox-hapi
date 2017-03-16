package org.hspconsortium.platform.api.fhir.multitenant;

import org.hspconsortium.platform.api.fhir.DatabaseProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@ConfigurationProperties("hspc.platform.api.fhir")
@Profile("multitenant")
@Qualifier("MultitenantDatabaseProperties")
public class MultitenantDatabaseProperties extends DatabaseProperties {

	@Value("${hspc.platform.api.fhir.datasource.cache.size:10}")
	private String dataSourceCacheSize;

	@Value("${hspc.platform.api.fhir.defaultTenantId:hspc3}")
	private String defaultTenantId;

	public String getDataSourceCacheSize() {
		return dataSourceCacheSize;
	}

	public String getDefaultTenantId() {
		return defaultTenantId;
	}

	public DataSourceProperties getDataSource(String hspcSchemaVersion, String tenant) {
		// override the database url and schema
		DataSourceProperties baseProperties = super.getDb();
		final String schema = String.format(SANDBOX_SCHEMA_NAMING, SANDBOX_SCHEMA_PREFIX, hspcSchemaVersion, tenant);
		String url = getDb().getUrl().replace(baseProperties.getSchema(), schema);
		baseProperties.setUrl(url);
		baseProperties.setSchema(schema);
		return baseProperties;
	}
}