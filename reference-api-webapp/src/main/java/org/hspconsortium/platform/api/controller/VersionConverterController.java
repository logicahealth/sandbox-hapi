package org.hspconsortium.platform.api.controller;

import org.hl7.fhir.convertors.VersionConvertor_30_40;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/converter")
public class VersionConverterController {

    @Value("${server.localhost}")
    private String localhost;

    @RequestMapping(method = RequestMethod.GET)
    public void convertStarterSetToR4() {
        VersionConvertor_30_40 converter = new VersionConvertor_30_40();
        String[] resources = {"AllergyIntolerance", "Binary", "Condition", "DocumentReference", "Encounter", "FamilyMemberHistory",
                "ImagingStudy", "Immunization", "List", "MedicationDispense", "MedicationRequest", "Observation", "Patient", "Practitioner", "Procedure"};
        List<String> resourceList = Arrays.asList(resources);
        String masterStu3Url = localhost + "/MasterStu3Smart/open/";
        for (String resourceName: resourceList) {

            Boolean next = true;
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

                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }
                }

        }


                // Convert the resource
        org.hl7.fhir.r4.model.Observation output = converter.convertObservation(input);
    }

}
