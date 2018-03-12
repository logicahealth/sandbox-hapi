package org.hspconsortium.platform.api.interceptor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import com.ihc.mercury.sm.event.Event;
import com.ihc.mercury.sm.event.Transaction;
import com.ihc.mercury.sm.wsclient.MercuryClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Subscription;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

// todo this should change to be after the resource is saved, not before
@Component
public class SubscriptionSupportBase extends InterceptorAdapter implements IServerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionSupportBase.class);
    private MercuryClient mercuryClient;

    public void handleSubscriptionResource(Subscription subscription, String subscriptionEndpoint) {
        LOGGER.info(prepareLogStatement(subscription));
        sendViaHTTP(subscription, subscriptionEndpoint);
    }

    public void sendToMercury(RestOperationTypeEnum theOperation, IBaseResource resource) {
        Event event = mercuryClient.createEvent();
        event.setContent(resource);
        mercuryClient.process(event);
        LOGGER.info("Published Message to Pub/Sub with id " + event.getId());
    }

    public void handleResource(IBaseResource resource, String resourceEndpoint) {
        if ((resource instanceof Patient)
                || (resource instanceof Observation)
                || (resource instanceof CarePlan)) {
            if (resourceEndpoint != null) {
                sendViaHTTP(resource, resourceEndpoint);
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Resource messaging is not configured for: " + resource);
                }
            }
        }
    }

    /* Prepare a log statement with resource specific info.
         * Ignore NullPointerExceptions caused by missing data.
         * NOTE: This logging is for use during the pubsub demo. */
    private String prepareLogStatement(IBaseResource iBaseResource) {
        String logString;
        if (iBaseResource instanceof Patient) {
            logString = "\n\r" + this.getClass().getSimpleName() +
                    " handling Patient " + ((Patient) iBaseResource).getId();
            try {
                logString = logString + "\n\r    with last name " + ((Patient) iBaseResource).getName().get(0).getFamily();
            } catch (Exception ex) {
            }
            try {
                logString = logString + "\n\r    birth date " + ((Patient) iBaseResource).getBirthDate().toString();
            } catch (Exception ex) {
            }
        } else if (iBaseResource instanceof Observation) {
            logString = "\n\r" + this.getClass().getSimpleName() +
                    " handling Observation " + ((Observation) iBaseResource).getId();
            try {
                logString = logString + "\n\r    with code " + ((Observation) iBaseResource).getCode().getCoding().get(0).getCode();
            } catch (Exception ex) {
            }
            try {
                logString = logString + "\n\r    effective date " + ((Observation) iBaseResource).getEffective().toString();
            } catch (Exception ex) {
            }
        } else if (iBaseResource instanceof Subscription) {
            logString = "\n\r" + this.getClass().getSimpleName() +
                    " handling Subscription Id:" + ((Subscription) iBaseResource).getId();
            try {
                logString = logString + "\n\r    with criteria " + ((Subscription) iBaseResource).getCriteria();
            } catch (Exception ex) {
            }
            try {
                logString = logString + "\n\r    status " + ((Subscription) iBaseResource).getStatus();
            } catch (Exception ex) {
            }
        } else {
            logString = "\n\r" + this.getClass().getSimpleName() +
                    " handling Resource " + iBaseResource.toString();
        }
        return logString;
    }

    private void sendViaHTTP(IBaseResource iBaseResource, String endpoint) {
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.addHeader("Content-Type", "application/json");
        StringEntity entity = null;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(bytes);
            FhirContext.forDstu3().newJsonParser().encodeResourceToWriter(iBaseResource, writer);
            String jsonString = bytes.toString();
            entity = new StringEntity(bytes.toString());
            postRequest.setEntity(entity);

        } catch (IOException e) {
            // log and bury exception
            LOGGER.error("Error Sending Resource", e);
            return;
        }

        CloseableHttpClient httpClient = HttpClients.custom().setConnectionTimeToLive(30, TimeUnit.SECONDS).build();

//        try (CloseableHttpResponse closeableHttpResponse = httpClient.execute(postRequest)) {
        try {
            CloseableHttpResponse closeableHttpResponse = httpClient.execute(postRequest);
            if (closeableHttpResponse.getStatusLine().getStatusCode() != 200) {
                // log and bury exception
                LOGGER.error("Error Sending Resource.  Status Code: " + closeableHttpResponse.getStatusLine().getStatusCode());
            }
            closeableHttpResponse.close();
        } catch (IOException e) {
            // log and bury exception
            LOGGER.error("Error Sending Resource", e);
        }

    }

    public void setMercuryClient(MercuryClient mercuryClient) {
        this.mercuryClient = mercuryClient;
    }

    public MercuryClient getMercuryClient() {
        return this.mercuryClient;
    }

}
