package org.hspconsortium.platform.api.service;

import org.apache.commons.lang3.Validate;
import org.hspconsortium.platform.api.fhir.DatabaseProperties;
import org.hspconsortium.platform.api.model.Sandbox;
import org.hspconsortium.platform.api.persister.SandboxPersister;
import org.hspconsortium.platform.api.persister.SchemaNotInitializedException;
import org.hspconsortium.platform.api.security.TenantInfoRequestMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@Component
public class SandboxService {
    private static final Logger logger = LoggerFactory.getLogger(SandboxService.class);

    private SandboxPersister sandboxPersister;

    private TenantInfoRequestMatcher tenantInfoRequestMatcher;

    @Autowired
    public SandboxService(SandboxPersister sandboxPersister, TenantInfoRequestMatcher tenantInfoRequestMatcher) {
        this.sandboxPersister = sandboxPersister;
        this.tenantInfoRequestMatcher = tenantInfoRequestMatcher;
    }

    @Value("${hspc.platform.api.sandbox.useHspcStarterData:false}")
    public String useHspcStarterData;

    public void reset() {
        tenantInfoRequestMatcher.reset();
        logger.info("Sandbox Service reset");
    }

    public Collection<String> all() {
        return sandboxPersister.getSandboxes();
    }

    public Sandbox save(@NotNull Sandbox sandbox) {
        logger.info("Saving sandbox: " + sandbox);
        Validate.notNull(sandbox, "Sandbox must be provided");
        Validate.notNull(sandbox.getTeamId(), "Sandbox.teamId must be provided");

        if (sandbox.getSchemaVersion() != null) {
            Validate.isTrue(DatabaseProperties.DEFAULT_HSPC_SCHEMA_VERSION.equals(sandbox.getSchemaVersion()),
                    "Sandbox schema version [" + sandbox.getSchemaVersion() + "] is not expected schema version [" +
                            DatabaseProperties.DEFAULT_HSPC_SCHEMA_VERSION + "]");
        } else {
            sandbox.setSchemaVersion(DatabaseProperties.DEFAULT_HSPC_SCHEMA_VERSION);
        }

        Sandbox existing = null;
        try {
            existing = sandboxPersister.findSandbox(sandbox.getTeamId());
            logger.info("Existing sandbox: " + existing);
            if (existing == null) {
                // check that the sandbox is unique across versions
                if (!sandboxPersister.isTeamIdUnique(sandbox.getTeamId())) {
                    throw new RuntimeException("TeamID is not unique");
                }
            }
        } catch (SchemaNotInitializedException e) {
            logger.info("SchemaNotInitializedException ignored for now");
            // ignore, will be fixed when saving
        }

        // save the sandbox info
        Sandbox saved = sandboxPersister.saveSandbox(sandbox);
        logger.info("Saved sandbox: " + saved);

        boolean useStarterData = Boolean.valueOf(useHspcStarterData);
        logger.info("useStarterData: " + useStarterData);
        if (existing == null) {
            sandboxPersister.loadInitialDataset(sandbox, useStarterData);
        }

        // Make sure the initial data set didn't replace the sandbox info
        saved = sandboxPersister.saveSandbox(sandbox);
        logger.info("Saved sandbox: " + saved);

        // update security
        if (sandbox.isAllowOpenAccess()) {
            tenantInfoRequestMatcher.addOpenTeamId(saved.getTeamId());
        } else {
            tenantInfoRequestMatcher.removeOpenTeamId(saved.getTeamId());
        }

        return saved;
    }

    public Sandbox get(String teamId) {
        Sandbox sandbox;
        try {
            sandbox = sandboxPersister.findSandbox(teamId);
        } catch (SchemaNotInitializedException e) {
            sandbox = save(SandboxPersister.sandboxTemplate().setTeamId(teamId));
        }

        return sandbox;
    }

    public boolean remove(String teamId) {
        Sandbox existing = get(teamId);
        return (existing == null) || delete(existing);
    }

    private boolean delete(Sandbox existing) {
        if (existing != null) {
            boolean success = sandboxPersister.removeSandbox(existing.getSchemaVersion(), existing.getTeamId());
            // update security
            tenantInfoRequestMatcher.removeOpenTeamId(existing.getTeamId());
            return success;
        } else {
            return true;
        }
    }

    public Sandbox reset(String teamId) {
        Sandbox existing = get(teamId);

        if (existing == null) {
            throw new RuntimeException("Unable to reset sandbox because sandbox does not exist: [" + teamId + "]");
        } else {
            boolean deleted = delete(existing);
            if (!deleted) {
                throw new RuntimeException("Unable to reset sandbox because existing could not be deleted: [" + teamId + "]");
            }
        }

        return save(SandboxPersister.sandboxTemplate().setTeamId(teamId));
    }

    public Sandbox takeSnapshot(String teamId, String snapshotId) {
        Sandbox existing = get(teamId);

        if (existing == null) {
            throw new RuntimeException("Unable to take snapshot of sandbox that does not exist: [" + teamId + "]");
        }

        return sandboxPersister.takeSnapshot(existing, snapshotId);
    }

    public Sandbox restoreSnapshot(String teamId, String snapshotId) {
        Sandbox existing = get(teamId);

        if (existing == null) {
            throw new RuntimeException("Unable to restore snapshot of sandbox that does not exist: [" + teamId + "]");
        }

        return sandboxPersister.restoreSnapshot(existing, snapshotId);
    }

    public Sandbox deleteSnapshot(String teamId, String snapshotId) {
        Sandbox existing = get(teamId);

        if (existing == null) {
            // don't fail on delete
            return null;
        }

        try {
            return sandboxPersister.deleteSnapshot(existing, snapshotId);
        } catch (RuntimeException e) {
            // don't fail on delete
            return null;
        }
    }

}
