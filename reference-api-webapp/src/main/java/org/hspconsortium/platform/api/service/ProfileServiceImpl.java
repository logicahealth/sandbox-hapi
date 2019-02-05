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
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.springframework.web.client.RestTemplate;


@Component
public class ProfileServiceImpl implements ProfileService {
    private static final Logger logger = LoggerFactory.getLogger(SandboxServiceImpl.class);

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${server.localhost}")
    private String localhost;

    @Value("${hspc.platform.api.fhir.profileResources}")
    private String[] profileResources;

    @Value("${hspc.platform.api.fhir.profileResourcesUpload}")
    private String[] profileResoucesUpload;

    public HashMap<String, List<JSONObject>> getAllUploadedProfiles(HttpServletRequest request, String sandboxId) {
        String authToken = request.getHeader("Authorization").substring(7);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "BEARER " + authToken);
        String jsonBody = "{\"sandbox\": \""+ sandboxId + "\"}";
        HttpEntity entity = new HttpEntity(jsonBody, headers);

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
                        if (linkArray.size() >= 2) {
                            if (((JSONObject) linkArray.get(1)).get("relation").toString().equals("next")) {
                                next = true;
                                url = ((JSONObject) linkArray.get(1)).get("url").toString();
                            } else {
                                next = false;
                            }
                        } else {
                            next = false;
                        }

                        JSONArray entry = (JSONArray) jsonBundle.get("entry");
                        for (int i = 0; i < entry.size(); i++) {
                            JSONObject resource = (JSONObject) ((JSONObject) entry.get(i)).get("resource");
                            completeUrl = resource.get("url").toString();
                            profileUrl = completeUrl.substring(0, completeUrl.indexOf(resourceType));
                            if (currentUrl.isEmpty()) {
                                currentUrl = profileUrl;
                                urtList.add(resource);
                            } else if (profileUrl.equals(currentUrl)) {
                                urtList.add(resource);
                            } else {
                                urlAndResources.put(currentUrl, urtList);
                                currentUrl = profileUrl;
                            }
                        }
                        urlAndResources.put(currentUrl, urtList);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }
        return urlAndResources;
    }

    private JSONObject parseJsonObject (ResponseEntity<String> response) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonBundle = new JSONObject();
        if (response.hasBody()) {
            String jsonString = response.getBody();
            try {
                jsonBundle = (JSONObject) jsonParser.parse(jsonString);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return jsonBundle;
    }


    public void saveZipFile (ZipFile zipFile, HttpServletRequest request, String sandboxId) throws IOException {
        String authToken = request.getHeader("Authorization").substring(7);

        String fileName = "";
        String beginsWith = "";

        Enumeration zipFileEntries = zipFile.entries();

        while(zipFileEntries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            fileName = entry.getName();
            if (fileName.contains("/")) {
                fileName = fileName.substring(fileName.indexOf("/") + 1);
            }
            if (fileName.endsWith(".json")) {
                beginsWith = fileName.substring(0, fileName.indexOf("-"));
                if(beginsWith.equals("StructureDefinition") || (beginsWith.equals("ValueSet")) || (beginsWith.equals("CodeSystem"))) {
                    InputStream inputStream = zipFile.getInputStream(entry);
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(inputStream, "UTF-8"));
                    }
                    catch (Exception e) {
                        logger.error("Unsupported file: " + fileName);
                    }
                    String jsonBody = jsonObject.toString();
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", "BEARER " + authToken);
                    headers.set("Content-Type", "application/json");
                    String url = localhost + "/" + sandboxId + "/data/" + jsonObject.get("resourceType").toString() + "/" + jsonObject.get("id").toString();
                    HttpEntity entity = new HttpEntity(jsonBody, headers);

                    try {
                        restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
                    } catch (HttpClientErrorException e) {
                        logger.error("File not saved: " + fileName);
                    }
                }
            }
        }
    }
}

//if (((JSONObject) jsonBundle.get("link")).get("relation").equals("next")) {




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



