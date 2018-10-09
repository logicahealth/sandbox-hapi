package org.hspconsortium.platform.api.fhir;

import org.hspconsortium.platform.api.fhir.DatabaseProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

import java.util.Collections;

@ConfigurationProperties("hspc.platform.api.fhir")
@Profile("multitenant")
@Qualifier("MultitenantDatabaseProperties")
public class MultitenantDatabaseProperties extends DatabaseProperties {

	@Value("${hspc.platform.api.fhir.datasource.cache.size:40}")
	private int dataSourceCacheSize;

	@Value("${hspc.platform.api.fhir.defaultTenantId}")
	private String defaultTenantId;

	@Value("${hspc.platform.api.fhir.templateSandboxes}")
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
}