package org.hspconsortium.platform.api.fhir.service;

import org.hspconsortium.platform.api.fhir.model.DataSet;
import org.hspconsortium.platform.api.fhir.model.Sandbox;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;

public interface SandboxService {
    void reset();

    Collection<String> allTenantNames();

    Collection<Sandbox> allSandboxes();

    Sandbox save(@NotNull Sandbox sandbox, @NotNull DataSet dataSet);

    void clone(@NotNull Sandbox newSandbox, @NotNull Sandbox clonedSandbox);

    Sandbox get(String teamId);

    boolean remove(String teamId);

    Sandbox reset(String teamId, DataSet dataSet);

    boolean verifyUser(HttpServletRequest request, String sandboxId);

    Set<String> getSandboxSnapshots(String teamId);

    String takeSnapshot(String teamId, String snapshotId);

    String restoreSnapshot(String teamId, String snapshotId);

    String deleteSnapshot(String teamId, String snapshotId);
}
