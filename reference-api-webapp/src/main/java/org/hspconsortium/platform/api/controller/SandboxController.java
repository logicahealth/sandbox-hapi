/**
 *  * #%L
 *  *
 *  * %%
 *  * Copyright (C) 2014-2019 Healthcare Services Platform Consortium
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

import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import org.apache.commons.lang3.Validate;
import org.hspconsortium.platform.api.fhir.model.DataSet;
import org.hspconsortium.platform.api.fhir.model.ResetSandboxCommand;
import org.hspconsortium.platform.api.fhir.model.Sandbox;
import org.hspconsortium.platform.api.fhir.model.SnapshotSandboxCommand;
import org.hspconsortium.platform.api.fhir.service.SandboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

@RestController
@RequestMapping("${hspc.platform.api.sandboxPath:/sandbox}")
@Profile("default")
public class SandboxController {

    @Value("${hspc.platform.api.sandbox.name}")
    private String sandboxName;

    private SandboxService sandboxService;

    @Autowired
    public SandboxController(SandboxService sandboxService) {
        this.sandboxService = sandboxService;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Sandbox save(@NotNull @RequestBody Sandbox sandbox, @RequestParam(value = "dataSet", required = false) DataSet dataSet) {
        Validate.notNull(sandbox);
        Validate.notNull(sandbox.getTeamId());

//        don't validate the name to allow for initialization
//        validate(sandbox.getTeamId());
        if (dataSet == null) {
            dataSet = DataSet.NONE;
        }

        return sandboxService.save(sandbox, dataSet);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Sandbox get() {
        Sandbox existing = sandboxService.get(sandboxName);
        if (existing == null) {
            throw new ResourceNotFoundException("Sandbox [" + sandboxName + "] is not found");
        }
        return existing;
    }

    @RequestMapping(path = "/reset", method = RequestMethod.POST)
    public String reset(@RequestBody ResetSandboxCommand resetSandboxCommand) {
        sandboxService.reset(sandboxName, resetSandboxCommand.getDataSet());
        return "Success";
    }

    @RequestMapping(path = "/snapshot", method = RequestMethod.GET)
    public Set<String> getSnapshots() {
        return sandboxService.getSandboxSnapshots(sandboxName);
    }

    @RequestMapping(path = "/snapshot/{snapshotId}", method = RequestMethod.POST)
    public String snapshot(@PathVariable("snapshotId") String snapshotId,
                                @RequestBody SnapshotSandboxCommand snapshotSandboxCommand) {
        Validate.notNull(snapshotId);
        Validate.isTrue(snapshotId.matches("^[a-zA-Z0-9]+$"), "Snapshot ID must only contain alphanumeric characters");
        Validate.isTrue(snapshotId.length() < 20, "Snapshot ID must be less than 20 characters");
        Validate.notNull(snapshotSandboxCommand);
        Validate.notNull(snapshotSandboxCommand.getAction());

        switch (snapshotSandboxCommand.getAction()) {
            case Take:
                return sandboxService.takeSnapshot(sandboxName, snapshotId);
            case Restore:
                return sandboxService.restoreSnapshot(sandboxName, snapshotId);
            case Delete:
                return sandboxService.deleteSnapshot(sandboxName, snapshotId);
            default:
                throw new RuntimeException("Unknown sandbox command action: " + snapshotSandboxCommand.getAction());
        }
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
