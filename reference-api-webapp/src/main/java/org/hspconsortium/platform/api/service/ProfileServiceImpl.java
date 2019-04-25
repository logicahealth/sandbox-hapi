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

package org.hspconsortium.platform.api.service;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FilenameUtils;
import org.hspconsortium.platform.api.fhir.model.FhirProfile;
import org.hspconsortium.platform.api.fhir.model.ProfileTask;
import org.hspconsortium.platform.api.fhir.model.Sandbox;
import org.hspconsortium.platform.api.fhir.service.ProfileService;
import org.hspconsortium.platform.api.fhir.service.SandboxService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ProfileServiceImpl implements ProfileService {
    private RestTemplate restTemplate = new RestTemplate();

    @Value("${server.localhost}")
    private String localhost;

    @Value("${hspc.platform.api.fhir.profileResources}")
    private String[] profileResources;

    @Value("${hspc.platform.api.sandboxManagerApi.url}")
    private String sandboxManagerApiUrl;

    @Value("${hspc.platform.api.sandboxManagerApi.profilePath}")
    private String profilePath;


    private HashMap<String, ProfileTask> idProfileTask = new HashMap<>();

    public ProfileTask getTaskRunning(String id) {
        return idProfileTask.get(id);
    }

    public HashMap<String, ProfileTask> getIdProfileTask() {
        return idProfileTask;
    }

    private SandboxService sandboxService;

    @Autowired
    public ProfileServiceImpl(SandboxService sandboxService) {
        this.sandboxService = sandboxService;
    }

    @Async("taskExecutor")
    public void saveZipFile (ZipFile zipFile, HttpServletRequest request, String sandboxId, String apiEndpoint, String id, String profileName, String profileId) throws IOException {
        String authToken = request.getHeader("Authorization");
        List<String> resourceSaved = new ArrayList<>();
        List<String> resourceNotSaved = new ArrayList<>();
        int totalCount = 0;
        int resourceSavedCount = 0;
        int resourceNotSavedCount = 0;
        ProfileTask profileTask = addToProfileTask(id, true, resourceSaved, resourceNotSaved, totalCount, resourceSavedCount, resourceNotSavedCount );
        List<FhirProfile> fhirProfiles = new ArrayList<>();
        Enumeration zipFileEntries = zipFile.entries();
        while(zipFileEntries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String fileName = entry.getName();
            if (fileName.endsWith(".json")) {
                InputStream inputStream = zipFile.getInputStream(entry);
                JSONObject profileTaskAndFhirProfile = saveProfileResource(authToken, sandboxId, apiEndpoint, id, inputStream, fileName, profileTask, profileName, profileId);
                profileTask = (ProfileTask) profileTaskAndFhirProfile.get("profileTask");
                if (profileTask.getError() != null) {
                    break;
                }
                FhirProfile fhirProfile = (FhirProfile) profileTaskAndFhirProfile.get("fhirProfile");
                if (fhirProfile != null) {
                    fhirProfiles.add(fhirProfile);
                }
            }
        }
        profileTask.setStatus(false);
        idProfileTask.put(id, profileTask);
        sendProfileToSandboxManagerApi(fhirProfiles, authToken);
    }

    @Async("taskExecutor")
    public void saveTGZfile (MultipartFile file, HttpServletRequest request, String sandboxId, String apiEndpoint, String id, String profileName, String profileId) throws IOException {
        //TODO: implement the new changes as saveZipFile
       String authToken = request.getHeader("Authorization");
        List<String> resourceSaved = new ArrayList<>();
        List<String> resourceNotSaved = new ArrayList<>();
        int totalCount = 0;
        int resourceSavedCount = 0;
        int resourceNotSavedCount = 0;
        ProfileTask profileTask = addToProfileTask(id, true, resourceSaved, resourceNotSaved, totalCount, resourceSavedCount, resourceNotSavedCount );
        InputStream fileInputStream = file.getInputStream();
        TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(new GzipCompressorInputStream(fileInputStream));
        TarArchiveEntry entry;
        while ((entry = tarArchiveInputStream.getNextTarEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }
            String fileName = entry.getName();
            String fileExtension = FilenameUtils.getExtension(fileName);
            if (fileExtension.equals("json")) {
                profileTask = (ProfileTask) saveProfileResource(authToken, sandboxId, apiEndpoint, id, tarArchiveInputStream, fileName, profileTask, profileName, profileId).get("profileTask");
                if (profileTask.getError() != null) {
                    break;
                }
            }
        }
        tarArchiveInputStream.close();
        profileTask.setStatus(false);
        idProfileTask.put(id, profileTask);
    }

    private ProfileTask addToProfileTask(String id, Boolean runStatus, List<String> resourceSaved,
                                        List<String> resourceNotSaved, int totalCount, int resourceSavedCount,
                                        int resourceNotSavedCount){
        ProfileTask profileTask = new ProfileTask();
        profileTask.setId(id);
        profileTask.setStatus(runStatus);
        profileTask.setResourceSaved(resourceSaved);
        profileTask.setResourceNotSaved(resourceNotSaved);
        profileTask.setTotalCount(totalCount);
        profileTask.setResourceSavedCount(resourceSavedCount);
        profileTask.setResourceNotSavedCount(resourceNotSavedCount);
        return profileTask;
    }

    private JSONObject saveProfileResource(String authToken, String sandboxId, String apiEndpoint, String id, InputStream inputStream, String fileName, ProfileTask profileTask, String profileName, String profileId) {
        JSONObject profileTaskAndFhirProfile = new JSONObject();
        List<String> resourceSaved = profileTask.getResourceSaved();
        List<String> resourceNotSaved = profileTask.getResourceNotSaved();
        int totalCount = profileTask.getTotalCount();
        int resourceSavedCount = profileTask.getResourceSavedCount();
        int resourceNotSavedCount = profileTask.getResourceNotSavedCount();
        profileTask = addToProfileTask(id, true, resourceSaved, resourceNotSaved, totalCount, resourceSavedCount, resourceNotSavedCount);
        idProfileTask.put(id, profileTask);
        profileTaskAndFhirProfile.put("profileTask", profileTask);
        FhirProfile fhirProfile = new FhirProfile();
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(inputStream, "UTF-8"));
            String resourceType = jsonObject.get("resourceType").toString();
            if (Arrays.stream(profileResources).anyMatch(resourceType::equals)) {
                String resourceId = jsonObject.get("id").toString();
                String fullUrl = jsonObject.get("url").toString();
                if (resourceType.equals("StructureDefinition")) {
                    String fhirVersion = jsonObject.get("fhirVersion").toString();
                    String profileType = jsonObject.get("type").toString();
                    fhirProfile.setProfileType(profileType);
                    String errorMessage = "";
                    if (apiEndpoint.equals("8") && !fhirVersion.equals("1.0.2")) {
                        errorMessage = fileName + " FHIR version (" + fhirVersion + ") is incompatible with your current sandbox's FHIR version (1.0.2). The profile was not saved.";
                        profileTask.setError(errorMessage);
                        profileTask.setStatus(false);
                        idProfileTask.put(id, profileTask);
                        profileTaskAndFhirProfile.put("profileTask", profileTask);
                        return profileTaskAndFhirProfile;
                    } else if (apiEndpoint.equals("9") && !fhirVersion.equals("3.0.1")) {
                        errorMessage = fileName + " FHIR version (" + fhirVersion + ") is incompatible with your current sandbox's FHIR version (3.0.1). The profile was not saved.";
                        profileTask.setError(errorMessage);
                        profileTask.setStatus(false);
                        idProfileTask.put(id, profileTask);
                        profileTaskAndFhirProfile.put("profileTask", profileTask);
                        return profileTaskAndFhirProfile;
                    } else if (apiEndpoint.equals("10") && !fhirVersion.equals("4.0.0")) {
                        errorMessage = fileName + " FHIR version (" + fhirVersion + ") is incompatible with your current sandbox's FHIR version (4.0.0). The profile was not saved.";
                        profileTask.setError(errorMessage);
                        profileTask.setStatus(false);
                        idProfileTask.put(id, profileTask);
                        profileTaskAndFhirProfile.put("profileTask", profileTask);
                        return profileTaskAndFhirProfile;
                    }
                }
                String jsonBody = jsonObject.toString();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", authToken);
                headers.set("Content-Type", "application/json");
                String url = localhost + "/" + sandboxId + "/data/" + resourceType + "/" + resourceId;
                HttpEntity entity = new HttpEntity(jsonBody, headers);
                try {
                    restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
                    resourceSaved.add(resourceType + " - " + resourceId);
                    totalCount++;
                    resourceSavedCount++;
                    profileTask = addToProfileTask(id, true, resourceSaved, resourceNotSaved, totalCount, resourceSavedCount, resourceNotSavedCount);
                    idProfileTask.put(id, profileTask);
                    profileTaskAndFhirProfile.put("profileTask", profileTask);

//                    Sandbox sandbox = sandboxService.get(sandboxId);
                    fhirProfile.setProfileName(profileName);
                    fhirProfile.setFullUrl(fullUrl);
                    fhirProfile.setRelativeUrl(resourceType + "/" + resourceId);
//                    fhirProfile.setSandbox(sandbox);
                    fhirProfile.setSandboxId(sandboxId);
                    fhirProfile.setProfileId(profileId);
                    profileTaskAndFhirProfile.put("fhirProfile", fhirProfile);

                } catch (HttpServerErrorException | HttpClientErrorException e) {
                    resourceNotSaved.add(resourceType + " - " + resourceId + " - " + e.getMessage());
                    totalCount++;
                    resourceNotSavedCount++;
                    profileTask = addToProfileTask(id, true, resourceSaved, resourceNotSaved, totalCount, resourceSavedCount, resourceNotSavedCount);
                    idProfileTask.put(id, profileTask);
                    profileTaskAndFhirProfile.put("profileTask", profileTask);
                }
            }
        } catch (Exception e) {

        }
        return profileTaskAndFhirProfile;
    }

    public void sendProfileToSandboxManagerApi(List<FhirProfile> fhirProfiles, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);
        headers.set("Content-Type", "application/json");
        HttpEntity entity = new HttpEntity(fhirProfiles.toString(), headers);
        try {
            restTemplate.exchange(sandboxManagerApiUrl + profilePath, HttpMethod.POST, entity, String.class);
        } catch (HttpServerErrorException | HttpClientErrorException e) {

        }
    }
}


//            if (fileName.endsWith(".xml")) {
//                beginsWith = fileName.substring(0, fileName.indexOf("-"));
//                if(beginsWith.equals("StructureDefinition") || (beginsWith.equals("ValueSet")) || (beginsWith.equals("CodeSystem"))) {
//                    InputStream inputStream = zipFile.getInputStream(entry);
//
//                    try {
//                        BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
//                        StringBuilder stringBuilder = new StringBuilder();
//                        String inline = "";
//                        while ((inline = inputReader.readLine()) != null) {
//                            stringBuilder.append(inline);
//                        }
//                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                        DocumentBuilder builder = factory.newDocumentBuilder();
//                        InputSource source = new InputSource();
//                        source.setCharacterStream(new StringReader(stringBuilder.toString()));
//
//                        Document doc = builder.parse(source);
//
////                        Document doc = builder.parse(new InputSource (new ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8))));
////                        InputStream inputStream2 = new    ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
////                        org.w3c.dom.Document doc = builder.parse(inputStream2);
//
//                        Element element = doc.getDocumentElement();
//                        String id = element.getAttribute("id value");
//
//                        HttpHeaders headers = new HttpHeaders();
//                        headers.set("Authorization", "BEARER " + authToken);
//                        headers.set("Content-Type", "application/xml");
//
//                        String url = localhost + "/" + sandboxId + "/data/" +  beginsWith + "/" + element.getAttribute("id value");
//                        HttpEntity entity = new HttpEntity(stringBuilder.toString(), headers);
//                        try {
//                            restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
//                        } catch (HttpClientErrorException e) {
//                            logger.error("File not saved: " + fileName);
//                        }
//                    } catch (Exception e) {
//                        logger.error(e.getMessage() + "Unsupported file " + fileName);
//                    }
//                }
//            }



