package org.hspconsortium.platform.api.persister;

public class SchemaNotInitializedException extends Exception {

    public static final String MESSAGE = "Schema is not initialized";

    private String instanceMesssage;

    public SchemaNotInitializedException() {
        super(MESSAGE);
    }

    public String getInstanceMesssage() {
        return instanceMesssage;
    }

    public SchemaNotInitializedException forTeam(String teamId) {
        this.instanceMesssage = MESSAGE + " for: " + teamId;
        return this;
    }
}
