package org.hspconsortium.platform.api.service;

import org.apache.commons.lang3.Validate;
import org.hspconsortium.platform.api.fhir.DatabaseProperties;
import org.hspconsortium.platform.api.fhir.service.SandboxService;
import org.hspconsortium.platform.api.fhir.model.DataSet;
import org.hspconsortium.platform.api.fhir.model.Sandbox;
import org.hspconsortium.platform.api.persister.SandboxPersister;
import org.hspconsortium.platform.api.persister.SchemaNotInitializedException;
import org.hspconsortium.platform.api.security.TenantInfoRequestMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;

@Component
public class SandboxServiceImpl implements SandboxService {
    private static final Logger logger = LoggerFactory.getLogger(SandboxServiceImpl.class);

    private SandboxPersister sandboxPersister;

    private TenantInfoRequestMatcher tenantInfoRequestMatcher;

    @Autowired
    public SandboxServiceImpl(SandboxPersister sandboxPersister, TenantInfoRequestMatcher tenantInfoRequestMatcher) {
        this.sandboxPersister = sandboxPersister;
        this.tenantInfoRequestMatcher = tenantInfoRequestMatcher;
    }

    @Override
    public void reset() {
        tenantInfoRequestMatcher.reset();
        logger.info("Sandbox Service reset");
    }

    @Override
    public Collection<String> all() {
        return sandboxPersister.getSandboxes();
    }

    @Override
    public Sandbox save(@NotNull Sandbox sandbox, @NotNull DataSet dataSet) {
        logger.info("Saving sandbox: " + sandbox);
        Validate.notNull(sandbox, "Sandbox must be provided");
        Validate.notNull(sandbox.getTeamId(), "Sandbox.teamId must be provided");

        sandbox.setSchemaVersion(DatabaseProperties.DEFAULT_HSPC_SCHEMA_VERSION);


        Sandbox existing = checkIfTenantNameIsUnique(sandbox);

        // save the sandbox info
        Sandbox saved = sandboxPersister.saveSandbox(sandbox);
        logger.info("Saved sandbox: " + saved);

        logger.info("useStarterData: " + dataSet);
        if (existing == null) {
            sandboxPersister.loadInitialDataset(sandbox, dataSet);
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

    @Override
    public void clone(@NotNull Sandbox newSandbox, @NotNull Sandbox clonedSandbox) {
        logger.info("Cloning sandbox " + clonedSandbox.getTeamId() + " to sandbox: " + newSandbox.getTeamId());
        Validate.notNull(newSandbox, "New sandbox must be provided");
        Validate.notNull(newSandbox.getTeamId(), "New sandbox.teamId must be provided");
        Validate.notNull(clonedSandbox, "Cloned sandbox must be provided");
        Validate.notNull(clonedSandbox.getTeamId(), "Cloned sandbox.teamId must be provided");

        newSandbox.setSchemaVersion(DatabaseProperties.DEFAULT_HSPC_SCHEMA_VERSION);
        clonedSandbox.setSchemaVersion(DatabaseProperties.DEFAULT_HSPC_SCHEMA_VERSION);

        Sandbox existing = checkIfTenantNameIsUnique(newSandbox);

        if (existing == null) {
            sandboxPersister.cloneSandbox(newSandbox, clonedSandbox);
            if (clonedSandbox.isAllowOpenAccess()) {
                tenantInfoRequestMatcher.addOpenTeamId(newSandbox.getTeamId());
            } else {
                tenantInfoRequestMatcher.removeOpenTeamId(newSandbox.getTeamId());
            }
            return;
        }

        if (clonedSandbox.isAllowOpenAccess()) {
            tenantInfoRequestMatcher.addOpenTeamId(newSandbox.getTeamId());
        } else {
            tenantInfoRequestMatcher.removeOpenTeamId(newSandbox.getTeamId());
        }

        throw new IllegalArgumentException("The new sandbox already exists");
    }

    @Override
    public Sandbox get(String teamId) {
        Sandbox sandbox;
        try {
            sandbox = sandboxPersister.findSandbox(teamId);
        } catch (SchemaNotInitializedException e) {
            sandbox = save(SandboxPersister.sandboxTemplate().setTeamId(teamId), DataSet.DEFAULT);
        }

        return sandbox;
    }

    @Override
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

    @Override
    public Sandbox reset(String teamId, DataSet dataSet) {
        Sandbox existing = get(teamId);

        if (existing == null) {
            throw new RuntimeException("Unable to reset sandbox because sandbox does not exist: [" + teamId + "]");
        } else {
            boolean deleted = delete(existing);
            if (!deleted) {
                throw new RuntimeException("Unable to reset sandbox because existing could not be deleted: [" + teamId + "]");
            }
        }

        return save(SandboxPersister.sandboxTemplate().setTeamId(teamId), dataSet);
    }

    private Sandbox checkIfTenantNameIsUnique(Sandbox sandbox) {
        try {
            Sandbox existing = sandboxPersister.findSandbox(sandbox.getTeamId());
            logger.info("Existing sandbox: " + existing);
            if (existing == null) {
                // check that the sandbox is unique across versions
                if (!sandboxPersister.isTeamIdUnique(sandbox.getTeamId())) {
                    throw new RuntimeException("TeamId [" + sandbox.getTeamId() + "] is not unique");
                }
            }
            return existing;
        } catch (SchemaNotInitializedException e) {
            logger.info("SchemaNotInitializedException ignored for now");
            // ignore, will be fixed when saving
        }
        return null;
    }
    
    @Override
    public Set<String> getSandboxSnapshots(String teamId) {
        logger.info("getSandboxSnapshots for: " + teamId);
        Sandbox existing = get(teamId);

        if (existing == null) {
            throw new RuntimeException("Unable to take snapshot of sandbox that does not exist: [" + teamId + "]");
        }

        Set<String> results = sandboxPersister.getSnapshots(existing);
        logger.info("found snapshots: {" + String.join(", ", results) + "}");
        return results;
    }
    
    @Override
    public String takeSnapshot(String teamId, String snapshotId) {
        Sandbox existing = get(teamId);

        if (existing == null) {
            throw new RuntimeException("Unable to take snapshot of sandbox that does not exist: [" + teamId + "]");
        }

        return sandboxPersister.takeSnapshot(existing, snapshotId);
    }

    @Override
    public String restoreSnapshot(String teamId, String snapshotId) {
        logger.info("restoreSnapshot called for " + teamId + ", " + snapshotId);
        Sandbox existing = get(teamId);

        if (existing == null) {
            throw new RuntimeException("Unable to restore snapshot of sandbox that does not exist: [" + teamId + "]");
        }
        logger.info("existing: " + existing.getTeamId());

        return sandboxPersister.restoreSnapshot(existing, snapshotId);
    }

    @Override
    public String deleteSnapshot(String teamId, String snapshotId) {
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
