package org.hspconsortium.platform.api.interceptor;

import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import org.hl7.fhir.dstu3.model.Subscription;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// todo this should change to be after the resource is saved, not before
@Component
public class SubscriptionSupportInterceptor extends SubscriptionSupportBase implements IServerInterceptor {

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
            IBaseResource iBaseResource = theProcessedRequest.getResource();
            if (iBaseResource != null) {
                if (iBaseResource instanceof Subscription) {
                    super.handleSubscriptionResource((Subscription) iBaseResource, subscriptionEndpoint);
                } else {
                    super.handleResource(iBaseResource, resourceEndpoint);
                }
            }
        }
        super.incomingRequestPreHandled(theOperation, theProcessedRequest);
    }
}
