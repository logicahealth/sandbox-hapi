package org.hspconsortium.platform.api.service;

import org.hspconsortium.platform.api.fhir.service.ProfileService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

    private boolean taskRunning = false;

    public HashMap<String, List<JSONObject>> getAllUploadedProfiles(HttpServletRequest request, String sandboxId) {
        String authToken = request.getHeader("Authorization").substring(7);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "BEARER " + authToken);
        HttpEntity entity = new HttpEntity(headers);

        HashMap<String, List<JSONObject>> urlAndResources = new HashMap<>();

        String url = "";
        String completeUrl = "";
        String profileUrl = "";
        String currentUrl = "";

        JSONParser jsonParser = new JSONParser();
        List<JSONObject> urtList = new ArrayList<>();
        Boolean next = true;

        for (String resourceType: profileResources) {
            url = localhost + "/" + sandboxId + "/data/" + resourceType + "?_count=50";
            next = true;
            while(next) {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                if (response.hasBody()) {
                    String jsonString = response.getBody();
                    try {
                        JSONObject jsonBundle = (JSONObject) jsonParser.parse(jsonString);
                        JSONArray  linkArray = (JSONArray) jsonBundle.get("link");
                        next = false;
                        if (linkArray.size() >= 2) {
                            for (int i = 0; i < linkArray.size(); i++) {
                                if (((JSONObject) linkArray.get(i)).get("relation").toString().equals("next")) {
                                    next = true;
                                    url = ((JSONObject) linkArray.get(i)).get("url").toString();
                                }
                            }
                        }

                        JSONArray entry = (JSONArray) jsonBundle.get("entry");
                        for (int i = 0; i < entry.size(); i++) {
                            JSONObject resource = (JSONObject) ((JSONObject) entry.get(i)).get("resource");
                            completeUrl = resource.get("url").toString();
                            if (completeUrl.contains(resourceType)) {
                                profileUrl = completeUrl.substring(0, completeUrl.indexOf(resourceType));
                            } else {
                                profileUrl = completeUrl;
                            }
                            if (currentUrl.isEmpty()) {
                                currentUrl = profileUrl;
                                urtList.add(resource);
                            } else if (profileUrl.equals(currentUrl)) {
                                urtList.add(resource);
                            } else {
                                urlAndResources.put(currentUrl, urtList);
                                currentUrl = profileUrl;
                                urtList.clear();
                                urtList.add(resource);
                                urlAndResources.put(currentUrl, urtList);
                                //TODO: not adding SD and another CS if the current url has changed.
                                // it only added one resource to the QICore, where it should be adding 3
                            }
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }
        return urlAndResources;
    }

    public HashMap<String, Boolean> getTaskRunning() {
        HashMap<String, Boolean> taskRunning = new HashMap<>();
        return taskRunning;
    }

    public void setTaskRunning(boolean taskRunning) {
        this.taskRunning = taskRunning;
    }

    @Async
    public HashMap<List<String>, List<String>> saveZipFile (ZipFile zipFile, HttpServletRequest request, String sandboxId) throws IOException {
        HashMap<List<String>, List<String>> successAndFailureList = new HashMap<>();
        String authToken = request.getHeader("Authorization").substring(7);
        String fileName = "";
        String resourceType = "";
        String resourceName = "";
        List<String> resourceSaved = new ArrayList<>();
        List<String> resourceNotSaved = new ArrayList<>();
        Enumeration zipFileEntries = zipFile.entries();

        while(zipFileEntries.hasMoreElements()) {
            taskRunning = true;
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            fileName = entry.getName();
            if (fileName.endsWith(".json")) {
                InputStream inputStream = zipFile.getInputStream(entry);
                JSONParser jsonParser = new JSONParser();
                try {
                    JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(inputStream, "UTF-8"));
                    resourceType = jsonObject.get("resourceType").toString();
                    resourceName = jsonObject.get("name").toString();

                    if (Arrays.stream(profileResources).anyMatch(resourceType::equals)) {
                        String jsonBody = jsonObject.toString();
                        HttpHeaders headers = new HttpHeaders();
                        headers.set("Authorization", "BEARER " + authToken);
                        headers.set("Content-Type", "application/json");
                        String url = localhost + "/" + sandboxId + "/data/" + resourceType + "/" + jsonObject.get("id").toString();
                        HttpEntity entity = new HttpEntity(jsonBody, headers);
                        try {
                            restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
                            resourceSaved.add(resourceType + " - " + resourceName);
                        } catch (HttpServerErrorException e) {
                            resourceNotSaved.add(resourceType + " - " + resourceName);
                            logger.error("Resource not saved: " + resourceType + ". " + e.getMessage());
                        }
                    }
                }
                catch (Exception e) {
                    logger.error("Content of the " + resourceType + " is not supported. " + e.getMessage());
                }
            }
        }
        successAndFailureList.put(resourceSaved, resourceNotSaved);
        taskRunning = false;
        return successAndFailureList;
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



