package org.hspconsortium.platform.api.fhir.repository;

import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Lazy
@Component
public class MetadataRepositoryR4Impl implements MetadataRepositoryR4 {

    @Autowired
    private MetadataRepositoryConfig metadataRepositoryConfig;

    @Override
    @SuppressWarnings("Duplicates")
    public CapabilityStatement addCapabilityStatement(CapabilityStatement capabilityStatement) {
        if (metadataRepositoryConfig.isSecured()) {
            List<CapabilityStatement.CapabilityStatementRestComponent> restList = capabilityStatement.getRest();

            CapabilityStatement.CapabilityStatementRestComponent rest = restList.get(0);
            CapabilityStatement.CapabilityStatementRestSecurityComponent restSecurity = rest.getSecurity();

            Extension conformanceExtension = new Extension(metadataRepositoryConfig.getUrisEndpointExtensionUrl());
            conformanceExtension.addExtension(new Extension("authorize", new UriType(metadataRepositoryConfig.getAuthorizeUrl())));
            conformanceExtension.addExtension(new Extension("token", new UriType(metadataRepositoryConfig.getTokenUrl())));
            conformanceExtension.addExtension(new Extension("register", new UriType(metadataRepositoryConfig.getRegistrationEndpointUrl())));
            conformanceExtension.addExtension(new Extension("launch-registration", new UriType(metadataRepositoryConfig.getLaunchRegistrationUrl())));

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
