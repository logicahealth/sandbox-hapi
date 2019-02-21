package org.hspconsortium.platform.api.controller;

import org.hl7.fhir.convertors.VersionConvertor_30_40;

import java.util.Arrays;
import java.util.List;

public class VersionConverterController {

    VersionConvertor_30_40 converter = new VersionConvertor_30_40();
    String[] resources = {"AllergyIntolerance", "Binary", "Condition", "DocumentReference", "Encounter", "FamilyMemberHistory",
            "ImagingStudy", "Immunization", "List", "MedicationDispense", "MedicationRequest", "Observation", "Patient", "Practitioner", "Procedure"};
    List<String> resourceList = Arrays.asList(resources);
    for (String resourceName: resourceList) {

    }


            // Convert the resource
    org.hl7.fhir.r4.model.Observation output = converter.convertObservation(input);

}
