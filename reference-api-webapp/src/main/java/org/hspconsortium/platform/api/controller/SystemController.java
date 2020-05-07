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

import org.hspconsortium.platform.api.model.ResetSecurityCommand;
import org.hspconsortium.platform.api.multitenant.db.DataSourceRepository;
import org.hspconsortium.platform.api.service.SandboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

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

    // are these needed anymore?
//    @RequestMapping(value = "/memory", method = RequestMethod.PUT)
//    public HashMap<String, Double> memoryAllSandboxes(@RequestBody List<String> sandboxIds) {
//        return dataSourceRepository.memoryAllSandboxes(sandboxIds);
//    }
//
//    @RequestMapping(value = "/memory/user", method = RequestMethod.PUT)
//    public HashMap<String, Double> memoryAllSandboxesOfUser(@RequestBody List<String> sandboxIds) {
//        return dataSourceRepository.memoryAllSandboxesOfUser(sandboxIds);
//    }
}
