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

package org.hspconsortium.platform.api.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hspconsortium.platform.api.model.Sandbox;
import org.hspconsortium.platform.api.model.SmartConfigurationMetadata;
import org.hspconsortium.platform.api.service.SandboxService;
import org.hspconsortium.platform.api.multitenant.TenantManagementService;
import org.hspconsortium.platform.api.oauth2.OAuth2ResourceConfig;
import org.hspconsortium.platform.api.smart.LaunchOrchestrationSendEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.ServletForwardingController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/{tenant}")
public class FhirMultiTenantController extends ServletForwardingController {

    private static Log log = LogFactory.getLog(FhirMultiTenantController.class);
    public static final String SANDBOX_NAME_ATTRIBUTE = "HSPC_SANDBOX_NAME";
    public static final String SANDBOX_OBJECT_ATTRIBUTE = "HSPC_SANDBOX_OBJECT";
    public static final String DSTU2_PROFILE_NAME = "dstu2";
    public static final String STU3_PROFILE_NAME = "stu3";
    public static final String R4_PROFILE_NAME = "r4";

    @Autowired
    private TenantManagementService tenantManagementService;

    @Autowired
    private SmartConfigurationMetadata smartConfigurationMetadata;

    @Autowired
    private LaunchOrchestrationSendEndpoint launchOrchestrationEndpoint;

    @Autowired
    private SandboxService sandboxService;

    public FhirMultiTenantController() {
        this.setServletName("fhirRestServlet");
    }

    @RequestMapping(value = {
            "/${hspc.platform.api.fhir.contextPath}",
            "/${hspc.platform.api.fhir.contextPath}/**",
            "/${hspc.platform.api.fhir.openContextPath:" + OAuth2ResourceConfig.NO_ENDPOINT + "}",
            "/${hspc.platform.api.fhir.openContextPath:" + OAuth2ResourceConfig.NO_ENDPOINT + "}/**"
    })
    public void tenantIdentifier(@PathVariable("tenant") String tenant, HttpServletRequest request, HttpServletResponse response) {

        if (!tenantManagementService.isValidExistingTenant(tenant)) {
            response.setStatus(404);
            return;
        }

        try {
            request.setAttribute(SANDBOX_NAME_ATTRIBUTE, tenant);
            Sandbox sandbox = sandboxService.get(tenant);
            request.setAttribute(SANDBOX_OBJECT_ATTRIBUTE, sandbox);
            this.handleRequest(request, response);

            this.handleRequestInternal(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/${hspc.platform.api.fhir.contextPath}/_services/smart/Launch", method = RequestMethod.GET)
    public String smartLaunchHello(HttpServletRequest request, HttpServletResponse response) {
        return launchOrchestrationEndpoint.hello(request, response);
    }

    @RequestMapping(value = "/${hspc.platform.api.fhir.contextPath}/_services/smart/Launch", method = RequestMethod.POST)
    public void smartLaunch(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonString) {
        launchOrchestrationEndpoint.handleLaunchRequest(request, response, jsonString);
    }

    @RequestMapping(value = { "/${hspc.platform.api.fhir.openContextPath:" + OAuth2ResourceConfig.NO_ENDPOINT + "}/.well-known/smart-configuration" ,
            "/${hspc.platform.api.fhir.contextPath}/.well-known/smart-configuration" }, method = RequestMethod.GET)
    public SmartConfigurationMetadata smartConfiguration() {
        SmartConfigurationMetadata smartConfigurationMetadataReturned = new SmartConfigurationMetadata();
        smartConfigurationMetadataReturned.setAuthorization_endpoint(smartConfigurationMetadata.getAuthorization_endpoint());
        smartConfigurationMetadataReturned.setToken_endpoint(smartConfigurationMetadata.getToken_endpoint());
        smartConfigurationMetadataReturned.setToken_endpoint_auth_methods(smartConfigurationMetadata.getToken_endpoint_auth_methods());
        smartConfigurationMetadataReturned.setRegistration_endpoint(smartConfigurationMetadata.getRegistration_endpoint());
        smartConfigurationMetadataReturned.setScopes_supported(smartConfigurationMetadata.getScopes_supported());
        smartConfigurationMetadataReturned.setResponse_types_supported(smartConfigurationMetadata.getResponse_types_supported());
        smartConfigurationMetadataReturned.setManagement_endpoint(smartConfigurationMetadata.getManagement_endpoint());
        smartConfigurationMetadataReturned.setIntrospection_endpoint(smartConfigurationMetadata.getIntrospection_endpoint());
        smartConfigurationMetadataReturned.setRevocation_endpoint(smartConfigurationMetadata.getRevocation_endpoint());
        smartConfigurationMetadataReturned.setCapabilities(smartConfigurationMetadata.getCapabilities());
        return smartConfigurationMetadataReturned;
    }

}