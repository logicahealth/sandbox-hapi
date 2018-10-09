package org.hspconsortium.platform.api.controller;

import org.hspconsortium.platform.api.fhir.DataSourceRepository;
import org.hspconsortium.platform.api.fhir.model.ResetSecurityCommand;
import org.hspconsortium.platform.api.fhir.model.Sandbox;
import org.hspconsortium.platform.api.fhir.service.SandboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/system")
public class SystemController {
    private static final Logger logger = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private SandboxService sandboxService;

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @RequestMapping("/")
    public String system() {
        return "System endpoint";
    }

    @RequestMapping(path = "/reset", method = RequestMethod.POST)
    public String reset(@RequestBody ResetSecurityCommand resetSecurityCommand) {
        // reset
        sandboxService.reset();
        logger.info("System Controller reset");
        return "Success";
    }

    @RequestMapping(value = "/sandbox", method = RequestMethod.GET)
    public Collection<String> allSandboxNames() {
        //TODO: figure out security here
        return sandboxService.allTenantNames();
    }

    @RequestMapping(value = "/sandboxObjects", method = RequestMethod.GET)
    public String allSandboxes() {
        //TODO: figure out security here
        return sandboxService.allSandboxes().toString();
    }

    @RequestMapping(value = "/memory", method = RequestMethod.PUT)
    public HashMap<String, Double> memoryAllSandboxes(@RequestBody List<String> sandboxIds) {
        return dataSourceRepository.memoryAllSandboxes(sandboxIds);
    }

    @RequestMapping(value = "/memory/user", method = RequestMethod.PUT)
    public HashMap<String, Double> memoryAllSandboxesOfUser(@RequestBody List<String> sandboxIds) {
        return dataSourceRepository.memoryAllSandboxesOfUser(sandboxIds);
    }

}
