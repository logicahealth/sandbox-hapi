/**
 *  * #%L
 *  *
 *  * %%
 *  * Copyright (C) 2014-2020 Healthcare Services Platform Consortium
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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.convertors.VersionConvertor_30_40;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

// This is unsecured so don't publish this as a restcontroller in production
// Will only work if the cqf-ruler code and dependency is removed.
//@RestController
//@RequestMapping("/converter")
public class VersionConverterController {

//    @Value("${server.localhost}")
//    private String localhost;

    private static final Logger logger = LoggerFactory.getLogger(VersionConverterController.class);

    private RestTemplate restTemplate = new RestTemplate();

    private FhirContext ourFhirCtx = FhirContext.forDstu3();
    private IParser parser = ourFhirCtx.newJsonParser().setPrettyPrint(true);
    private VersionConvertor_30_40 converter = new VersionConvertor_30_40();
    private String masterStu3Url = "http://localhost:8079/MasterStu3Smart/data/";
    private String masterR4Url = "http://localhost:8070/MasterR4Empty/data/";

    @RequestMapping(path = "", method = RequestMethod.GET)
    public void convertStarterSetToR4(HttpServletRequest request) {

        // This list is according to the available resources in the starter data set.
        String[] resources = {"Patient", "Practitioner", "AllergyIntolerance", "Binary", "Condition", "DocumentReference", "Encounter", "FamilyMemberHistory",
                "ImagingStudy", "Immunization", "List", "MedicationDispense", "MedicationRequest", "Procedure", "Observation" };
//        String[] resources = {"Observation"};
        List<String> resourceList = Arrays.asList(resources);

        String authToken = request.getHeader("Authorization").substring(7);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "BEARER " + authToken);
        HttpEntity entity = new HttpEntity(headers);
        JSONParser jsonParser = new JSONParser();

        for (String resourceName: resourceList) {
            Boolean next;
            String masterStu3UrlResource = masterStu3Url + resourceName + "?_count=50";
            next = true;
            while(next) {
                ResponseEntity<String> response = restTemplate.exchange(masterStu3UrlResource, HttpMethod.GET, entity, String.class);
                if (response.hasBody()) {
                    String jsonString = response.getBody();
                    try {
                        JSONObject jsonBundle = (JSONObject) jsonParser.parse(jsonString);
                        JSONArray linkArray = (JSONArray) jsonBundle.get("link");

                        next = false;
                        if (linkArray.size() >= 2) {
                            for (int i = 0; i < linkArray.size(); i++) {
                                if (((JSONObject) linkArray.get(i)).get("relation").toString().equals("next")) {
                                    next = true;
                                    masterStu3UrlResource = ((JSONObject) linkArray.get(i)).get("url").toString();
                                }
                            }
                        }
                        JSONArray entry = (JSONArray) jsonBundle.get("entry");
                        for (int i = 0; i < entry.size(); i++) {
                            JSONObject resource = (JSONObject) ((JSONObject) entry.get(i)).get("resource");
                            if (resource.get("id").toString().matches("[0-9]+")) {
                                String resourceString = resource.toString().replaceAll(resource.get("id").toString(), "SMART-" + resource.get("id").toString());
                                convert(resourceString, resourceName, authToken);
                                logger.info(resourceName);
                            } else {
                                convert(resource.toString(), resourceName, authToken);
                            }
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }
    }

    private void convert(String resourceString, String resourceName, String authToken) {
        if (resourceName.equals("Patient")) {
            org.hl7.fhir.dstu3.model.Patient input = parser.parseResource(org.hl7.fhir.dstu3.model.Patient.class, resourceString);
            var output = converter.convertResource(input, false);
            uploadProfile(resourceName, FhirContext.forR4().newJsonParser().encodeResourceToString(output), output.getId().split("/")[1], authToken);
        }
        if (resourceName.equals("Practitioner")) {
            org.hl7.fhir.dstu3.model.Practitioner input = parser.parseResource(org.hl7.fhir.dstu3.model.Practitioner.class, resourceString);
            var output = converter.convertResource(input, false);
            uploadProfile(resourceName, FhirContext.forR4().newJsonParser().encodeResourceToString(output), output.getId().split("/")[1], authToken);
        }
        if (resourceName.equals("AllergyIntolerance")) {
            org.hl7.fhir.dstu3.model.AllergyIntolerance input = parser.parseResource(org.hl7.fhir.dstu3.model.AllergyIntolerance.class, resourceString);
            var output = converter.convertResource(input, false);
            uploadProfile(resourceName, FhirContext.forR4().newJsonParser().encodeResourceToString(output), output.getId().split("/")[1], authToken);
        }
        if (resourceName.equals("Binary")) {
            org.hl7.fhir.dstu3.model.Binary input = parser.parseResource(org.hl7.fhir.dstu3.model.Binary.class, resourceString);
            var output = converter.convertResource(input, false);
            uploadProfile(resourceName, FhirContext.forR4().newJsonParser().encodeResourceToString(output), output.getId(), authToken);
        }
        if (resourceName.equals("Condition")) {
            org.hl7.fhir.dstu3.model.Condition input = parser.parseResource(org.hl7.fhir.dstu3.model.Condition.class, resourceString);
            var output = converter.convertResource(input, false);
            uploadProfile(resourceName, FhirContext.forR4().newJsonParser().encodeResourceToString(output), output.getId().split("/")[1], authToken);
        }
        if (resourceName.equals("DocumentReference")) {
            org.hl7.fhir.dstu3.model.DocumentReference input = parser.parseResource(org.hl7.fhir.dstu3.model.DocumentReference.class, resourceString);
            var output = converter.convertResource(input, false);
            uploadProfile(resourceName, FhirContext.forR4().newJsonParser().encodeResourceToString(output), output.getId().split("/")[1], authToken);
        }
        if (resourceName.equals("Encounter")) {
            org.hl7.fhir.dstu3.model.Encounter input = parser.parseResource(org.hl7.fhir.dstu3.model.Encounter.class, resourceString);
            var output = converter.convertResource(input, false);
            uploadProfile(resourceName, FhirContext.forR4().newJsonParser().encodeResourceToString(output), output.getId().split("/")[1], authToken);
        }
        if (resourceName.equals("FamilyMemberHistory")) {
            org.hl7.fhir.dstu3.model.FamilyMemberHistory input = parser.parseResource(org.hl7.fhir.dstu3.model.FamilyMemberHistory.class, resourceString);
            var output = converter.convertResource(input, false);
            uploadProfile(resourceName, FhirContext.forR4().newJsonParser().encodeResourceToString(output), output.getId().split("/")[1], authToken);
        }
        if (resourceName.equals("ImagingStudy")) {
            org.hl7.fhir.dstu3.model.ImagingStudy input = parser.parseResource(org.hl7.fhir.dstu3.model.ImagingStudy.class, resourceString);
//            org.hl7.fhir.r4.model.ImagingStudy output = converter.convertImagingStudy(input);
            uploadProfile(resourceName, FhirContext.forDstu3().newJsonParser().encodeResourceToString(input), input.getId().split("/")[1], authToken);
        }
        if (resourceName.equals("Immunization")) {
            org.hl7.fhir.dstu3.model.Immunization input = parser.parseResource(org.hl7.fhir.dstu3.model.Immunization.class, resourceString);
            var output = converter.convertResource(input, false);
            uploadProfile(resourceName, FhirContext.forR4().newJsonParser().encodeResourceToString(output), output.getId().split("/")[1], authToken);
        }
        if (resourceName.equals("List")) {
            org.hl7.fhir.dstu3.model.ListResource input = parser.parseResource(org.hl7.fhir.dstu3.model.ListResource.class, resourceString);
            var output = converter.convertResource(input, false);
            uploadProfile(resourceName, FhirContext.forR4().newJsonParser().encodeResourceToString(output), output.getId().split("/")[1], authToken);
        }
        if (resourceName.equals("MedicationDispense")) {
            org.hl7.fhir.dstu3.model.MedicationDispense input = parser.parseResource(org.hl7.fhir.dstu3.model.MedicationDispense.class, resourceString);
            var output = converter.convertResource(input, false);
            uploadProfile(resourceName, FhirContext.forR4().newJsonParser().encodeResourceToString(output), output.getId().split("/")[1], authToken);
        }
        if (resourceName.equals("MedicationRequest")) {
            org.hl7.fhir.dstu3.model.MedicationRequest input = parser.parseResource(org.hl7.fhir.dstu3.model.MedicationRequest.class, resourceString);
            var output = converter.convertResource(input, false);
            uploadProfile(resourceName, FhirContext.forR4().newJsonParser().encodeResourceToString(output), output.getId().split("/")[1], authToken);
        }
        if (resourceName.equals("Observation")) {
            org.hl7.fhir.dstu3.model.Observation input = parser.parseResource(org.hl7.fhir.dstu3.model.Observation.class, resourceString);
            var output = converter.convertResource(input, false);
            uploadProfile(resourceName, FhirContext.forR4().newJsonParser().encodeResourceToString(output), output.getId().split("/")[1], authToken);
        }
        if (resourceName.equals("Procedure")) {
            org.hl7.fhir.dstu3.model.Procedure input = parser.parseResource(org.hl7.fhir.dstu3.model.Procedure.class, resourceString);
//            org.hl7.fhir.r4.model.Procedure output = converter.convertProcedure(input);
            uploadProfile(resourceName, FhirContext.forDstu3().newJsonParser().encodeResourceToString(input), input.getId().split("/")[1], authToken);
        }

    }

    private void uploadProfile(String resourceName, String resource, String resourceId, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "BEARER " + authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(resource, headers);
        String masterR4ResourceUrl = masterR4Url + resourceName + "/" + resourceId;
        try {
            ResponseEntity<String> response = restTemplate.exchange(masterR4ResourceUrl, HttpMethod.PUT, entity, String.class);
        } catch (HttpServerErrorException e) {
            throw new ResourceNotFoundException(resourceName + "/" + resourceId + " not uploaded");
        }

    }

}
