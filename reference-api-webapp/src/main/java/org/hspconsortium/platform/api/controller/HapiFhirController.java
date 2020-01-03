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

import ca.uhn.fhir.context.FhirVersionEnum;
import org.hspconsortium.platform.api.oauth2.OAuth2ResourceConfig;
import org.hspconsortium.platform.api.smart.LaunchOrchestrationSendEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.ServletWrappingController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

@RestController
@Profile("default")
public class HapiFhirController extends ServletWrappingController {

    public static final String DSTU2_PROFILE_NAME = "dstu2";
    public static final String STU3_PROFILE_NAME = "stu3";
    public static final String R4_PROFILE_NAME = "r4";

    @Autowired
    private WebApplicationContext myAppCtx;

    @Autowired
    private LaunchOrchestrationSendEndpoint launchOrchestrationEndpoint;

    @Autowired
    private Environment environment;


    @Autowired
    public HapiFhirController(Environment env,
                              WebApplicationContext myAppCtx,
                              @Value("${hspc.platform.api.fhir.contextPath:data}") String fhirContextPath,
                              @Value("${hspc.platform.api.fhir.openContextPath:" + OAuth2ResourceConfig.NO_ENDPOINT + "}") String openContextPath) {
        setServletClass(HapiFhirServlet.class);
        setServletName("hapiFhirServlet");
        setSupportedMethods(
                RequestMethod.GET.toString(),
                RequestMethod.PUT.toString(),
                RequestMethod.POST.toString(),
                RequestMethod.PATCH.toString(),
                RequestMethod.DELETE.toString(),
                RequestMethod.HEAD.toString(),
                RequestMethod.OPTIONS.toString(),
                RequestMethod.TRACE.toString()
        );

        HapiFhirServletContextHolder.getInstance().init(myAppCtx, fhirContextPath, openContextPath, HapiFhirController.getFhirVersion(env));
    }

    @Override
    public void setInitParameters(Properties initParameters) {
        super.setInitParameters(initParameters);
    }

    @RequestMapping(value = {
            "/${hspc.platform.api.fhir.contextPath:data}",
            "/${hspc.platform.api.fhir.contextPath:data}/**",
            "/${hspc.platform.api.fhir.openContextPath:" + OAuth2ResourceConfig.NO_ENDPOINT + "}",
            "/${hspc.platform.api.fhir.openContextPath:" + OAuth2ResourceConfig.NO_ENDPOINT + "}/**"
    })
    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.handleRequest(request, response);
    }

    @RequestMapping(value = "/${hspc.platform.api.fhir.contextPath}/_services/smart/Launch", method = RequestMethod.GET)
    public String smartLaunchHello(HttpServletRequest request, HttpServletResponse response) {
        return launchOrchestrationEndpoint.hello(request, response);
    }

    @RequestMapping(value = "/${hspc.platform.api.fhir.contextPath}/_services/smart/Launch", method = RequestMethod.POST)
    public void smartLaunch(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonString) {
        launchOrchestrationEndpoint.handleLaunchRequest(request, response, jsonString);
    }

    public static FhirVersionEnum getFhirVersion(Environment env) {

        for (String curProfile : env.getActiveProfiles()) {
            if (curProfile.equals(HapiFhirController.DSTU2_PROFILE_NAME)) {
                return FhirVersionEnum.DSTU2;
            } else if (curProfile.equals(HapiFhirController.STU3_PROFILE_NAME)) {
                return FhirVersionEnum.DSTU3;
            } else if(curProfile.equals(HapiFhirController.R4_PROFILE_NAME)){
                return FhirVersionEnum.R4;
            }
        }

        throw new IllegalStateException("One of the following profiles must be set: [dstu2, stu3, r4]");
    }
}
