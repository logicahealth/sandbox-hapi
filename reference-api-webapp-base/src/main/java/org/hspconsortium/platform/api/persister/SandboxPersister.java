package org.hspconsortium.platform.api.persister;

import org.hspconsortium.platform.api.fhir.DatabaseManager;
import org.hspconsortium.platform.api.fhir.DatabaseProperties;
import org.hspconsortium.platform.api.fhir.model.TenantInfo;
import org.hspconsortium.platform.api.model.Sandbox;
import org.hspconsortium.platform.api.oauth2.OAuth2ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SandboxPersister {

    private static final String EMPTY_SCHEMA_PATH = "db/hspc_%s_schema_empty.sql";

    private static final String STARTER_SCHEMA_PATH = "db/hspc_%s_initial_dataset.sql";

    private static String DEFAULT_OPEN_CONTEXT_PATH = OAuth2ResourceConfig.NO_ENDPOINT;


    @Value("${hspc.platform.api.fhir.openContextPath:" + OAuth2ResourceConfig.NO_ENDPOINT + "}")
    private SandboxPersister setOpenContextPath(String openContextPath) {
        DEFAULT_OPEN_CONTEXT_PATH = openContextPath;
        return this;
    }

    public static Sandbox sandboxTemplate() {
        return new Sandbox(
                null,
                DatabaseProperties.DEFAULT_HSPC_SCHEMA_VERSION,
                // if the default context path does not equal NO_ENDPOINT, then allow the open endpoint by default
                !OAuth2ResourceConfig.NO_ENDPOINT.equals(DEFAULT_OPEN_CONTEXT_PATH)
        );
    }

    Function<Sandbox, String> toSchemaName = (sandbox) -> {
        try {
            return String.format(DatabaseProperties.SANDBOX_SCHEMA_NAMING,
                    DatabaseProperties.SANDBOX_SCHEMA_PREFIX,
                    sandbox.getSchemaVersion(), sandbox.getTeamId());
        } catch (Exception e) {
            // to nothing, skip this one
            return null;
        }
    };

    Function<String, String> toTeamId = (schemaName) -> {
        try {
            return schemaName.split(DatabaseProperties.SANDBOX_SCHEMA_DELIMITER)[2];
        } catch (Exception e) {
            // to nothing, skip this one
            return null;
        }
    };

    Function<Sandbox, TenantInfo> toTenantInfo = (sandbox) -> {
        if (sandbox == null) {
            return null;
        }
        TenantInfo tenantInfo = new TenantInfo(
                sandbox.getTeamId(),
                sandbox.getSchemaVersion(),
                sandbox.isAllowOpenAccess()
        );
        tenantInfo.setBaselineDate(sandbox.getBaselineDate());
        String properties = sandbox.getProperties();
        tenantInfo.setProperties(
                properties != null && properties.length() > 0
                        ? properties
                        : null);
        // for inbound request, don't transfer the snapshots
        return tenantInfo;
    };

    Function<TenantInfo, Sandbox> toSandbox = (tenantInfo) -> {
        if (tenantInfo == null) {
            return null;
        }
        Sandbox sandbox = new Sandbox(
                tenantInfo.getTenantId(),
                tenantInfo.getHspcSchemaVersion(),
                tenantInfo.isAllowOpenEndpoint()
        );
        sandbox.setBaselineDate(tenantInfo.getBaselineDate());
        sandbox.setProperties(tenantInfo.getPropertiesAsString());
        sandbox.getSnapshots().addAll(tenantInfo.getSnapshots());

        return sandbox;
    };

    @Autowired
    private DatabaseManager databaseManager;

    public List<String> getSandboxes() {
        // those that begin with the sandbox prefix
        List<String> schemas = databaseManager.getSchemasLike(
                DatabaseProperties.SANDBOX_SCHEMA_PREFIX +
                        DatabaseProperties.SANDBOX_SCHEMA_DELIMITER +
                        DatabaseProperties.DEFAULT_HSPC_SCHEMA_VERSION +
                        DatabaseProperties.SANDBOX_SCHEMA_DELIMITER + "%",
                "%" + DatabaseProperties.SANDBOX_SCHEMA_SNAPSHOT_DELIMITER + "%");
        if (!schemas.isEmpty()) {
            return schemas
                    .parallelStream()
                    .map(toTeamId)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public boolean isTeamIdUnique(String teamId) {
        // those that end with the teamId
        List<String> schemasLike = databaseManager.getSchemasLike(
                "%" + DatabaseProperties.SANDBOX_SCHEMA_DELIMITER + teamId);
        return schemasLike.isEmpty();
    }

    public String findSchemaForTeam(String teamId) {
        // those that end with the teamId
        List<String> schemasLike = databaseManager.getSchemasLike(
                DatabaseProperties.SANDBOX_SCHEMA_PREFIX +
                        DatabaseProperties.SANDBOX_SCHEMA_DELIMITER +
                        DatabaseProperties.DEFAULT_HSPC_SCHEMA_VERSION +
                        DatabaseProperties.SANDBOX_SCHEMA_DELIMITER +
                        teamId);
        // should be only one
        switch (schemasLike.size()) {
            case 0:
                return null;
            case 1:
                return schemasLike.get(0);
            default:
                throw new RuntimeException("More than one schema matched the teamId: [" + teamId + "]");
        }
    }

    public Sandbox findSandbox(String teamId) throws SchemaNotInitializedException {
        String schema = findSchemaForTeam(teamId);
        if (schema != null) {
            try {
                TenantInfo tenantInfo = databaseManager.getTenantInfo(schema);
                if (tenantInfo != null) {
                    return toSandbox.apply(tenantInfo);
                }
                throw new SchemaNotInitializedException().forTeam(teamId);
            } catch (Exception e) {
                throw new SchemaNotInitializedException().forTeam(teamId);
            }
        }
        return null;
    }

    public Sandbox saveSandbox(Sandbox sandbox) {
        String schemaName = toSchemaName.apply(sandbox);
        TenantInfo tenantInfo = toTenantInfo.apply(sandbox);

        TenantInfo saved = databaseManager.createAndInitializeSchema(schemaName, tenantInfo);
        return toSandbox.apply(saved);
    }

    public Sandbox takeSnapshot(Sandbox sandbox, String suffix) {
        String schemaName = toSchemaName.apply(sandbox);
        TenantInfo tenantInfo = databaseManager.takeSnapshot(schemaName, suffix);
        return toSandbox.apply(tenantInfo);
    }

    public Sandbox restoreSnapshot(Sandbox sandbox, String suffix) {
        String schemaName = toSchemaName.apply(sandbox);
        TenantInfo tenantInfo = databaseManager.restoreSnapshot(schemaName, suffix);
        return toSandbox.apply(tenantInfo);
    }

    public Sandbox deleteSnapshot(Sandbox sandbox, String suffix) {
        String schemaName = toSchemaName.apply(sandbox);
        TenantInfo tenantInfo = databaseManager.deleteSnapshot(schemaName, suffix);
        return toSandbox.apply(tenantInfo);
    }

    public boolean loadInitialDataset(Sandbox sandbox, boolean starterDataSet) {
        String schemaName = toSchemaName.apply(sandbox);

        // copy in the starter set
        final String dataFileName = String.format(
                starterDataSet ? STARTER_SCHEMA_PATH : EMPTY_SCHEMA_PATH,
                sandbox.getSchemaVersion());
        try {
            ClassPathResource classPathResource = new ClassPathResource(dataFileName);
            InputStream inputStream = classPathResource.getInputStream();
            Reader reader = new BufferedReader(new InputStreamReader(inputStream));
            return databaseManager.loadInitialDataset(schemaName, reader);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error creating initial dataset. Data file reference '%s'", dataFileName), e);
        }
    }

    @CacheEvict(cacheNames = "dataSource", key = "#p1 + '~' + #p0")
    public boolean removeSandbox(String schemaVersion, String teamId) {
        return databaseManager.dropSchema(toSchemaName.apply(new Sandbox(teamId, schemaVersion, false)));
    }

}

