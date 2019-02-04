package org.hspconsortium.platform.api.controller;

import org.codehaus.plexus.util.IOUtil;
import org.hspconsortium.platform.api.fhir.service.ProfileService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ProfileService profileService;

    @ResponseBody
    @GetMapping(value = "/getAllProfiles", params = {"sandboxId"})
    public HashMap<String, List<JSONObject>> getAllUploadedProfiles(HttpServletRequest request, @RequestParam(value = "sandboxId") String sandboxId) {
        //TODO: figure out security here
        return profileService.getAllUploadedProfiles(request, sandboxId);
    }

    @ResponseBody
    @GetMapping(value = "/getAllProfilesOriginal", params = {"sandboxId"})
    public HashMap<String, HashMap<String, String>> getAllUploadedProfilesOriginal(HttpServletRequest request, @RequestParam(value = "sandboxId") String sandboxId) {
        //TODO: figure out security here
        return profileService.getAllUploadedProfilesOriginal(request, sandboxId);
    }

    @PostMapping(value = "/uploadProfile")
    public void uploadProfile (@RequestParam("file") MultipartFile file, HttpServletRequest request, String sandboxId) throws IOException {
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
//        finally {
//            zip.delete();
//        }
    }

}

