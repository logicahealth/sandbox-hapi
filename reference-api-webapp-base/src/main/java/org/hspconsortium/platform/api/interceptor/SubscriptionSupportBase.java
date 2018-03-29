package org.hspconsortium.platform.api.interceptor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hl7.fhir.instance.model.OperationOutcome;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

abstract public class SubscriptionSupportBase extends InterceptorAdapter implements IServerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionSupportBase.class);

    abstract protected void handleResource(IBaseResource resource, String resourceEndpoint);

    protected void sendViaHTTP(IBaseResource iBaseResource, String endpoint) {
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.addHeader("Content-Type", "application/json");
        StringEntity entity = null;
        try {
            LOGGER.info("Sending " + iBaseResource.getIdElement() + " to " + endpoint);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(bytes);
            switch (iBaseResource.getStructureFhirVersionEnum()) {
                case DSTU2:
                    if (iBaseResource instanceof OperationOutcome) {
                        OperationOutcome operationOutcome = (OperationOutcome)iBaseResource;
                        operationOutcome.getResourceType();
                    }
                    FhirContext.forDstu2().newJsonParser().encodeResourceToWriter(iBaseResource, writer);
                    break;
                case DSTU3:
                    FhirContext.forDstu3().newJsonParser().encodeResourceToWriter(iBaseResource, writer);
                    break;
                case R4:
                    FhirContext.forR4().newJsonParser().encodeResourceToWriter(iBaseResource, writer);
                    break;
            }
            String jsonString = bytes.toString();
            entity = new StringEntity(jsonString);
            postRequest.setEntity(entity);
        } catch (IOException e) {
            // log exception and disregard
            LOGGER.error("Error Sending Resource", e);
            return;
        }

        CloseableHttpClient httpClient = HttpClients.custom().setConnectionTimeToLive(30, TimeUnit.SECONDS).build();
        try (CloseableHttpResponse closeableHttpResponse = httpClient.execute(postRequest)) {
            if (closeableHttpResponse.getStatusLine().getStatusCode() != 200) {
                // log and bury exception
                LOGGER.error("Error sending resource to [" + endpoint + "]  Status Code: " + closeableHttpResponse.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            // log and bury exception
            LOGGER.error("Error Sending Resource", e);
        }
    }
}
