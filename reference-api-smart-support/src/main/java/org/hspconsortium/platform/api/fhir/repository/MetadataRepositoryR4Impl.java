package org.hspconsortium.platform.api.fhir.repository;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.UriType;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Lazy
public class MetadataRepositoryR4Impl implements MetadataRepositoryR4 {

    static private String SECURE_MODE = "secured";
    static private String SECURE_MODE_MOCK = "mock";

    @Value("${hspc.platform.api.security.mode}")
    private String securityMode;

    @Value("${hspc.platform.authorization.tokenUrl}")
    private String tokenEndpointUri;

    @Value("${hspc.platform.authorization.authorizeUrl}")
    private String authorizationEndpointUri;

    @Value("${hspc.platform.authorization.smart.registrationEndpointUrl}")
    private String registrationEndpointUri;

    @Value("${hspc.platform.authorization.smart.urisEndpointExtensionUrl}")
    private String urisEndpointExtensionUrl;

    @Value("${hspc.platform.authorization.smart.launchRegistrationUrl}")
    private String launchRegistrationUrl;

    @Override
    @SuppressWarnings("Duplicates")
    public CapabilityStatement addCapabilityStatement(CapabilityStatement capabilityStatement) {
        if (SECURE_MODE.equalsIgnoreCase(securityMode) || SECURE_MODE_MOCK.equalsIgnoreCase(securityMode)) {
            List<CapabilityStatement.CapabilityStatementRestComponent> restList = capabilityStatement.getRest();

            CapabilityStatement.CapabilityStatementRestComponent rest = restList.get(0);
            CapabilityStatement.CapabilityStatementRestSecurityComponent restSecurity = rest.getSecurity();

            Extension conformanceExtension = new Extension(this.urisEndpointExtensionUrl);
            conformanceExtension.addExtension(new Extension("authorize", new UriType(this.authorizationEndpointUri)));
            conformanceExtension.addExtension(new Extension("token", new UriType(this.tokenEndpointUri)));
            conformanceExtension.addExtension(new Extension("register", new UriType(this.registrationEndpointUri)));
            conformanceExtension.addExtension(new Extension("launch-registration", new UriType(this.launchRegistrationUrl)));

            restSecurity.addExtension(conformanceExtension);
            CodeableConcept codeableConcept = new CodeableConcept();
            Coding smartOnFhirCoding = new Coding("http://hl7.org/fhir/restful-security-service", "SMART-on-FHIR", "SMART-on-FHIR");
            codeableConcept.getCoding().add(smartOnFhirCoding);
            codeableConcept.setText("OAuth2 using SMART-on-FHIR profile (see http://docs.smarthealthit.org)");
            restSecurity.getService().add(codeableConcept);
        }

        return capabilityStatement;
    }
}
