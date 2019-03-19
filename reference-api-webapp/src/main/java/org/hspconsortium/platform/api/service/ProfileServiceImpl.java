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

import org.hspconsortium.platform.api.fhir.model.ProfileTask;
import org.hspconsortium.platform.api.fhir.service.ProfileService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Component
public class ProfileServiceImpl implements ProfileService {
    private static final Logger logger = LoggerFactory.getLogger(SandboxServiceImpl.class);

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${server.localhost}")
    private String localhost;

    @Value("${hspc.platform.api.fhir.profileResources}")
    private String[] profileResources;

    private HashMap<String, ProfileTask> idProfileTask = new HashMap<>();
    private ProfileTask profileTask;

    public ProfileTask getTaskRunning(String id) {
        return idProfileTask.get(id);
    }

    public HashMap<String, ProfileTask> getIdProfileTask() {
        return idProfileTask;
    }

    @Async
    public void saveZipFile (ZipFile zipFile, HttpServletRequest request, String sandboxId, String apiEndpoint, String id) throws IOException {
        List<String> resourceSaved = new ArrayList<>();
        List<String> resourceNotSaved = new ArrayList<>();
        int totalCount = 0;
        int resourceSavedCount = 0;
        int resourceNotSavedCount = 0;
        addToProfileTask(id, true, resourceSaved, resourceNotSaved, totalCount, resourceSavedCount, resourceNotSavedCount);
        idProfileTask.put(id, profileTask);
        String authToken = request.getHeader("Authorization").substring(7);
        Enumeration zipFileEntries = zipFile.entries();
        while(zipFileEntries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String fileName = entry.getName();
            if (fileName.endsWith(".json")) {
                InputStream inputStream = zipFile.getInputStream(entry);
                JSONParser jsonParser = new JSONParser();
                try {
                    JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(inputStream, "UTF-8"));
                    String resourceType = jsonObject.get("resourceType").toString();
                    if (Arrays.stream(profileResources).anyMatch(resourceType::equals)) {
                        String resourceId = jsonObject.get("id").toString();
                        if (resourceType.equals("StructureDefinition")) {
                            String fhirVersion = jsonObject.get("fhirVersion").toString();
                            if (apiEndpoint.equals("5") && !fhirVersion.equals("1.0.2")) {
                                throw new RuntimeException(fileName + " FHIR version (" + fhirVersion + ") is incompatible with your current sandbox's FHIR version (1.0.2). The profile was not saved.");
                            } else if (apiEndpoint.equals("6") && !fhirVersion.equals("3.0.1")) {
                                throw new RuntimeException(fileName + " FHIR version (" + fhirVersion + ") is incompatible with your current sandbox's FHIR version (3.0.1). The profile was not saved.");
                            } else if (apiEndpoint.equals("7") && !fhirVersion.equals("3.4.0")) {
                                throw new RuntimeException(fileName + " FHIR version (" + fhirVersion + ") is incompatible with your current sandbox's FHIR version (3.4.0). The profile was not saved.");
                            } else if (apiEndpoint.equals("8") && !fhirVersion.equals("1.0.2")) {
                                throw new RuntimeException(fileName + " FHIR version (" + fhirVersion + ") is incompatible with your current sandbox's FHIR version (1.0.2). The profile was not saved.");
                            } else if (apiEndpoint.equals("9") && !fhirVersion.equals("3.0.1")) {
                                throw new RuntimeException(fileName + " FHIR version (" + fhirVersion + ") is incompatible with your current sandbox's FHIR version (3.0.1). The profile was not saved.");
                            } else if (apiEndpoint.equals("10") && !fhirVersion.equals("4.0.0")) {
                                throw new RuntimeException(fileName + " FHIR version (" + fhirVersion + ") is incompatible with your current sandbox's FHIR version (4.0.0). The profile was not saved.");
                            }
                        }
                        String jsonBody = jsonObject.toString();
                        HttpHeaders headers = new HttpHeaders();
                        headers.set("Authorization", "BEARER " + authToken);
                        headers.set("Content-Type", "application/json");
                        String url = localhost + "/" + sandboxId + "/data/" + resourceType + "/" + resourceId;
                        HttpEntity entity = new HttpEntity(jsonBody, headers);
                        try {
                            restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
                            resourceSaved.add(resourceType + " - " + resourceId);
                            totalCount++;
                            resourceSavedCount++;
                            addToProfileTask(id, true, resourceSaved, resourceNotSaved, totalCount, resourceSavedCount, resourceNotSavedCount);
                            idProfileTask.put(id, profileTask);
                        } catch (HttpServerErrorException | HttpClientErrorException e) {
                            resourceNotSaved.add(resourceType + " - " + resourceId + " - " + e.getMessage());
                            totalCount++;
                            resourceNotSavedCount++;
                            addToProfileTask(id, true, resourceSaved, resourceNotSaved, totalCount, resourceSavedCount, resourceNotSavedCount);
                            idProfileTask.put(id, profileTask);
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
        addToProfileTask(id, false, resourceSaved, resourceNotSaved, totalCount, resourceSavedCount, resourceNotSavedCount);
        idProfileTask.put(id, profileTask);
    }

    public ProfileTask addToProfileTask(String id, Boolean runStatus, List<String> resourceSaved,
                                        List<String> resourceNotSaved, int totalCount, int resourceSavedCount,
                                        int resourceNotSavedCount){
        profileTask = new ProfileTask();
        profileTask.setId(id);
        profileTask.setStatus(runStatus);
        profileTask.setResourceSaved(resourceSaved);
        profileTask.setResourceNotSaved(resourceNotSaved);
        profileTask.setTotalCount(totalCount);
        profileTask.setResourceSavedCount(resourceSavedCount);
        profileTask.setResourceNotSavedCount(resourceNotSavedCount);
        return profileTask;
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



