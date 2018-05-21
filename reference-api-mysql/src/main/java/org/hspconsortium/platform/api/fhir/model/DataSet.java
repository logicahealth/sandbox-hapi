package org.hspconsortium.platform.api.fhir.model;

public enum DataSet {
    NONE("none"),
    DEFAULT("default");

    String value;

    DataSet(String value) {
        this.value = value;
    }
};