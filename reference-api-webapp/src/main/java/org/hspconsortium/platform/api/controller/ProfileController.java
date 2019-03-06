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

import org.codehaus.plexus.util.IOUtil;
import org.hspconsortium.platform.api.fhir.service.ProfileService;
import org.hspconsortium.platform.api.fhir.service.SandboxService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private SandboxService sandboxService;

    @Autowired
    private ProfileService profileService;

    @ResponseBody
    @GetMapping(value = "/getAllProfiles", params = {"sandboxId"})
    public HashMap<String, List<JSONObject>> getAllUploadedProfiles(HttpServletRequest request, @RequestParam(value = "sandboxId") String sandboxId) {
        if(!sandboxService.verifyUser(request, sandboxId)) {
            throw new UnauthorizedUserException("User not authorized");
        }
        return profileService.getAllUploadedProfiles(request, sandboxId);
    }

    @PostMapping(value = "/uploadProfile", params = {"sandboxId", "apiEndpoint"})
    public HashMap<List<String>, List<String>> uploadProfile (@RequestParam("file") MultipartFile file, HttpServletRequest request, @RequestParam(value = "sandboxId") String sandboxId, @RequestParam(value = "apiEndpoint") String apiEndpoint) throws IOException {
        if(!sandboxService.verifyUser(request, sandboxId)) {
            throw new UnauthorizedUserException("User not authorized");
        }
        HashMap<List<String>, List<String>> list = new HashMap<>();
        // Save file to temp
        File zip = File.createTempFile(UUID.randomUUID().toString(), "temp");
        FileOutputStream o = new FileOutputStream(zip);
        IOUtil.copy(file.getInputStream(), o);
        o.close();

        try {
            ZipFile zipFile = new ZipFile(zip);
            list = profileService.saveZipFile(zipFile, request, sandboxId, apiEndpoint);
        } catch (ZipException e) {
            e.printStackTrace();
        }
        finally {
            zip.delete();
        }
        return list;
    }
}

