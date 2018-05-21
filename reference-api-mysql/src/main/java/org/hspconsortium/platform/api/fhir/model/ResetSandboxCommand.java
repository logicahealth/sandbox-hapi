package org.hspconsortium.platform.api.fhir.model;

public class ResetSandboxCommand implements Command {
    private DataSet dataSet;

    public DataSet getDataSet() {
        return dataSet;
    }
}
