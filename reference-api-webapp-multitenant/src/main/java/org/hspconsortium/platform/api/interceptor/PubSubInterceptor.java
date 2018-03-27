package org.hspconsortium.platform.api.interceptor;

import ca.uhn.fhir.rest.api.server.RequestDetails;
import com.google.common.base.Strings;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hspconsortium.platform.api.controller.HapiFhirServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
@Profile("multitenant")
public class PubSubInterceptor extends SubscriptionSupportBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(PubSubInterceptor.class);

    @Value("${hspc.platform.messaging.pubsub.enabled:false}")
    private boolean enabled;

    @Value("${hspc.platform.messaging.pubsub.subscription.channel.endpoint:}")
    private String forwardUrl = "";

    @Value("#{'${hspc.platform.messaging.pubsub.forSandboxes:}'.split(',')}")
    private Set<String> forSandboxes;

    @Value("${hspc.platform.messaging.pubsub.includeSourceQueryParameter:false}")
    private boolean includeSourceQueryParameter;

//    @Value("#{'${hspc.platform.messaging.pubsub.subscription.forResources:}'.split(',')}")
//    private Set<String> forResources;

    @Override
    public boolean outgoingResponse(RequestDetails theRequestDetails, IBaseResource theResponseObject, HttpServletRequest theServletRequest, HttpServletResponse theServletResponse) {
        if (enabled) {
            LOGGER.info("forwardUrl: " + forwardUrl);
            LOGGER.info("forSandboxes: " + forSandboxes);
            LOGGER.info("forSandboxes.size(): " + forSandboxes.size());
            LOGGER.info("includeSourceQueryParameter: " + includeSourceQueryParameter);

            switch (theRequestDetails.getRestOperationType()) {
                case CREATE:
                case UPDATE:
                case DELETE:
                    if (!Strings.isNullOrEmpty(forwardUrl)) {
//                        if (forResources.contains(theResponseObject.getClass().getSimpleName())) {
                        String requestSandbox = HapiFhirServlet.getTenantPart(theServletRequest.getServletPath());
                        if (forSandboxes.contains(requestSandbox)) {
                            LOGGER.info("Matched resource: " + theResponseObject.getIdElement().getIdPart());
                            if (includeSourceQueryParameter) {
                                try {
                                    String fhirRootPath = theRequestDetails.getFhirServerBase();
                                    fhirRootPath = fhirRootPath.substring(0, fhirRootPath.indexOf(theRequestDetails.getRequestPath())-1);
                                    handleResource(theResponseObject, forwardUrl
                                            + "?source="
                                            + URLEncoder.encode(fhirRootPath, StandardCharsets.UTF_8.toString()));
                                } catch (Exception e) {
                                    LOGGER.error("Error handling resource for: " + theRequestDetails.getFhirServerBase(), e);
                                }
                            } else {
                                handleResource(theResponseObject, forwardUrl);
                            }
                        }
//                        }
                    }
                    break;
            }
        }

        return super.outgoingResponse(theRequestDetails, theResponseObject, theServletRequest, theServletResponse);
    }

    @Override
    protected void handleResource(IBaseResource resource, String resourceEndpoint) {
        sendViaHTTP(resource, resourceEndpoint);
    }
}
