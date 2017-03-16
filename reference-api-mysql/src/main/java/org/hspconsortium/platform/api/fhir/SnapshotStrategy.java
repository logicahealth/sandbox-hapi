package org.hspconsortium.platform.api.fhir;

import org.hspconsortium.platform.api.fhir.model.TenantInfo;

public interface SnapshotStrategy {

    TenantInfo takeSnapshot(String sourceSchema, String snapshotKey);

    TenantInfo restoreSnapshot(String sourceSchema, String snapshotKey);

    TenantInfo deleteSnapshot(String sourceSchema, String snapshotKey);
}
