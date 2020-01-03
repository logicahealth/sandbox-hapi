/**
 *  * #%L
 *  *
 *  * %%
 *  * Copyright (C) 2014-2020 Healthcare Services Platform Consortium
 *  * %%
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  * #L%
 */

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
