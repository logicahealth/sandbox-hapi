/**
 *  * #%L
 *  *
 *  * %%
 *  * Copyright (C) 2014-2020 Healthcare Services Platform Consortium
 *  * %%
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  * #L%
 */

package org.hspconsortium.platform.api.interceptor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.OperationOutcome;
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
    public boolean incomingRequestPostProcessed(RequestDetails theRequestDetails, HttpServletRequest theRequest, HttpServletResponse theResponse) throws AuthenticationException {
        return super.incomingRequestPostProcessed(theRequestDetails, theRequest, theResponse);
    }

    @Override
    public void incomingRequestPreHandled(RestOperationTypeEnum theOperation, ActionRequestDetails theProcessedRequest) {
        super.incomingRequestPreHandled(theOperation, theProcessedRequest);
    }

    @Override
    public boolean incomingRequestPreProcessed(HttpServletRequest theRequest, HttpServletResponse theResponse) {
        return super.incomingRequestPreProcessed(theRequest, theResponse);
    }

    @Override
    public boolean outgoingResponse(RequestDetails theRequestDetails) {
        return super.outgoingResponse(theRequestDetails);
    }

    @Override
    public boolean outgoingResponse(RequestDetails theRequestDetails, IBaseResource theResponseObject, HttpServletRequest theServletRequest, HttpServletResponse theServletResponse) {
        if (enabled) {
            switch (theRequestDetails.getRestOperationType()) {
                case CREATE:
                case UPDATE:
//                case DELETE:
                    if (!Strings.isNullOrEmpty(forwardUrl)) {
//                        if (forResources.contains(theResponseObject.getClass().getSimpleName())) {
                        String requestSandbox = HapiFhirServlet.getTenantPart(theServletRequest.getServletPath());

                        if (forSandboxes.contains(requestSandbox)) {
                            IBaseResource targetResource = findTargetResource(theRequestDetails, theResponseObject);
                            if (targetResource == null) {
                                LOGGER.warn("Unable to find resource for request: " + theRequestDetails);
                                break;
                            }
                            LOGGER.info("Matched resource: " + theResponseObject.getIdElement().getIdPart());
                            if (includeSourceQueryParameter) {
                                try {
                                    String fhirRootPath = theRequestDetails.getFhirServerBase();
                                    if (fhirRootPath.contains("/data")) {
                                        fhirRootPath = fhirRootPath.substring(0, fhirRootPath.indexOf("/data"));
                                    } else if (fhirRootPath.contains("/open")) {
                                        fhirRootPath = fhirRootPath.substring(0, fhirRootPath.indexOf("/open"));
                                    }
                                    LOGGER.info("Source path: " + fhirRootPath);
                                    handleResource(targetResource, forwardUrl
                                            + "?source="
                                            + URLEncoder.encode(fhirRootPath, StandardCharsets.UTF_8.toString()));
                                } catch (Exception e) {
                                    LOGGER.error(
                                            "Error handling resource for server: "
                                                    + theRequestDetails.getFhirServerBase()
                                                    + " path: " + theRequestDetails.getRequestPath()
                                                    + " forwardUrl: " + forwardUrl, e);
                                }
                            } else {
                                handleResource(targetResource, forwardUrl);
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

    protected IBaseResource findTargetResource(RequestDetails theRequestDetails, IBaseResource theResponseObject) {
        switch (theResponseObject.getStructureFhirVersionEnum()) {
            case DSTU2:
                return getTargetResourceForDSTU2(theRequestDetails, theResponseObject);
            case DSTU3:
                return getTargetResourceForSTU3(theRequestDetails, theResponseObject);
            case R4:
                throw new RuntimeException("R4 is not supported for PubSubInterceptor");
        }
        throw new RuntimeException("No match");
    }

    protected String extractIdFromDiagnosisString(String diagnosisString) {
        return diagnosisString.split("\"")[1];
    }

//    protected String extractIdFromRequestPath(RequestDetails theRequestDetails) {
//        return theRequestDetails.getRequestPath();
//    }

    protected String[] splitResourceId(String resourceIdString) {
        return resourceIdString.split("/");
    }

    private IBaseResource getTargetResourceForDSTU2(RequestDetails theRequestDetails, IBaseResource theResponseObject) {
        if (theResponseObject instanceof ca.uhn.fhir.model.dstu2.resource.OperationOutcome) {
            ca.uhn.fhir.model.dstu2.resource.OperationOutcome operationOutcome = (ca.uhn.fhir.model.dstu2.resource.OperationOutcome) theResponseObject;
            if (operationOutcome.getIssue().size() > 0) {
                String diagnosisString = operationOutcome.getIssue().get(0).getDiagnostics();
                if (StringUtils.isNotEmpty(diagnosisString)) {
                    String resourceIdString = extractIdFromDiagnosisString(diagnosisString);
//                    String resourceIdString = extractIdFromRequestPath(theRequestDetails);
                    FhirContext ctx = FhirContext.forDstu2();
                    String serverBase = theRequestDetails.getFhirServerBase();
                    String[] resourceIdParts = splitResourceId(resourceIdString);
                    return getTargetResource(ctx, serverBase, resourceIdParts[0], resourceIdParts[1]);
                }
            }
            LOGGER.info("OperationOutcome does not contain an issue diagnosis: " + operationOutcome);
            return null;
        } else {
            return theResponseObject;
        }
    }

    private IBaseResource getTargetResourceForSTU3(RequestDetails theRequestDetails, IBaseResource theResponseObject) {
        if (theResponseObject instanceof org.hl7.fhir.dstu3.model.OperationOutcome) {
            org.hl7.fhir.dstu3.model.OperationOutcome operationOutcome = (org.hl7.fhir.dstu3.model.OperationOutcome) theResponseObject;
            if (operationOutcome.getIssue().size() > 0) {
                String diagnosisString = operationOutcome.getIssue().get(0).getDiagnostics();
                if (StringUtils.isNotEmpty(diagnosisString)) {
                    String resourceIdString = extractIdFromDiagnosisString(diagnosisString);
//                    String resourceIdString = extractIdFromRequestPath(theRequestDetails);
                    FhirContext ctx = FhirContext.forDstu3();
                    String serverBase = theRequestDetails.getFhirServerBase();
                    String[] resourceIdParts = splitResourceId(resourceIdString);
                    return getTargetResource(ctx, serverBase, resourceIdParts[0], resourceIdParts[1]);
                }
            }
            LOGGER.info("OperationOutcome does not contain an issue diagnosis: " + operationOutcome);
            return null;
        } else {
            return theResponseObject;
        }
    }

    private IBaseResource getTargetResourceForR4(RequestDetails theRequestDetails, IBaseResource theResponseObject) {
        if (theResponseObject instanceof OperationOutcome) {
            OperationOutcome operationOutcome = (OperationOutcome) theResponseObject;
            if (operationOutcome.getIssue().size() > 0) {
                String diagnosisString = operationOutcome.getIssue().get(0).getDiagnostics();
                if (StringUtils.isNotEmpty(diagnosisString)) {
                    String resourceIdString = extractIdFromDiagnosisString(diagnosisString);
//                    String resourceIdString = extractIdFromRequestPath(theRequestDetails);
                    FhirContext ctx = FhirContext.forR4();
                    String serverBase = theRequestDetails.getFhirServerBase();
                    String[] resourceIdParts = splitResourceId(resourceIdString);
                    return getTargetResource(ctx, serverBase, resourceIdParts[0], resourceIdParts[1]);
                }
            }
            LOGGER.info("OperationOutcome does not contain an issue diagnosis: " + operationOutcome);
            return null;
        } else {
            return theResponseObject;
        }
    }

    private IBaseResource getTargetResource(FhirContext ctx, String serverBase, String resourceTypeStr, String resourceIdStr) {
        IGenericClient client = ctx.newRestfulGenericClient(serverBase);

        IBaseResource targetResource = client.read()
                .resource(resourceTypeStr)
                .withId(resourceIdStr)
                .execute();

        return targetResource;
    }
}
