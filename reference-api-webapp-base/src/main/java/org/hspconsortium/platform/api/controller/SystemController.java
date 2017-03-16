package org.hspconsortium.platform.api.controller;

import org.hspconsortium.platform.api.model.ResetSecurityCommand;
import org.hspconsortium.platform.api.service.SandboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/system")
public class SystemController {
    private static final Logger logger = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private SandboxService sandboxService;

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
    public Collection<String> all() {
        return sandboxService.all();
    }

}
