package org.hspconsortium.platform.api.controller;

import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.google.common.collect.Sets;
import org.hspconsortium.platform.api.fhir.model.DataSet;
import org.hspconsortium.platform.api.fhir.model.Sandbox;
import org.hspconsortium.platform.api.fhir.service.SandboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Set;

/**
 * @deprecated Move to the SandboxController ASAP!
 */
@RestController
@RequestMapping("/management")
@Deprecated
public class ManagementController {
    private static final Logger logger = LoggerFactory.getLogger(ManagementController.class);

    private static final Set<String> restrictedTenants = Sets.newHashSet(
            "hspc",
            "hspc2",
            "hspc3",
            "hspc4",
            "hspc5",
            "hspc6",
            "hspc7",
            "hspc8",
            "hspc9",
            "hspc10",
            "hspc11",
            "hspc12",
            "test",
            "management",
            "admin"
    );

    @Autowired
    private SandboxService sandboxService;

    @RequestMapping("/")
    public String management() {
        return "Management endpoint";
    }

    @RequestMapping("/reset")
    public String reset() {
        sandboxService.reset();
        logger.info("Management Controller reset");
        return "Management reset";
    }

    @RequestMapping(value = "/sandbox", method = RequestMethod.GET)
    public Collection<String> all() {
        return sandboxService.allTenantNames();
    }

    @RequestMapping(value = "/sandbox/{teamId}", method = RequestMethod.PUT)
    public Sandbox create(@PathVariable String teamId, @RequestBody Sandbox sandbox) {
        validate(teamId);
        return sandboxService.save(sandbox, DataSet.NONE);
//        return sandboxService.addOrReplace(teamId, sandbox);
    }

    @RequestMapping(value = "/sandbox/{teamId}", method = RequestMethod.GET)
    public Sandbox get(@PathVariable String teamId) {
        Sandbox existing = sandboxService.get(teamId);
        if (existing == null) {
            throw new ResourceNotFoundException("Sandbox {" + teamId + "} is not found");
        }
        return existing;
    }

    @RequestMapping(value = "/sandbox/{teamId}", method = RequestMethod.DELETE)
    public boolean delete(@PathVariable String teamId) {
        validate(teamId);
        return sandboxService.remove(teamId);
    }

    private void validate(String teamId) {
        if (restrictedTenants.contains(teamId)) {
            throw new ForbiddenOperationException("Sandbox {" + teamId + "} is not allowed.");
        }
    }

}
