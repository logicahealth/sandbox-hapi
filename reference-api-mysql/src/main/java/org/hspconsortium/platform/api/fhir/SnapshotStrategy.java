package org.hspconsortium.platform.api.fhir;

public interface SnapshotStrategy {

    String takeSnapshot(String sourceSchema, String snapshotKey);

    String restoreSnapshot(String sourceSchema, String snapshotKey);

    String deleteSnapshot(String sourceSchema, String snapshotKey);
}
