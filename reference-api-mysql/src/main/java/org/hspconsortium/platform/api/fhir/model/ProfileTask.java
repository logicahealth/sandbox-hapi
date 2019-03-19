package org.hspconsortium.platform.api.fhir.model;

import java.util.List;

public class ProfileTask {
    private String id;
    private Boolean status;
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
