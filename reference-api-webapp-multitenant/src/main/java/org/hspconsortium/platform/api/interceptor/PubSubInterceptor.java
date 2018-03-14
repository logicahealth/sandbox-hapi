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

    @Value("#{'${hspc.platform.messaging.pubsub.subscription.forResources:}'.split(',')}")
    private Set<String> forResources;

    @Override
    public boolean outgoingResponse(RequestDetails theRequestDetails, IBaseResource theResponseObject, HttpServletRequest theServletRequest, HttpServletResponse theServletResponse) {
        if (enabled) {
            switch (theRequestDetails.getRestOperationType()) {
                case CREATE:
                case UPDATE:
                case DELETE:
                    if (!Strings.isNullOrEmpty(forwardUrl)) {
                        if (forResources.contains(theResponseObject.getClass().getSimpleName())) {
                            // todo match the desired sandbox
                            String requestSandbox = HapiFhirServlet.getTenantPart(theServletRequest.getServletPath());
                            if (forSandboxes.contains(requestSandbox)) {
                                LOGGER.info("Matched resource: " + theResponseObject.getIdElement().getIdPart());
                                handleResource(theResponseObject, forwardUrl);
                            }
                        }
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
