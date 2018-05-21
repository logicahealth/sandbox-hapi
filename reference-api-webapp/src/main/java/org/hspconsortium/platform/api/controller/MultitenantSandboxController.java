package org.hspconsortium.platform.api.controller;

import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import org.apache.commons.lang3.Validate;
import org.hspconsortium.platform.api.fhir.model.DataSet;
import org.hspconsortium.platform.api.fhir.model.ResetSandboxCommand;
import org.hspconsortium.platform.api.fhir.model.Sandbox;
import org.hspconsortium.platform.api.fhir.model.SnapshotSandboxCommand;
import org.hspconsortium.platform.api.fhir.service.SandboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Set;

@RestController
@RequestMapping("${hspc.platform.api.sandboxPath:/{teamId}/sandbox}")
public class MultitenantSandboxController {

    private static final Logger logger = LoggerFactory.getLogger(MultitenantSandboxController.class);

    private SandboxService sandboxService;

    @Autowired
    public MultitenantSandboxController(SandboxService sandboxService) {
        this.sandboxService = sandboxService;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Sandbox save(@PathVariable String teamId, @NotNull @RequestBody Sandbox sandbox,
                        @RequestParam(value = "dataSet", required = false) DataSet dataSet) {
        Validate.notNull(sandbox);
        Validate.notNull(sandbox.getTeamId());

//        don't validate the name to allow for initialization
//        validate(teamId);

        Validate.isTrue(teamId.equals(sandbox.getTeamId()));

        if (dataSet == null) {
            dataSet = DataSet.NONE;
        }

        return sandboxService.save(sandbox, dataSet);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Sandbox get(@PathVariable String teamId) {
        Sandbox existing = sandboxService.get(teamId);
        if (existing == null) {
            throw new ResourceNotFoundException("Sandbox [" + teamId + "] is not found");
        }
        return existing;
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public boolean delete(@PathVariable String teamId) {
        validate(teamId);
        return sandboxService.remove(teamId);
    }

    @RequestMapping(path = "/reset", method = RequestMethod.POST)
    public String reset(@PathVariable String teamId, @RequestBody ResetSandboxCommand resetSandboxCommand) {
        sandboxService.reset(teamId, resetSandboxCommand.getDataSet());
        return "Success";
    }

    @RequestMapping(path = "/snapshot", method = RequestMethod.GET)
    public Set<String> getSnapshots(@PathVariable String teamId) {
        return sandboxService.getSandboxSnapshots(teamId);
    }

    @RequestMapping(path = "/snapshot/{snapshotId}", method = RequestMethod.POST)
    public String snapshot(@PathVariable String teamId,
                            @PathVariable("snapshotId") String snapshotId,
                            @RequestBody SnapshotSandboxCommand snapshotSandboxCommand) {
        Validate.notNull(teamId);
        Validate.notNull(snapshotId);
        Validate.isTrue(snapshotId.matches("^[a-zA-Z0-9]+$"), "Snapshot ID must only contain alphanumeric characters");
        Validate.isTrue(snapshotId.length() < 20, "Snapshot ID must be less than 20 characters");
        Validate.notNull(snapshotSandboxCommand);
        Validate.notNull(snapshotSandboxCommand.getAction());

        logger.info("Sandbox Snapshot action of [" + snapshotSandboxCommand.getAction() + "], requested for " + teamId + ", " + snapshotId);
        switch (snapshotSandboxCommand.getAction()) {
            case Take:
                return sandboxService.takeSnapshot(teamId, snapshotId);
            case Restore:
                return sandboxService.restoreSnapshot(teamId, snapshotId);
            case Delete:
                return sandboxService.deleteSnapshot(teamId, snapshotId);
            default:
                throw new RuntimeException("Unknown sandbox command action: " + snapshotSandboxCommand.getAction());
        }
    }

    @GetMapping("/echo/**")
    public @ResponseBody ResponseEntity<String> echoGet(HttpServletRequest request) {
        String message = "Received " + request.getMethod() + ", request path: " + request.getRequestURI();
        logger.info(message);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/echo/**")
    public @ResponseBody ResponseEntity<String> echoPost(HttpServletRequest request, @RequestBody Object obj) {
        StringBuilder sb = new StringBuilder();
        sb.append("Received " + request.getMethod() + ", request path: " + request.getRequestURI());
        sb.append("Received POST request body: " + obj);
        logger.info(sb.toString());

        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }

    @PutMapping("/echo/**")
    public @ResponseBody ResponseEntity<String> echoPut(HttpServletRequest request, @RequestBody Object obj) {
        StringBuilder sb = new StringBuilder();
        sb.append("Received " + request.getMethod() + ", request path: " + request.getRequestURI());
        sb.append("Received PUT request body: " + obj);
        logger.info(sb.toString());

        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }

    private void validate(String teamId) {
        try {
            SandboxReservedName.valueOf(teamId);
            // oh no, this is a reserved name
            throw new ForbiddenOperationException("Sandbox [" + teamId + "] is not allowed.");
        } catch (IllegalArgumentException e) {
            // good, not a reserved name
        }
    }

}
