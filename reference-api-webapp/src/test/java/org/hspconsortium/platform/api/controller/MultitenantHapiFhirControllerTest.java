package org.hspconsortium.platform.api.controller;

import org.hspconsortium.platform.api.fhir.model.Sandbox;
import org.hspconsortium.platform.api.fhir.service.SandboxService;
import org.hspconsortium.platform.api.smart.LaunchOrchestrationSendEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MultitenantHapiFhirControllerTest {

    private WebApplicationContext myAppCtx = mock(WebApplicationContext.class);
    private LaunchOrchestrationSendEndpoint launchOrchestrationEndpoint = mock(LaunchOrchestrationSendEndpoint.class);
    private HttpServletRequest request = spy(HttpServletRequest.class);
    private HttpServletResponse response = mock(HttpServletResponse.class);
    private Environment environment = mock(Environment.class);
    private SandboxService sandboxService = mock(SandboxService.class);

    private MultitenantHapiFhirController multitenantHapiFhirController;

    @Before
    public void setUp() {
        String[] version = {"dstu2"};
        when(environment.getActiveProfiles()).thenReturn(version);
        multitenantHapiFhirController = new MultitenantHapiFhirController(environment, myAppCtx, "data", "path");
        ReflectionTestUtils.setField(multitenantHapiFhirController, "launchOrchestrationEndpoint", launchOrchestrationEndpoint);
        ReflectionTestUtils.setField(multitenantHapiFhirController, "sandboxService", sandboxService);
    }

    @Test
    public void smartLaunchHelloTest() throws Exception {
        when(launchOrchestrationEndpoint.hello(request, response)).thenReturn("hello");
//        mvc
//                .perform(get("/_services/smart/Launch"))
//                .andExpect(status().isOk());

        String hello = multitenantHapiFhirController.smartLaunchHello(request, response);
        verify(launchOrchestrationEndpoint).hello(request, response);
        assertEquals("hello", hello);
    }

    @Test
    public void smartLaunchTest() throws Exception {
//        when(launchOrchestrationEndpoint.hello(request, response)).thenReturn("hello");
//        mvc
//                .perform(post("/_services/smart/Launch"))
//                .andExpect(status().isOk());

        multitenantHapiFhirController.smartLaunch(request, response, "json");
        verify(launchOrchestrationEndpoint).handleLaunchRequest(request, response, "json");
    }
}
