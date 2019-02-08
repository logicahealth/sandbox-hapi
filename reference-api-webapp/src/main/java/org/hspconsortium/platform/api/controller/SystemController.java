package org.hspconsortium.platform.api.controller;

import org.codehaus.jackson.map.util.JSONPObject;
import org.codehaus.plexus.util.IOUtil;
import org.hspconsortium.platform.api.fhir.DataSourceRepository;
import org.hspconsortium.platform.api.fhir.model.ResetSecurityCommand;
import org.hspconsortium.platform.api.fhir.model.Sandbox;
import org.hspconsortium.platform.api.fhir.service.SandboxService;
import org.hspconsortium.platform.api.fhir.service.SystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

@RestController
@RequestMapping("/system")
public class SystemController {
    private static final Logger logger = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private SandboxService sandboxService;

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Autowired
    private SystemService systemService;

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

    @RequestMapping(value = "/getAllUploadedProfiles", method = RequestMethod.GET)
    public HashMap<String, String> getAllUploadedProfiles(HttpServletRequest request, String sandboxId) {
        //TODO: figure out security here
        return systemService.getAllUploadedProfiles(request, sandboxId);
    }

    @RequestMapping(value = "/uploadProfile", method = RequestMethod.POST)
    public void uploadProfile (@RequestParam("file") MultipartFile file, HttpServletRequest request, String sandboxId) throws IOException {
        // Save file to temp
        File zip = File.createTempFile(UUID.randomUUID().toString(), "temp");
        FileOutputStream o = new FileOutputStream(zip);
        IOUtil.copy(file.getInputStream(), o);
        o.close();

        try {
            ZipFile zipFile = new ZipFile(zip);
            systemService.saveZipFile(zipFile, request, sandboxId);
        } catch (ZipException e) {
            e.printStackTrace();
        }
//        finally {
//            zip.delete();
//        }
    }

}
