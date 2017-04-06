package org.hspconsortium.platform.api.authorization;

import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.exceptions.FHIRException;
import org.hspconsortium.platform.api.controller.HapiFhirController;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(HapiFhirController.STU3_PROFILE_NAME)
public class ScopeBasedAuthorizationParamsStu3 implements ScopeBasedAuthorizationParams {
    @Override
    public String getParamForResource(String resourceTypeString) {
        ResourceType resourceType = null;
        try {
            resourceType = ResourceType.fromCode(resourceTypeString);
        } catch (FHIRException e) {
            e.printStackTrace();
            throw new SecurityException("Unexpected resource type: " + resourceTypeString);
        }

        switch (resourceType) {
            // subject
            case Encounter:
            case BodySite:
            case CarePlan:
            case Consent:
            case DetectedIssue:
            case EpisodeOfCare:
            case FamilyMemberHistory:
            case Flag:
            case Goal:
            case ImagingStudy:
            case Immunization:
            case ImmunizationRecommendation:
            case MeasureReport:
            case MedicationDispense:
            case MedicationRequest:
            //case NutritionRequest:
            case Person:
            case RelatedPerson:
            case SupplyDelivery:
            case SupplyRequest:
            case VisionPrescription:
            case Basic: // author
            case AllergyIntolerance: // recorder, asserter
            case AuditEvent: // agent.patient, entity.patient
            case CareTeam: // participant
            case Condition: // asserter
            case Procedure: // performer
            case ReferralRequest: // requester
            case ImagingManifest: // author
            case MedicationAdministration: // performer
                return "patient";
            case DeviceUseStatement:
            case DiagnosticReport:
            case EnrollmentRequest:
            case Account:
            case Media:
            case MedicationStatement:
            case RiskAssessment:
            case Specimen:
            case Observation: // performer
            case ProcedureRequest: // orderer, performer
            case QuestionnaireResponse: // author
            case RequestGroup: // participantcase ClinicalImpression:
            case Communication: // sender, recipient
            case CommunicationRequest: // sender, recipient, requester
            case Composition: // author, attester
            //case DeviceUseRequest: // requester, filler
            case List: // source
            //case DiagnosticRequest: // filler
            case DocumentManifest: // author, recipient
            case DocumentReference: // author
                return "subject";
            case Appointment:
            case AppointmentResponse:
            case Schedule:
                return "actor";
            case Group:
                return "member";
            case Patient:
                return "link";
            case Provenance: // target.patient, patient
                return "target.subject";
            case ResearchSubject:
                return "individual";

        }

        return null;
    }
}
