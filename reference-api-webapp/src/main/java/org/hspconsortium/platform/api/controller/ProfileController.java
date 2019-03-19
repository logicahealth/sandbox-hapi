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
import org.hspconsortium.platform.api.fhir.model.ProfileTask;
import org.hspconsortium.platform.api.fhir.service.ProfileService;
import org.hspconsortium.platform.api.fhir.service.SandboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    @PostMapping(value = "/uploadProfile", params = {"sandboxId", "apiEndpoint"})
    public ResponseEntity<Object> uploadProfile (@RequestParam("file") MultipartFile file, HttpServletRequest request, @RequestParam(value = "sandboxId") String sandboxId, @RequestParam(value = "apiEndpoint") String apiEndpoint) throws IOException {
        String id = UUID.randomUUID().toString();
        if(!sandboxService.verifyUser(request, sandboxId)) {
            throw new UnauthorizedUserException("User not authorized");
        }
        if (!file.getOriginalFilename().isEmpty()) {
            File zip = File.createTempFile(UUID.randomUUID().toString(), "temp");
            FileOutputStream o = new FileOutputStream(zip);
            IOUtil.copy(file.getInputStream(), o);
            o.close();
            try {
                ZipFile zipFile = new ZipFile(zip);
                profileService.saveZipFile(zipFile, request, sandboxId, apiEndpoint, id);
            } catch (ZipException e) {
                e.printStackTrace();
            }
            finally {
                zip.delete();
            }
        } else {
            return new ResponseEntity<>("Invalid File", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @RequestMapping(value = "/profileUploadStatus", params = {"id"})
    @ResponseBody
    public ProfileTask fetchStatus(@RequestParam(value = "id") String id) {
        ProfileTask profileTask = profileService.getTaskRunning(id);
        ProfileTask profileTaskCopy = profileTask;
        if (!profileTask.getStatus()){
            profileService.getIdProfileTask().remove(id);
        }
        return profileTaskCopy;
    }
}
