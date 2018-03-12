package org.hspconsortium.platform.api.interceptor;

import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import com.ihc.mercury.sm.wsclient.MercuryClient;
import com.ihc.mercury.sm.wsclient.MercuryClientFactory;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class PubSubInterceptor extends SubscriptionSupportBase implements IServerInterceptor {
    @Value("${hspc.platform.messaging.pubsub.subscriptionEndpointEx}")
    private String subscriptionEndpointEx;

    @Value("${hspc.platform.messaging.pubsub.user}")
    private String user;

    @Value("${hspc.platform.messaging.pubsub.password}")
    private String password;

    @Value("${hspc.platform.messaging.pubsub.securityMode}")
    private String securityMode;

    @Value("${hspc.platform.messaging.pubsub.enabled}")
    private String enabled;


    @Override
    public void incomingRequestPreHandled(RestOperationTypeEnum theOperation, ActionRequestDetails theProcessedRequest) {
        if (Boolean.valueOf(enabled)) {
            if (getMercuryClient() == null) {
                Properties mercuryProperties = new Properties();
                mercuryProperties.setProperty(MercuryClient.CLIENT_ID, user);
                mercuryProperties.setProperty(MercuryClient.CLIENT_SECRET, password);
                mercuryProperties.setProperty(MercuryClient.SERVER_URL, subscriptionEndpointEx);
                mercuryProperties.setProperty(MercuryClient.API_SECURITY_MODE, securityMode);
                System.setProperty(MercuryClient.API_SECURITY_MODE, securityMode);
                setMercuryClient(MercuryClientFactory.getClientFactory(mercuryProperties).getMercuryClient());
            }
            IBaseResource iBaseResource = theProcessedRequest.getResource();
            if (iBaseResource != null) {
                super.sendToMercury(theOperation, iBaseResource);
            }
        }
        super.incomingRequestPreHandled(theOperation, theProcessedRequest);
    }
}
