package org.hspconsortium.platform.api.fhir.repository;

import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.dstu2.composite.BoundCodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.Conformance;
import ca.uhn.fhir.model.dstu2.valueset.RestfulSecurityServiceEnum;
import ca.uhn.fhir.model.primitive.UriDt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Lazy
public class MetadataRepositoryDstu2Impl implements MetadataRepositoryDstu2 {
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
    public Conformance addConformance(Conformance conformance){

        if (SECURE_MODE.equalsIgnoreCase(securityMode) || SECURE_MODE_MOCK.equalsIgnoreCase(securityMode)) {
            List<Conformance.Rest> restList = conformance.getRest();
            Conformance.Rest rest = restList.get(0);
            Conformance.RestSecurity restSecurity = rest.getSecurity();

            ExtensionDt conformanceExtension = new ExtensionDt(false, this.urisEndpointExtensionUrl);
            conformanceExtension.addUndeclaredExtension(new ExtensionDt(false, "authorize", new UriDt(this.authorizationEndpointUri)));
            conformanceExtension.addUndeclaredExtension(new ExtensionDt(false, "token", new UriDt(this.tokenEndpointUri)));
            conformanceExtension.addUndeclaredExtension(new ExtensionDt(false, "register", new UriDt(this.registrationEndpointUri)));
            conformanceExtension.addUndeclaredExtension(new ExtensionDt(false, "launch-registration", new UriDt(this.launchRegistrationUrl)));

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
