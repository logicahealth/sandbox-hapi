package org.hspconsortium.platform.api.fhir.repository;

import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.dstu2.composite.BoundCodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.Conformance;
import ca.uhn.fhir.model.dstu2.valueset.RestfulSecurityServiceEnum;
import ca.uhn.fhir.model.primitive.UriDt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Lazy
@Component
public class MetadataRepositoryDstu2Impl implements MetadataRepositoryDstu2 {

    @Autowired
    private MetadataRepositoryConfig metadataRepositoryConfig;

    @SuppressWarnings("Duplicates")
    @Override
    public Conformance addConformance(Conformance conformance){

        if (metadataRepositoryConfig.isSecured()) {
            List<Conformance.Rest> restList = conformance.getRest();
            Conformance.Rest rest = restList.get(0);
            Conformance.RestSecurity restSecurity = rest.getSecurity();

            ExtensionDt conformanceExtension = new ExtensionDt(false, metadataRepositoryConfig.getUrisEndpointExtensionUrl());
            conformanceExtension.addUndeclaredExtension(new ExtensionDt(false, "authorize", new UriDt(metadataRepositoryConfig.getAuthorizeUrl())));
            conformanceExtension.addUndeclaredExtension(new ExtensionDt(false, "token", new UriDt(metadataRepositoryConfig.getTokenUrl())));
            conformanceExtension.addUndeclaredExtension(new ExtensionDt(false, "register", new UriDt(metadataRepositoryConfig.getRegistrationEndpointUrl())));
            conformanceExtension.addUndeclaredExtension(new ExtensionDt(false, "launch-registration", new UriDt(metadataRepositoryConfig.getLaunchRegistrationUrl())));
            restSecurity.addUndeclaredExtension(conformanceExtension);

            BoundCodeableConceptDt<RestfulSecurityServiceEnum> boundCodeableConceptDt =
                    new BoundCodeableConceptDt<>(
                            RestfulSecurityServiceEnum.VALUESET_BINDER, RestfulSecurityServiceEnum.SMART_ON_FHIR);
            boundCodeableConceptDt.setText("OAuth2 using SMART-on-FHIR profile (see http://docs.smarthealthit.org)");
            restSecurity.getService().add(boundCodeableConceptDt);
        }

        return conformance;
    }
}
