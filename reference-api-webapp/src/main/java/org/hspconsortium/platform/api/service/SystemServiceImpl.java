package org.hspconsortium.platform.api.service;

import org.hspconsortium.platform.api.fhir.service.SystemService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.w3c.dom.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.*;
import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.springframework.web.client.RestTemplate;

@Component
public class SystemServiceImpl implements SystemService {
    private static final Logger logger = LoggerFactory.getLogger(SandboxServiceImpl.class);

    @Value("${server.port}")
    private String port;

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
                    RestTemplate restTemplate = new RestTemplate();
                    HttpEntity entity = new HttpEntity(jsonBody, headers);

                    try {
                        restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
                    } catch (HttpClientErrorException e) {
                        logger.error("File not saved: " + fileName);
                    }
                }
            }

            if (fileName.endsWith(".xml")) {
                beginsWith = fileName.substring(0, fileName.indexOf("-"));
                if(beginsWith.equals("StructureDefinition") || (beginsWith.equals("ValueSet")) || (beginsWith.equals("CodeSystem"))) {
                    InputStream inputStream = zipFile.getInputStream(entry);
                    try{
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        Document doc = builder.parse(inputStream);
                        Element root = doc.getDocumentElement();
                        String url = "/" + root.getAttribute("resourceType") + "/" + root.getAttribute("id");
                    }
                    catch (Exception e) {
                        logger.error("Unsupported file " + fileName);
                    }
//                  save it to the https://api-v5-stu3.hspconsortium.org/DaVinciDemoPayer/open/StructureDefinition/ + id
                }

            }
        }
    }
}


//    public void saveZipFile (ZipFile zipFile) throws IOException {
//        String fileName = "";
//        String beginsWith = "";
//
//        Enumeration zipFileEntries = zipFile.entries();
//
//        while(zipFileEntries.hasMoreElements()) {
//            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
//            fileName = entry.getName();
//            if(fileName.endsWith(".json")) {
//                beginsWith = fileName.substring(0, fileName.indexOf("-"));
//                if(beginsWith.equals("StructureDefinition") || (beginsWith.equals("ValueSet")) || (beginsWith.equals("CodeSystem"))) {
//                    InputStream inputStream = zipFile.getInputStream(entry);
//                    JSONParser jsonParser = new JSONParser();
//                    JSONObject jsonObject = new JSONObject();
//                    try {
//                        jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(inputStream, "UTF-8"));
//                    }
//                    catch (Exception e) {
//                        logger.error("Unsupported file " + fileName);
//                    }
////                  save it to the https://api-v5-stu3.hspconsortium.org/DaVinciDemoPayer/open/StructureDefinition/ + id
//                    String url = "/" + jsonObject.get("resourceType").toString() + "/" + jsonObject.get("id").toString();
//
//                    // SAVE entry to Hapi
//
//                }
//            }
//
//            if (fileName.endsWith(".xml")) {
//                beginsWith = fileName.substring(0, fileName.indexOf("-"));
//                if(beginsWith.equals("StructureDefinition") || (beginsWith.equals("ValueSet")) || (beginsWith.equals("CodeSystem"))) {
//                    InputStream inputStream = zipFile.getInputStream(entry);
//                    try{
//                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                        DocumentBuilder builder = factory.newDocumentBuilder();
//                        Document doc = builder.parse(inputStream);
//                        Element root = doc.getDocumentElement();
//                        String url = "/" + root.getAttribute("resourceType") + "/" + root.getAttribute("id");
//                    }
//                    catch (Exception e) {
//                        logger.error("Unsupported file " + fileName);
//                    }
////                  save it to the https://api-v5-stu3.hspconsortium.org/DaVinciDemoPayer/open/StructureDefinition/ + id
//                }
//
//            }
//        }
//    }
//}


//           if (fileName.endsWith(".xml")) {
//                beginsWith = fileName.substring(0, fileName.indexOf("-"));
//                if(beginsWith.equals("StructureDefinitions")) {
//                    extractContentFromJSONFile(fileName, zip);
////                    save it to the https://api-v5-stu3.hspconsortium.org/DaVinciDemoPayer/open/StructureDefinition/ + id
//
//                } else if (beginsWith.equals("ValueSet")) {
//
//                } else if (beginsWith.equals("CodeSystem")) {
//
//                } else {
//                    // do nothing
//                }

//    https://stackoverflow.com/questions/33320064/java-opening-zip-files-into-memory
//    ZipFile zipFile = new ZipFile("archive.zip");
//    ZipEntry entry = zipFile.getEntry("file.json");
//    InputStream is = zipFile.getInputStream(entry);
//    byte[] data = new byte[is.available()];
//    is.read(data);
//    String json = new String(data);


// https://stackoverflow.com/questions/981578/how-to-unzip-files-recursively-in-java
//    static public void extractFolder(String zipFile) throws ZipException, IOException {
//        System.out.println(zipFile);
//        int BUFFER = 2048;
//        File file = new File(zipFile);
//
//        ZipFile zip = new ZipFile(file);
//        String newPath = zipFile.substring(0, zipFile.length() - 4);
//
//        new File(newPath).mkdir();
//        Enumeration zipFileEntries = zip.entries();
//
//        // Process each entry
//        while (zipFileEntries.hasMoreElements()) {
//            // grab a zip file entry
//            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
//            String currentEntry = entry.getName();
//            File destFile = new File(newPath, currentEntry);
//            //destFile = new File(newPath, destFile.getName());
//            File destinationParent = destFile.getParentFile();
//
//            // create the parent directory structure if needed
//            destinationParent.mkdirs();
//
//            if (!entry.isDirectory()) {
//                BufferedInputStream is = new BufferedInputStream(zip
//                        .getInputStream(entry));
//                int currentByte;
//                // establish buffer for writing file
//                byte data[] = new byte[BUFFER];
//
//                // write the current file to disk
//                FileOutputStream fos = new FileOutputStream(destFile);
//                BufferedOutputStream dest = new BufferedOutputStream(fos,
//                        BUFFER);
//
//                // read and write until last byte is encountered
//                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
//                    dest.write(data, 0, currentByte);
//                }
//                dest.flush();
//                dest.close();
//                is.close();
//            }
//
//            if (currentEntry.endsWith(".zip")) {
//                // found a zip file, try to open
//                extractFolder(destFile.getAbsolutePath());
//            }
//        }
//    }


//    private String extractContentFromJSONFile2(String fileName, ZipFile zip) throws ZipException, IOException {
//        ZipEntry entry = zip.getEntry(fileName);
//        InputStream inputStream = zip.getInputStream(entry);
//        byte[] data = new byte[inputStream.available()];
//        inputStream.read(data);
//        String jsonString = new String(data);
//        return jsonString;
//    }

//    private JSONObject extractContentFromJSONFile(String fileName, ZipFile zip) throws ZipException, IOException {
//        ZipEntry entry = zip.getEntry(fileName);
//        InputStream inputStream = zip.getInputStream(entry);
//        JSONParser jsonParser = new JSONParser();
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(inputStream, "UTF-8"));
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//        return jsonObject;
//    }

//    private XML extractContentFromXMLFile(String fileName, ZipFile zip) throws ZipException, IOException {
//        ZipEntry entry = zip.getEntry(fileName);
//        InputStream inputStream = zip.getInputStream(entry);
//        XML xml = new XML();
//
//        try{
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document doc = builder.parse(inputStream);
//            Element root = doc.getDocumentElement();
//            String url = "/" + root.getAttribute("resourceType") + "/" + root.getAttribute("id");
//
//
//        }
//        catch (Exception e) {
//
//        }
//
//
//        return xml;
//    }


