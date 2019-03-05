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

package org.hspconsortium.platform.api.persister;

import org.apache.commons.io.IOUtils;
import org.hspconsortium.platform.api.controller.HapiFhirController;
import org.hspconsortium.platform.api.fhir.DatabaseManager;
import org.hspconsortium.platform.api.fhir.DatabaseProperties;
import org.hspconsortium.platform.api.fhir.model.TenantInfo;
import org.hspconsortium.platform.api.fhir.model.DataSet;
import org.hspconsortium.platform.api.fhir.model.Sandbox;
import org.hspconsortium.platform.api.oauth2.OAuth2ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class SandboxPersister {

    private static final Logger logger = LoggerFactory.getLogger(SandboxPersister.class);

    private static String DEFAULT_OPEN_CONTEXT_PATH = OAuth2ResourceConfig.NO_ENDPOINT;

    @Value("${spring.profiles.active}")
    private String profile;

    @Value("${hspc.platform.api.fhir.openContextPath:" + OAuth2ResourceConfig.NO_ENDPOINT + "}")
    private SandboxPersister setOpenContextPath(String openContextPath) {
        DEFAULT_OPEN_CONTEXT_PATH = openContextPath;
        return this;
    }

    @Value("${hspc.platform.api.fhir.datasource.emptySchemaScriptPattern}")
    private String emptySchemaScriptPattern;

    @Value("${hspc.platform.api.fhir.datasource.starterSchemaScriptPattern}")
    private String starterSchemaScriptPattern;

    @Value("${hspc.platform.api.fhir.datasource.initializationScripts}")
    private String[] additionalScripts;

    @Value("${hspc.platform.api.fhir.datasource.password}")
    private String dbpassword;

    @Value("${hspc.platform.api.fhir.datasource.username}")
    private String dbusername;

    @Value("${hspc.platform.api.fhir.datasource.host}")
    private String dbhost;

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

        return sandbox;
    };

    Function<String, Sandbox> toSandboxWithTenantName = (tenantName) ->  {
        try {
            tenantName = tenantName.split(DatabaseProperties.SANDBOX_SCHEMA_DELIMITER)[2];
            return findSandbox(tenantName);
        } catch (SchemaNotInitializedException e) {
            throw new RuntimeException("Error finding " + tenantName, e);
        }
    };

    @Autowired
    @Lazy
    private DatabaseManager databaseManager;

    public List<String> getSandboxNames() {
        // those that begin with the sandbox prefix
        Set<String> schemas = databaseManager.getSchemasLike(
                DatabaseProperties.SANDBOX_SCHEMA_PREFIX +
                        DatabaseProperties.SANDBOX_SCHEMA_DELIMITER_ESCAPED +
                        DatabaseProperties.DEFAULT_HSPC_SCHEMA_VERSION +
                        DatabaseProperties.SANDBOX_SCHEMA_DELIMITER_ESCAPED + "%",
                "%" + DatabaseProperties.SANDBOX_SCHEMA_SNAPSHOT_DELIMITER + "%");
        if (!schemas.isEmpty()) {
            return schemas
                    .parallelStream()
                    .map(toTeamId)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<Sandbox> getSandboxes() {
        // those that begin with the sandbox prefix
        Set<String> schemas = databaseManager.getSchemasLike(
                DatabaseProperties.SANDBOX_SCHEMA_PREFIX +
                        DatabaseProperties.SANDBOX_SCHEMA_DELIMITER_ESCAPED +
                        "%" +
                        DatabaseProperties.SANDBOX_SCHEMA_DELIMITER_ESCAPED + "%",
                "%" + DatabaseProperties.SANDBOX_SCHEMA_SNAPSHOT_DELIMITER + "%");
        if (!schemas.isEmpty()) {
            return schemas
                    .parallelStream()
                    .map(toSandboxWithTenantName)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public boolean isTeamIdUnique(String teamId) {
        // those that end with the teamId
        Set<String> schemasLike = databaseManager.getSchemasLike(
                "%" + DatabaseProperties.SANDBOX_SCHEMA_DELIMITER_ESCAPED + teamId);
        return schemasLike.isEmpty();
    }

    public String findSchemaForTeam(String teamId) {
        // those that end with the teamId
        Set<String> schemasLike = databaseManager.getSchemasLike(
                DatabaseProperties.SANDBOX_SCHEMA_PREFIX +
                        DatabaseProperties.SANDBOX_SCHEMA_DELIMITER_ESCAPED +
                        "%" +
                        DatabaseProperties.SANDBOX_SCHEMA_DELIMITER_ESCAPED +
                        teamId);
        // should be only one
        switch (schemasLike.size()) {
            case 0:
                return null;
            case 1:
                return schemasLike.iterator().next();
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

    public void cloneSandbox(Sandbox newSandbox, Sandbox clonedSandbox) {
        String schemaNameNewSandbox = toSchemaName.apply(newSandbox);
        TenantInfo tenantInfoNewSandbox = toTenantInfo.apply(newSandbox);
        String schemaNameClonedSandbox = toSchemaName.apply(clonedSandbox);

        try {
            String dump = "mysqldump -h " + dbhost + " -u " + dbusername + " -p'" + dbpassword + "' " + schemaNameClonedSandbox + " > ./temp.sql";
            String[] cmdarray = {"/bin/sh","-c", dump};
            Process pr = Runtime.getRuntime().exec(cmdarray);
            Integer outcome = pr.waitFor();
            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
            String error = IOUtils.toString(in);
            if (outcome == 0) {
                logger.info("Mysql dump successful.");
                String create = "mysqladmin -h " + dbhost + " -u " + dbusername + " -p'" + dbpassword + "' create " + schemaNameNewSandbox;
                String[] cmdarray2 = {"/bin/sh","-c", create};
                Process pr2 = Runtime.getRuntime().exec(cmdarray2);
                Integer outcome2 = pr2.waitFor();
                BufferedReader in2 = new BufferedReader(new InputStreamReader(pr2.getErrorStream()));
                error = IOUtils.toString(in2);
                if (outcome2 == 0) {
                    logger.info("New schema created.");
                    String clone = "mysql -h " + dbhost + " -u " + dbusername + " -p'" + dbpassword + "' " + schemaNameNewSandbox + " < ./temp.sql";
                    String[] cmdarray3 = {"/bin/sh","-c", clone};
                    Process pr3 = Runtime.getRuntime().exec(cmdarray3);
                    Integer outcome3 = pr3.waitFor();
                    BufferedReader in3 = new BufferedReader(new InputStreamReader(pr3.getErrorStream()));
                    error = IOUtils.toString(in3);
                    if (outcome3 == 0) {
                        logger.info("Data loaded into new schema.");
                        String delete = "rm ./temp.sql";
                        String[] cmdarray4 = {"/bin/sh","-c", delete};
                        Process pr4 = Runtime.getRuntime().exec(cmdarray4);
                        Integer outcome4 = pr4.waitFor();
                        BufferedReader in4 = new BufferedReader(new InputStreamReader(pr4.getErrorStream()));
                        error = IOUtils.toString(in4);
                        if (outcome4 == 0) {
                            logger.info("Temp sql file deleted.");
                            TenantInfo saved = databaseManager.createAndInitializeSchema(schemaNameNewSandbox, tenantInfoNewSandbox);
                            toSandbox.apply(saved);
                        } else {
                            throw new Exception(error);
                        }
                    } else {
                        throw new Exception(error);
                    }
                } else {
                    throw new Exception(error);
                }
            } else {
                throw new Exception(error);
            }

        } catch (Exception e) {
            logger.info("Error in cloning.", e);
            throw new RuntimeException(e);
        }
    }

    public Set<String> getSnapshots(Sandbox sandbox) {
        String schemaName = toSchemaName.apply(sandbox);
        return databaseManager.getSnapshotsForSchema(schemaName);
    }

    public String takeSnapshot(Sandbox sandbox, String suffix) {
        String schemaName = toSchemaName.apply(sandbox);
        String snapshot = databaseManager.takeSnapshot(schemaName, suffix);
        return snapshot;
    }

    public String restoreSnapshot(Sandbox sandbox, String suffix) {
        String schemaName = toSchemaName.apply(sandbox);
        logger.info("schemaName: " + schemaName);
        return databaseManager.restoreSnapshot(schemaName, suffix);
    }

    public String deleteSnapshot(Sandbox sandbox, String suffix) {
        String schemaName = toSchemaName.apply(sandbox);
        return databaseManager.deleteSnapshot(schemaName, suffix);
    }

    public boolean loadInitialDataset(Sandbox sandbox, DataSet starterDataSet) {
        boolean success = false;
        logger.info("loadInitialDataset [" + starterDataSet + "] in sandbox [" + sandbox.toString() + "]");
        DataSet loadingDataSet = DataSet.NONE;
        if (starterDataSet != null) {
            loadingDataSet = starterDataSet;
        }

        // copy in the starter set
        String dataFileNameTemplate = loadingDataSet == DataSet.DEFAULT ? starterSchemaScriptPattern : emptySchemaScriptPattern;
        final String dataFileName = String.format(
                dataFileNameTemplate,
                sandbox.getSchemaVersion(),
                returnActiveFhirVersion(),
                loadingDataSet.toString().toLowerCase());
        ClassPathResource classPathResource = new ClassPathResource(dataFileName);
        try {
            if (classPathResource.exists()) {
                logger.info("Found resource: " + dataFileName);
                try (InputStream inputStream = classPathResource.getInputStream()) {
                    success = loadFromInputStream(sandbox, inputStream);
                }
            } else {
                logger.info("Did not find resource: " + dataFileName + ", trying as .zip...");
                String asZip = dataFileName + ".zip";
                classPathResource = new ClassPathResource(asZip);
                if (classPathResource.exists()) {
                    logger.info("found resource: " + asZip);
                    try (InputStream inputStream = classPathResource.getInputStream();
                         ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
                        ZipEntry zipEntry = zipInputStream.getNextEntry();
                        if (zipEntry == null) {
                            throw new RuntimeException("Unable to find script inside of " + dataFileName);
                        }
                        success = loadFromInputStream(sandbox, zipInputStream);
                    }
                } else {
                    throw new RuntimeException("Not able to find data file: " + dataFileName);
                }
            }
        } catch (IOException ioe) {
            throw new RuntimeException(String.format("Error creating initial dataset. Data file reference '%s'", dataFileName), ioe);
        }

        return success && loadInitializationScripts(sandbox);
    }

    private boolean loadFromInputStream(Sandbox sandbox, InputStream inputStream) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return databaseManager.loadInitialDataset(toSchemaName.apply(sandbox), reader);
        }
    }

    private boolean loadInitializationScripts(Sandbox sandbox) {
        logger.info("loadInitializationScripts: ");
//        if (additionalScripts != null ) {
//            for (String additionalScript : additionalScripts) {
//                logger.info("additionalScript: " + additionalScript);
//                ClassPathResource classPathResource = new ClassPathResource(additionalScript);
//                if (classPathResource.exists()) {
//                    try (InputStream inputStream = classPathResource.getInputStream()) {
//                        loadFromInputStream(sandbox, inputStream);
//                    } catch (IOException e) {
//                        throw new RuntimeException("Error reading additionalScript: " + additionalScript, e);
//                    }
//
//                } else {
//                    throw new RuntimeException("AdditionalScript not found: " + additionalScript);
//                }
//            }
//        }
        return true;
    }

    public boolean removeSandbox(String schemaVersion, String teamId) {
        return databaseManager.dropSchema(toSchemaName.apply(new Sandbox(teamId, schemaVersion, false)));
    }

    private String returnActiveFhirVersion() {
        if (profile.contains(HapiFhirController.DSTU2_PROFILE_NAME)) {
            return HapiFhirController.DSTU2_PROFILE_NAME;
        } else if (profile.contains(HapiFhirController.STU3_PROFILE_NAME)) {
            return HapiFhirController.STU3_PROFILE_NAME;
        } else if (profile.contains(HapiFhirController.R4_PROFILE_NAME)) {
            return HapiFhirController.R4_PROFILE_NAME;
        }

        throw new IllegalArgumentException("No valid FHIR version profile is set.");
    }

}

