package org.hspconsortium.platform.api.model;

public enum DataSet {
    NONE("none"),
    DEFAULT("default");

    String value;

    DataSet(String value) {
        this.value = value;
    }
};