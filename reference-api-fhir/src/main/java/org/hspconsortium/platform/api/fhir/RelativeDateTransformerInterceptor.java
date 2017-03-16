package org.hspconsortium.platform.api.fhir;

import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

public class RelativeDateTransformerInterceptor extends InterceptorAdapter {

    @Value("${hspc.platform.api.fhir.relativeDateTransformerEnabled:false}")
    private String enabled;

    private LocalDate baselineDate = null;

    public RelativeDateTransformerInterceptor(LocalDate baselineDate) {
        this.baselineDate = baselineDate;
    }

    @Override
    public void incomingRequestPreHandled(RestOperationTypeEnum theOperation, ActionRequestDetails theProcessedRequest) {
        boolean isEnabled = Boolean.valueOf(enabled);
        if (isEnabled && baselineDate != null) {
            // transform dates
            switch (theOperation) {
                case CREATE:
                case DELETE:
                case UPDATE:
                    IBaseResource resource = theProcessedRequest.getResource();

                    // todo need to do something here
                    break;
                default:
                    break;
            }
            super.incomingRequestPreHandled(theOperation, theProcessedRequest);
        } else {
            super.incomingRequestPreHandled(theOperation, theProcessedRequest);
        }
    }
}
