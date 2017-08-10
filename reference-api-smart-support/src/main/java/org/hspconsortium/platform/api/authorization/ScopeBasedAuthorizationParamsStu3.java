package org.hspconsortium.platform.api.authorization;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("stu3")
public class ScopeBasedAuthorizationParamsStu3 implements ScopeBasedAuthorizationParams {
    @Override
    public String getParamForResource(String resourceTypeString) {
        switch (resourceTypeString) {
            // subject
            case "Encounter":
            case "BodySite":
            case "CarePlan":
            case "Consent":
            case "Claim":
            case "ClaimResponse":
            case "DetectedIssue":
            case "EpisodeOfCare":
            case "FamilyMemberHistory":
            case "Flag":
            case "Goal":
            case "ImagingStudy":
            case "Immunization":
            case "ImmunizationRecommendation":
            case "MeasureReport":
            case "MedicationDispense":
            case "NutritionOrder":
            case "Person":
            case "RelatedPerson":
            case "SupplyDelivery":
            case "VisionPrescription":
            case "Basic": // author
            case "AllergyIntolerance": // recorder, asserter
            case "AuditEvent": // agent.patient, entity.patient
            case "CareTeam": // participant
            case "Condition": // asserter
            case "Procedure": // performer
            case "ReferralRequest": // requester
            case "ImagingManifest": // author
            case "MedicationAdministration": // performer
            case "EligibilityRequest":
            case "ExplanationOfBenefit":
                return "patient";
            case "Account":
            case "AdverseEvent":
            case "ChargeItem":
            case "DeviceUseStatement":
            case "DiagnosticReport":
            case "EnrollmentRequest":
            case "Media":
            case "MedicationStatement":
            case "RiskAssessment":
            case "Specimen":
            case "Observation": // performer
            case "ProcedureRequest": // orderer, performer
            case "QuestionnaireResponse": // author
            case "RequestGroup": // participan
            case "ClinicalImpression":
            case "Communication": // sender, recipient
            case "CommunicationRequest": // sender, recipient, requester
            case "Composition": // author, attester
            case "DeviceRequest": // requester, filler
            case "List": // source
                //case "DiagnosticRequest": // filler
            case "DocumentManifest": // author, recipient
            case "DocumentReference": // author
            case "MedicationRequest":
                return "subject";
            case "Appointment":
            case "AppointmentResponse":
            case "Schedule":
                return "actor";
            case "Group":
                return "member";
            case "Patient":
                return "link";
            case "Provenance": // target.patient, patient
                return "target.subject";
            case "ResearchSubject":
                return "individual";
            case "Coverage":
                return "policy-holder";
            case "SupplyRequest":
                return "requester";

        }

        return null;
    }
}
