package org.hspconsortium.platform.api.authorization;

import org.hl7.fhir.instance.model.ResourceType;
import org.hspconsortium.platform.api.controller.HapiFhirController;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile(HapiFhirController.DSTU2_PROFILE_NAME)
@Component
public class ScopeBasedAuthorizationParamsDstu2 implements ScopeBasedAuthorizationParams {
    @Override
    public String getParamForResource(String resourceTypeString) {
        ResourceType resourceType = null;
        try {
            resourceType = ResourceType.valueOf(resourceTypeString);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SecurityException("Unexpected resource type: " + resourceTypeString);
        }

        switch (resourceType) {
            case Encounter:
            case BodySite:
            case CarePlan:
            case Claim:
            case ClinicalImpression:
            case EpisodeOfCare:
            case FamilyMemberHistory:
            case Flag:
            case Goal:
            case ImagingStudy:
            case Immunization:
            case ImmunizationRecommendation:
            case MedicationDispense:
            case MedicationAdministration:
            case MedicationOrder:
            case MedicationStatement:
            case NutritionOrder:
            case Person:
            case RelatedPerson:
            case SupplyDelivery:
            case SupplyRequest:
            case VisionPrescription:
            case Basic:
            case AllergyIntolerance:
            case AuditEvent:
            case Condition:
            case DetectedIssue:
            case Procedure:
            case ReferralRequest:
            case ImagingObjectSelection:
                return "patient";
            case DeviceUseStatement:
            case DiagnosticReport:
            case EnrollmentRequest:
            case Account:
            case Media:
            case RiskAssessment:
            case Specimen:
            case Observation:
            case ProcedureRequest:
            case QuestionnaireResponse:
            case Communication:
            case CommunicationRequest:
            case Composition:
            case DeviceUseRequest:
            case List:
            case DiagnosticOrder:
            case DocumentManifest:
            case DocumentReference:
            case Order:
                return "subject";
            case Appointment:
            case AppointmentResponse:
            case Schedule:
                return "actor";
            case Group:
                return "member";
            case Patient:
                return "link";
            case Provenance:
                return "target.subject";
            case OrderResponse:
                return "request.patient";

        }

        return null;
    }
}
