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

package org.hspconsortium.platform.api.fhir.rp;

import ca.uhn.fhir.jpa.rp.dstu3.MeasureReportResourceProvider;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.MeasureReport;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IAnyResource;

import java.util.ArrayList;
import java.util.List;

public class MeasureReportResourceProviderHspc extends MeasureReportResourceProvider {

    @Operation(name = "$submit-data", idempotent = true)
    public Resource submitData(
            RequestDetails details,
            @IdParam IdType theId,
            @OperationParam(name="measure-report", min = 1, max = 1, type = MeasureReport.class) MeasureReport report,
            @OperationParam(name="resource") List<IAnyResource> resources)
    {
        Bundle transactionBundle = new Bundle().setType(Bundle.BundleType.TRANSACTION);

        /*
            TODO - resource validation using $data-requirements operation
            (params are the provided id and the measurement period from the MeasureReport)
            TODO - profile validation ... not sure how that would work ...
            (get StructureDefinition from URL or must it be stored in Ruler?)
        */

        for (IAnyResource resource : resources) {
            Resource res = (Resource) resource;
            if (res instanceof Bundle) {
                for (Bundle.BundleEntryComponent entry : createTransactionBundle((Bundle) res).getEntry()) {
                    transactionBundle.addEntry(entry);
                }
            }
            else {
                // Build transaction bundle
                transactionBundle.addEntry(createTransactionEntry(res));
            }
        }

        transactionBundle.addEntry(createTransactionEntry(report));

        IGenericClient client = this.getContext().newRestfulGenericClient(details.getFhirServerBase());

        if (details.getHeader("Authorization") != null) {
            String[] splitAuth = details.getHeader("Authorization").split(" ");
            String token = splitAuth[1];
            BearerTokenAuthInterceptor authInterceptor = new BearerTokenAuthInterceptor(token);
            client.registerInterceptor(authInterceptor);
        }

        return client.transaction().withBundle(transactionBundle).execute();
    }

    private Bundle createTransactionBundle(Bundle bundle) {
        Bundle transactionBundle;
        if (bundle != null) {
            if (bundle.hasType() && bundle.getType() == Bundle.BundleType.TRANSACTION) {
                transactionBundle = bundle;
            }
            else {
                transactionBundle = new Bundle().setType(Bundle.BundleType.TRANSACTION);
                if (bundle.hasEntry()) {
                    for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                        if (entry.hasResource()) {
                            transactionBundle.addEntry(createTransactionEntry(entry.getResource()));
                        }
                    }
                }
            }
        }
        else {
            transactionBundle = new Bundle().setType(Bundle.BundleType.TRANSACTION).setEntry(new ArrayList<>());
        }

        return transactionBundle;
    }

    private Bundle.BundleEntryComponent createTransactionEntry(Resource resource) {
        Bundle.BundleEntryComponent transactionEntry = new Bundle.BundleEntryComponent().setResource(resource);
        if (resource.hasId()) {
            transactionEntry.setRequest(
                    new Bundle.BundleEntryRequestComponent()
                            .setMethod(Bundle.HTTPVerb.PUT)
                            .setUrl(resource.getId())
            );
        }
        else {
            transactionEntry.setRequest(
                    new Bundle.BundleEntryRequestComponent()
                            .setMethod(Bundle.HTTPVerb.POST)
                            .setUrl(resource.fhirType())
            );
        }
        return transactionEntry;
    }
}
