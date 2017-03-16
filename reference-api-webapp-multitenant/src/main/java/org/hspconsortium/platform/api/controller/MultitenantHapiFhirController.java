package org.hspconsortium.platform.api.controller;

import org.hspconsortium.platform.api.model.Sandbox;
import org.hspconsortium.platform.api.oauth2.OAuth2ResourceConfig;
import org.hspconsortium.platform.api.service.SandboxService;
import org.hspconsortium.platform.api.smart.LaunchOrchestrationSendEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.ServletWrappingController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

@RestController
@RequestMapping("/{tenant}")
public class MultitenantHapiFhirController extends ServletWrappingController {

    public static final String SANDBOX_NAME_ATTRIBUTE = "HSPC_SANDBOX_NAME";

    public static final String SANDBOX_OBJECT_ATTRIBUTE = "HSPC_SANDBOX_OBJECT";

    @Autowired
    private WebApplicationContext myAppCtx;


    @Autowired
    private LaunchOrchestrationSendEndpoint launchOrchestrationEndpoint;

    @Autowired
    private SandboxService sandboxService;

    @Value("${spring.profiles.active}")
    private String springProfilesActive;

    @Autowired
    public MultitenantHapiFhirController(Environment env,
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
            "/${hspc.platform.api.fhir.contextPath}",
            "/${hspc.platform.api.fhir.contextPath}/**",
            "/${hspc.platform.api.fhir.openContextPath:" + OAuth2ResourceConfig.NO_ENDPOINT + "}",
            "/${hspc.platform.api.fhir.openContextPath:" + OAuth2ResourceConfig.NO_ENDPOINT + "}/**"
    })
    public void handle(@PathVariable("tenant") String tenant, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        request.setAttribute(SANDBOX_NAME_ATTRIBUTE, tenant);
        Sandbox sandbox = sandboxService.get(tenant);
        request.setAttribute(SANDBOX_OBJECT_ATTRIBUTE, sandbox);
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
}
