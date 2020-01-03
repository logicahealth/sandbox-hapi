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

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TenantInfo implements Serializable {

    private String tenantId;

    private String hspcSchemaVersion;

    private boolean allowOpenEndpoint;

    private LocalDate baselineDate;

    private Map<String, String> properties = new HashMap<>();

    public TenantInfo() {
    }

    public TenantInfo(String tenantId, String hspcSchemaVersion, boolean allowOpenEndpoint) {
        this.tenantId = tenantId;
        this.hspcSchemaVersion = hspcSchemaVersion;
        this.allowOpenEndpoint = allowOpenEndpoint;
    }

    public String getHspcSchemaVersion() {
        return hspcSchemaVersion;
    }

    public TenantInfo setHspcSchemaVersion(String hspcSchemaVersion) {
        this.hspcSchemaVersion = hspcSchemaVersion;
        return this;
    }

    public boolean isAllowOpenEndpoint() {
        return allowOpenEndpoint;
    }

    public TenantInfo setAllowOpenEndpoint(boolean allowOpenEndpoint) {
        this.allowOpenEndpoint = allowOpenEndpoint;
        return this;
    }

    public String getTenantId() {
        return tenantId;
    }

    public TenantInfo setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public LocalDate getBaselineDate() {
        return baselineDate;
    }

    public TenantInfo setBaselineDate(LocalDate baselineDate) {
        this.baselineDate = baselineDate;
        return this;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        if (properties == null) {
            this.properties = new HashMap<>();
        } else {
            this.properties = properties;
        }
    }

    public String getPropertiesAsString() {
        String result = properties.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(","));
        return (result.length() > 0 ? result : null);
    }

    public void setProperties(String propertiesAsString) {
        properties = new HashMap<>();
        if (propertiesAsString == null) {
            this.properties = new HashMap<>();
        } else {
            String[] propertyArray = propertiesAsString.split(",");
            for (String property : propertyArray) {
                String s[] = property.split("=");
                if (s.length > 0) {
                    properties.put(
                            s[0],
                            (s.length == 1 || s[1].equals("") || s[1].equals("null"))
                                    ? ""
                                    : s[1]
                    );
                }
            }
        }
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("tenantId", tenantId)
                .append("hspcSchemaVersion", hspcSchemaVersion)
                .append("allowOpenEndpoint", allowOpenEndpoint)
                .append("baselineDate", baselineDate)
                .append("properties", getPropertiesAsString())
                .toString();
    }
}
