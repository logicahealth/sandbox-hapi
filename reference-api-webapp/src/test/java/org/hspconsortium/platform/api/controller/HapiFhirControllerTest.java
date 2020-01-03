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
import org.hspconsortium.platform.api.smart.LaunchOrchestrationSendEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringRunner.class)
//@WebMvcTest(value = HapiFhirController.class, secure = false)
//@ContextConfiguration(classes = HapiFhirController.class)
public class HapiFhirControllerTest {

    @Autowired
    private MockMvc mvc;

    private WebApplicationContext myAppCtx = mock(WebApplicationContext.class);
    private LaunchOrchestrationSendEndpoint launchOrchestrationEndpoint = mock(LaunchOrchestrationSendEndpoint.class);
    private Environment environment = mock(Environment.class);
    private String fhirContextPath = "data";
    private String openContextPath = "path";
    private HttpServletRequest request = mock(HttpServletRequest.class);
    private HttpServletResponse response = mock(HttpServletResponse.class);

    private HapiFhirController hapiFhirController;

    @Before
    public void setUp() {
        String[] version = {"dstu2"};
        when(environment.getActiveProfiles()).thenReturn(version);
        hapiFhirController = new HapiFhirController(environment, myAppCtx, fhirContextPath, openContextPath);
        ReflectionTestUtils.setField(hapiFhirController, "launchOrchestrationEndpoint", launchOrchestrationEndpoint);
    }

    @Test
    public void getFhirVersionTestDstu2() {
        FhirVersionEnum fhirVersionEnum = hapiFhirController.getFhirVersion(environment);
        assertEquals(FhirVersionEnum.DSTU2, fhirVersionEnum);
    }

    @Test
    public void getFhirVersionTestStu3() {
        String[] version = {"stu3"};
        when(environment.getActiveProfiles()).thenReturn(version);
        FhirVersionEnum fhirVersionEnum = hapiFhirController.getFhirVersion(environment);
        assertEquals(FhirVersionEnum.DSTU3, fhirVersionEnum);
    }

    @Test
    public void getFhirVersionTestR4() {
        String[] version = {"r4"};
        when(environment.getActiveProfiles()).thenReturn(version);
        FhirVersionEnum fhirVersionEnum = hapiFhirController.getFhirVersion(environment);
        assertEquals(FhirVersionEnum.R4, fhirVersionEnum);
    }

    @Test(expected = IllegalStateException.class)
    public void getFhirVersionTestOther() {
        String[] version = {"other"};
        when(environment.getActiveProfiles()).thenReturn(version);
        hapiFhirController.getFhirVersion(environment);
    }

    @Test
    public void smartLaunchHelloTest() throws Exception {
        when(launchOrchestrationEndpoint.hello(request, response)).thenReturn("hello");
//        mvc
//                .perform(get("/_services/smart/Launch"))
//                .andExpect(status().isOk());

        String hello = hapiFhirController.smartLaunchHello(request, response);
        verify(launchOrchestrationEndpoint).hello(request, response);
        assertEquals("hello", hello);
    }

    @Test
    public void smartLaunchTest() throws Exception {
//        when(launchOrchestrationEndpoint.hello(request, response)).thenReturn("hello");
//        mvc
//                .perform(post("/_services/smart/Launch"))
//                .andExpect(status().isOk());

        hapiFhirController.smartLaunch(request, response, "json");
        verify(launchOrchestrationEndpoint).handleLaunchRequest(request, response, "json");
    }
}
