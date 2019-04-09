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

import java.util.List;

public class ProfileTask {
    private String id;
    private Boolean status;
    private String error;
    private List<String> resourceSaved;
    private List<String> resourceNotSaved;
    private int totalCount;
    private int resourceSavedCount;
    private int resourceNotSavedCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<String> getResourceSaved() {
        return resourceSaved;
    }

    public void setResourceSaved(List<String> resourceSaved) {
        this.resourceSaved = resourceSaved;
    }

    public List<String> getResourceNotSaved() {
        return resourceNotSaved;
    }

    public void setResourceNotSaved(List<String> resourceNotSaved) {
        this.resourceNotSaved = resourceNotSaved;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getResourceSavedCount() {
        return resourceSavedCount;
    }

    public void setResourceSavedCount(int resourceSavedCount) {
        this.resourceSavedCount = resourceSavedCount;
    }

    public int getResourceNotSavedCount() {
        return resourceNotSavedCount;
    }

    public void setResourceNotSavedCount(int resourceNotSavedCount) {
        this.resourceNotSavedCount = resourceNotSavedCount;
    }
}
