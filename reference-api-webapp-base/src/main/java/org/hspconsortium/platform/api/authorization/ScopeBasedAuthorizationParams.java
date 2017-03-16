package org.hspconsortium.platform.api.authorization;

public interface ScopeBasedAuthorizationParams {
    String getParamForResource(String resourceTypeString);
}
