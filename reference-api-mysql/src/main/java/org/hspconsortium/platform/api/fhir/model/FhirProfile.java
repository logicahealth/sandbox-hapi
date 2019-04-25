/**
 *  * #%L
 *  *
 *  * %%
 *  * Copyright (C) 2014-2019 Healthcare Services Platform Consortium
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

public class FhirProfile {

    private String profileName;
    private String fullUrl;
    private String relativeUrl;
    private String sandboxId;
    private String profileId;
    private String profileType;

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public String getRelativeUrl() {
        return relativeUrl;
    }

    public void setRelativeUrl(String relativeUrl) {
        this.relativeUrl = relativeUrl;
    }

    public String getSandboxId() {
        return sandboxId;
    }

    public void setSandboxId(String sandboxId) {
        this.sandboxId = sandboxId;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    @Override
    public String toString() {
        if (profileType == null) {
            return "{" +
                    "\"profileName\": \"" + profileName + "\"" +
                    ", \"fullUrl\": \"" + fullUrl + "\"" +
                    ", \"relativeUrl\": \"" + relativeUrl + "\"" +
                    ", \"sandbox\": " + "{\"sandboxId\": \"" + sandboxId + "\"" + "}" +
                    ", \"profileId\": \"" + profileId + "\"" +
                    ", \"profileType\": " + null +
                    '}';
        } else return "{" +
                "\"profileName\": \"" + profileName + "\"" +
                ", \"fullUrl\": \"" + fullUrl + "\"" +
                ", \"relativeUrl\": \"" + relativeUrl + "\"" +
                ", \"sandbox\": " + "{\"sandboxId\": \"" + sandboxId + "\"" + "}" +
                ", \"profileId\": \"" + profileId + "\"" +
                ", \"profileType\": \"" + profileType + "\"" +
                '}';
    }
}
