package org.hspconsortium.platform.api.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.convertors.VersionConvertor_30_40;
import org.hl7.fhir.dstu3.model.Observation;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/converter")
public class VersionConverterController {

//    @Value("${server.localhost}")
//    private String localhost;

    private static final Logger logger = LoggerFactory.getLogger(VersionConverterController.class);

    private RestTemplate restTemplate = new RestTemplate();

    private FhirContext ourFhirCtx = FhirContext.forDstu3();
    private IParser parser = ourFhirCtx.newJsonParser().setPrettyPrint(true);
    private VersionConvertor_30_40 converter = new VersionConvertor_30_40();
    private String masterStu3Url = "http://localhost:8076/MasterStu3Smart/open/";
    private String masterR4Url = "http://localhost:8076/MasterR4Smart/open/";

    @RequestMapping(path = "", method = RequestMethod.GET)
    public void convertStarterSetToR4() {

        String[] resources = {"AllergyIntolerance", "Binary", "Condition", "DocumentReference", "Encounter", "FamilyMemberHistory",
                "ImagingStudy", "Immunization", "List", "MedicationDispense", "MedicationRequest", "Observation", "Patient", "Practitioner", "Procedure"};
        List<String> resourceList = Arrays.asList(resources);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);
        JSONParser jsonParser = new JSONParser();

        for (String resourceName: resourceList) {

            Boolean next;
            String masterStu3UrlResource = masterStu3Url + resourceName + "?_count=50";

                next = true;
                while(next) {
                    ResponseEntity<org.hl7.fhir.dstu3.model.Bundle> response = restTemplate.exchange(masterStu3UrlResource, HttpMethod.GET, entity, org.hl7.fhir.dstu3.model.Bundle.class);
                    if (response.hasBody()) {

                        try {
                            org.hl7.fhir.dstu3.model.Bundle bundle = response.getBody();
                            ;
                            next = false;
                            if (bundle.getLink().size() >= 2) {
                                for (int i = 0; i < bundle.getLink().size(); i++) {
                                    if (bundle.getLink().get(i).getRelation().equals("next")) {
                                        next = true;
                                        masterStu3UrlResource = bundle.getLink().get(i).getUrl();
                                    }
                                }
                            }

                            ;
                            for (int i = 0; i < bundle.getEntry().size(); i++) {
                                String entry = bundle.getEntry().get(i).toString();
                                convert(entry, resourceName);
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }
                }

        }


                // Convert the resource

    }

    public void convert(String resourceString, String resourceName) {
        if (resourceName.equals("Observation")) {
            org.hl7.fhir.dstu3.model.Observation input = parser.parseResource(org.hl7.fhir.dstu3.model.Observation.class, resourceString);
            org.hl7.fhir.r4.model.Observation output = converter.convertObservation(input);
            org.hl7.fhir.r4.model.Observation output2 = output;
        }
        if (resourceName.equals("AllergyIntolerance")) {
            org.hl7.fhir.dstu3.model.AllergyIntolerance input = parser.parseResource(org.hl7.fhir.dstu3.model.AllergyIntolerance.class, resourceString);
            org.hl7.fhir.r4.model.AllergyIntolerance output = converter.convertAllergyIntolerance(input);
            org.hl7.fhir.r4.model.AllergyIntolerance output2 = output;
        }

    }

}
