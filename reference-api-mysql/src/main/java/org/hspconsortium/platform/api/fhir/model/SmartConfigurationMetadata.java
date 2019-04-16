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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties("smart-configuration-metadata")
@Configuration
public class SmartConfigurationMetadata {

    private String authorizationEndpoint;
    private String tokenEndpoint;
    private List<String> tokenEndpointAuthMethodsSupported;
    private String registrationEndpoint;
    private List<String> scopesSupported;
    private List<String> responseTypesSupported;
    private String managementEndpoint;
    private String introspectionEndpoint;
    private String revocationEndpoint;
    private List<String> capabilities;

    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    public void setAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public List<String> getTokenEndpointAuthMethodsSupported() {
        return tokenEndpointAuthMethodsSupported;
    }

    public void setTokenEndpointAuthMethodsSupported(List<String> tokenEndpointAuthMethodsSupported) {
        this.tokenEndpointAuthMethodsSupported = tokenEndpointAuthMethodsSupported;
    }

    public String getRegistrationEndpoint() {
        return registrationEndpoint;
    }

    public void setRegistrationEndpoint(String registrationEndpoint) {
        this.registrationEndpoint = registrationEndpoint;
    }

    public List<String> getScopesSupported() {
        return scopesSupported;
    }

    public void setScopesSupported(List<String> scopesSupported) {
        this.scopesSupported = scopesSupported;
    }

    public List<String> getResponseTypesSupported() {
        return responseTypesSupported;
    }

    public void setResponseTypesSupported(List<String> responseTypesSupported) {
        this.responseTypesSupported = responseTypesSupported;
    }

    public String getManagementEndpoint() {
        return managementEndpoint;
    }

    public void setManagementEndpoint(String managementEndpoint) {
        this.managementEndpoint = managementEndpoint;
    }

    public String getIntrospectionEndpoint() {
        return introspectionEndpoint;
    }

    public void setIntrospectionEndpoint(String introspectionEndpoint) {
        this.introspectionEndpoint = introspectionEndpoint;
    }

    public String getRevocationEndpoint() {
        return revocationEndpoint;
    }

    public void setRevocationEndpoint(String revocationEndpoint) {
        this.revocationEndpoint = revocationEndpoint;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }

    // ************************ Constructor ****************

    public SmartConfigurationMetadata() { }

    public SmartConfigurationMetadata(String authorizationEndpoint,
                                      String tokenEndpoint,
                                      List<String> tokenEndpointAuthMethodsSupported,
                                      String registrationEndpoint,
                                      List<String> scopesSupported,
                                      List<String> responseTypesSupported,
                                      String managementEndpoint,
                                      String introspectionEndpoint,
                                      String revocationEndpoint,
                                      List<String> capabilities) {
        this.authorizationEndpoint = authorizationEndpoint;
        this.tokenEndpoint = tokenEndpoint;
        this.tokenEndpointAuthMethodsSupported = tokenEndpointAuthMethodsSupported;
        this.registrationEndpoint = registrationEndpoint;
        this.scopesSupported = scopesSupported;
        this.responseTypesSupported = responseTypesSupported;
        this.managementEndpoint = managementEndpoint;
        this.introspectionEndpoint = introspectionEndpoint;
        this.revocationEndpoint = revocationEndpoint;
        this.capabilities = capabilities;
    }

}


