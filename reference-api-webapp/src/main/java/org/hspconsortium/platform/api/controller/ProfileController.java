package org.hspconsortium.platform.api.controller;

import org.codehaus.plexus.util.IOUtil;
import org.hspconsortium.platform.api.fhir.service.ProfileService;
import org.hspconsortium.platform.api.fhir.service.SandboxService;
import org.json.simple.JSONObject;
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
import java.util.*;
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

//    @PostMapping(value = "/uploadProfile")
//    public HashMap<List<String>, List<String>> uploadProfile (@RequestParam("file") MultipartFile file, HttpServletRequest request, String sandboxId) throws IOException {
//        if(!sandboxService.verifyUser(request, sandboxId)) {
//            throw new UnauthorizedUserException("User not authorized");
//        }
//        HashMap<List<String>, List<String>> list = new HashMap<>();
//        // Save file to temp
//        File zip = File.createTempFile(UUID.randomUUID().toString(), "temp");
//        FileOutputStream o = new FileOutputStream(zip);
//        IOUtil.copy(file.getInputStream(), o);
//        o.close();
//
//        try {
//            ZipFile zipFile = new ZipFile(zip);
//            list = profileService.saveZipFile(zipFile, request, sandboxId);
//        } catch (ZipException e) {
//            e.printStackTrace();
//        }
//        finally {
//            zip.delete();
//        }
//        return list;
//    }

    @PostMapping(value = "/uploadProfile")
    public ResponseEntity<Object> uploadProfile (@RequestParam("file") MultipartFile file, HttpServletRequest request, String sandboxId) throws IOException {
        if(!sandboxService.verifyUser(request, sandboxId)) {
            throw new UnauthorizedUserException("User not authorized");
        }
        if (!file.getOriginalFilename().isEmpty()) {
            // Save file to temp
            File zip = File.createTempFile(UUID.randomUUID().toString(), "temp");
            FileOutputStream o = new FileOutputStream(zip);
            IOUtil.copy(file.getInputStream(), o);
            o.close();

            try {
                ZipFile zipFile = new ZipFile(zip);
                profileService.saveZipFile(zipFile, request, sandboxId);
            } catch (ZipException e) {
                e.printStackTrace();
            }
            finally {
                zip.delete();
            }
        } else {
            return new ResponseEntity<>("Invalid File", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/profileUploadStatus")
    @ResponseBody
    public boolean fetchStatus() {
        return profileService.getTaskRunning();
    }
}

