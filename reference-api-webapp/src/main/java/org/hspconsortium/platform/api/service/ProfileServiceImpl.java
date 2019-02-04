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

    @Value("${server.port}")
    private String port;

    public HashMap<String, HashMap<String, String>> getAllUploadedProfilesOriginal(HttpServletRequest request, String sandboxId) {
        String authToken = getBearerToken(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "BEARER " + authToken);
        String jsonBody = "{\"sandbox\": \""+ sandboxId + "\"}";
        HttpEntity entity = new HttpEntity(jsonBody, headers);

        HashMap<String, HashMap<String, String>> resourceAndUrl = new HashMap<>();
        HashMap<String, String> innerMap = new HashMap<>();
        JSONParser jsonParser = new JSONParser();

        List<String> resourceTypes = Arrays.asList("StructureDefinition", "ValueSet", "CodeSystem");
        for (String resourceType: resourceTypes) {
            String url = "http://localhost:" + port + "/" + sandboxId + "/data/" + resourceType;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            if (response.hasBody()) {
                String jsonString = response.getBody();
                try {
                    JSONObject jsonBundle = (JSONObject) jsonParser.parse(jsonString);
                    JSONArray entry = (JSONArray) jsonBundle.get("entry");
                    for (int i = 0; i < entry.size(); i++) {
                        JSONObject resource = (JSONObject) entry.get(i);
                        JSONObject urt = (JSONObject) resource.get("resource");
                        String uploadedResourceType = urt.get("resourceType").toString();
                        String resourceUrl = urt.get("url").toString();
                        String fhirVersion = urt.get("fhirVersion").toString();
                        innerMap.put(resourceUrl, fhirVersion);
                        resourceAndUrl.put(uploadedResourceType, innerMap);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return resourceAndUrl;
    }

    public HashMap<String, List<JSONObject>> getAllUploadedProfiles(HttpServletRequest request, String sandboxId) {
        String authToken = getBearerToken(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "BEARER " + authToken);
        String jsonBody = "{\"sandbox\": \""+ sandboxId + "\"}";
        HttpEntity entity = new HttpEntity(jsonBody, headers);

        HashMap<String, List<JSONObject>> resourceAndUrl = new HashMap<>();

        String completeUrl = "";
        String profileUrl = "";
        String currentUrl = "";

        JSONParser jsonParser = new JSONParser();
        List<JSONObject> urtList = new ArrayList<>();

        List<String> resourceTypes = Arrays.asList("StructureDefinition", "CodeSystem");
        for (String resourceType: resourceTypes) {
            String url = "http://localhost:" + port + "/" + sandboxId + "/data/" + resourceType;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            if (response.hasBody()) {
                String jsonString = response.getBody();
                try {
                    JSONObject jsonBundle = (JSONObject) jsonParser.parse(jsonString);
                    JSONArray entry = (JSONArray) jsonBundle.get("entry");
                    for (int i = 0; i < entry.size(); i++) {
                        JSONObject resource = (JSONObject) entry.get(i);
                        JSONObject urt = (JSONObject) resource.get("resource");
                        completeUrl = urt.get("url").toString();
                        profileUrl = completeUrl.substring(0, completeUrl.indexOf(resourceType));
                        if (currentUrl.isEmpty()) {
                            currentUrl = profileUrl;
                            urtList.add(urt);
                        } else if (profileUrl.equals(currentUrl)) {
                            urtList.add(urt);
                        } else {
                            resourceAndUrl.put(currentUrl, urtList);
                            currentUrl = profileUrl;
                        }
                    }
                    resourceAndUrl.put(currentUrl, urtList);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return resourceAndUrl;
    }

    public void saveZipFile (ZipFile zipFile, HttpServletRequest request, String sandboxId) throws IOException {

        String authToken = request.getHeader("Authorization");
        if (authToken == null) {
            logger.error("User is unauthorized to upload the profile");
        }
        authToken = authToken.substring(7);

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
                    String url = "http://localhost:" + port + "/" + sandboxId + "/data/" + jsonObject.get("resourceType").toString() + "/" + jsonObject.get("id").toString();
                    HttpEntity entity = new HttpEntity(jsonBody, headers);

                    try {
                        restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
                    } catch (HttpClientErrorException e) {
                        logger.error("File not saved: " + fileName);
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
//                        String url = "http://localhost:" + port + "/" + sandboxId + "/data/" +  beginsWith + "/" + element.getAttribute("id value");
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
        }
    }
    private String getBearerToken(HttpServletRequest request) {

        String authToken = request.getHeader("Authorization");
        if (authToken == null) {
            return null;
        }
        return authToken.substring(7);
    }
}
