package org.hspconsortium.platform.api.fhir.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDate;

// Don't leak schema version out of the API server
@JsonIgnoreProperties(value = "schemaVersion")
public class Sandbox implements Serializable {

    private String teamId;

    private String schemaVersion;

    private boolean allowOpenAccess;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate baselineDate;
    
    private String properties;

    protected Sandbox() {
    }

    public Sandbox(String teamId) {
        this.teamId = teamId;
    }

    public Sandbox(String teamId, String schemaVersion, boolean allowOpenAccess) {
        this.teamId = teamId;
        this.schemaVersion = schemaVersion;
        this.allowOpenAccess = allowOpenAccess;
        this.baselineDate = null;
        this.properties = null;
    }

    public String getTeamId() {
        return teamId;
    }

    public Sandbox setTeamId(String teamId) {
        this.teamId = teamId;
        return this;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public Sandbox setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
        return this;
    }

    public boolean isAllowOpenAccess() {
        return allowOpenAccess;
    }

    public Sandbox setAllowOpenAccess(boolean allowOpenAccess) {
        this.allowOpenAccess = allowOpenAccess;
        return this;
    }

    public LocalDate getBaselineDate() {
        return baselineDate;
    }

    public Sandbox setBaselineDate(LocalDate baselineDate) {
        this.baselineDate = baselineDate;
        return this;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "{" +
                "\"teamId\": \"" + teamId + "\"" +
                ", \"schemaVersion\": \"" + schemaVersion + "\"" +
                ", \"allowOpenAccess\": \"" + allowOpenAccess + "\"" +
                ", \"baselineDate\": \"" + baselineDate + "\"" +
                ", \"properties\": \"" + properties + "\"" +
                '}';
    }
}
