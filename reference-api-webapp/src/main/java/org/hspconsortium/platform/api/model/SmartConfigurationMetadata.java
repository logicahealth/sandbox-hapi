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

package org.hspconsortium.platform.api.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties("smart-configuration-metadata")
@Configuration
public class SmartConfigurationMetadata {

    private String authorization_endpoint;
    private String token_endpoint;
    private List<String> token_endpoint_auth_methods;
    private String registration_endpoint;
    private List<String> scopes_supported;
    private List<String> response_types_supported;
    private String management_endpoint;
    private String introspection_endpoint;
    private String revocation_endpoint;
    private List<String> capabilities;

    public String getAuthorization_endpoint() {
        return authorization_endpoint;
    }

    public void setAuthorization_endpoint(String authorization_endpoint) {
        this.authorization_endpoint = authorization_endpoint;
    }

    public String getToken_endpoint() {
        return token_endpoint;
    }

    public void setToken_endpoint(String token_endpoint) {
        this.token_endpoint = token_endpoint;
    }

    public List<String> getToken_endpoint_auth_methods() {
        return token_endpoint_auth_methods;
    }

    public void setToken_endpoint_auth_methods(List<String> token_endpoint_auth_methods) {
        this.token_endpoint_auth_methods = token_endpoint_auth_methods;
    }

    public String getRegistration_endpoint() {
        return registration_endpoint;
    }

    public void setRegistration_endpoint(String registration_endpoint) {
        this.registration_endpoint = registration_endpoint;
    }

    public List<String> getScopes_supported() {
        return scopes_supported;
    }

    public void setScopes_supported(List<String> scopes_supported) {
        this.scopes_supported = scopes_supported;
    }

    public List<String> getResponse_types_supported() {
        return response_types_supported;
    }

    public void setResponse_types_supported(List<String> response_types_supported) {
        this.response_types_supported = response_types_supported;
    }

    public String getManagement_endpoint() {
        return management_endpoint;
    }

    public void setManagement_endpoint(String management_endpoint) {
        this.management_endpoint = management_endpoint;
    }

    public String getIntrospection_endpoint() {
        return introspection_endpoint;
    }

    public void setIntrospection_endpoint(String introspection_endpoint) {
        this.introspection_endpoint = introspection_endpoint;
    }

    public String getRevocation_endpoint() {
        return revocation_endpoint;
    }

    public void setRevocation_endpoint(String revocation_endpoint) {
        this.revocation_endpoint = revocation_endpoint;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }

    // ************************ Constructor ****************

    public SmartConfigurationMetadata() { }

    public SmartConfigurationMetadata(String authorization_endpoint,
                                      String token_endpoint,
                                      List<String> token_endpoint_auth_methods,
                                      String registration_endpoint,
                                      List<String> scopes_supported,
                                      List<String> response_types_supported,
                                      String management_endpoint,
                                      String introspection_endpoint,
                                      String revocation_endpoint,
                                      List<String> capabilities) {
        this.authorization_endpoint = authorization_endpoint;
        this.token_endpoint = token_endpoint;
        this.token_endpoint_auth_methods = token_endpoint_auth_methods;
        this.registration_endpoint = registration_endpoint;
        this.scopes_supported = scopes_supported;
        this.response_types_supported = response_types_supported;
        this.management_endpoint = management_endpoint;
        this.introspection_endpoint = introspection_endpoint;
        this.revocation_endpoint = revocation_endpoint;
        this.capabilities = capabilities;
    }

}


