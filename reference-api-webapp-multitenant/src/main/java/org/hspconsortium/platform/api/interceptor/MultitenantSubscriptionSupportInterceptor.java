package org.hspconsortium.platform.api.interceptor;

import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import org.hl7.fhir.dstu3.model.Subscription;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hspconsortium.platform.api.controller.MultitenantHapiFhirController;
import org.hspconsortium.platform.api.model.Sandbox;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class MultitenantSubscriptionSupportInterceptor extends SubscriptionSupportBase implements IServerInterceptor {

    public static final String SANDBOX_SUBSCRIPTION_ENABLED_PROPERTY = "subscription=enabled";

    @Value("${hspc.platform.messaging.subscriptionSupport.subscriptionEndpoint}")
    private String subscriptionEndpoint;

    @Value("${hspc.platform.messaging.subscriptionSupport.resourceEndpoint}")
    private String resourceEndpoint;

    @Value("${hspc.platform.messaging.subscriptionSupport.enabled}")
    private String enabled;

    @Override
    public void incomingRequestPreHandled(RestOperationTypeEnum theOperation, ActionRequestDetails theProcessedRequest) {
        // enabled on a system level?
        if (Boolean.valueOf(enabled)) {
            // enabled on a sandbox level
            ServletRequestDetails servletRequestDetails = (ServletRequestDetails)theProcessedRequest.getRequestDetails();
            HttpServletRequest theServletRequest = servletRequestDetails.getServletRequest();
            Sandbox sandbox = (Sandbox) theServletRequest.getAttribute(MultitenantHapiFhirController.SANDBOX_OBJECT_ATTRIBUTE);
            if (sandbox != null) {
                String properties = sandbox.getProperties();
                if (properties != null && properties.contains(SANDBOX_SUBSCRIPTION_ENABLED_PROPERTY)) {
                    IBaseResource iBaseResource = theProcessedRequest.getResource();
                    if (iBaseResource != null) {
                        if (iBaseResource instanceof Subscription) {
                            super.handleSubscriptionResource((Subscription) iBaseResource, subscriptionEndpoint);
                        } else {
                            super.handleResource(iBaseResource, resourceEndpoint);
                        }
                    }
                }
            }
        }
        super.incomingRequestPreHandled(theOperation, theProcessedRequest);
    }
}
