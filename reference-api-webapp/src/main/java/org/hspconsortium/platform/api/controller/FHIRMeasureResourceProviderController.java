package org.hspconsortium.platform.api.controller;

import ca.uhn.fhir.jpa.rp.dstu3.MeasureResourceProvider;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OperationParam;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.hl7.fhir.dstu3.model.*;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.opencds.cqf.cql.data.fhir.FhirDataProviderStu3;

import java.util.ArrayList;
import java.util.List;

@RestController
//@RequestMapping("${hspc.platform.api.sandboxPath:/sandbox}")
@Profile("default")
public class FHIRMeasureResourceProviderController extends MeasureResourceProvider {

    private FhirDataProviderStu3 provider;

    @RequestMapping(value = "$submit-data", method = RequestMethod.PUT)
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

        // TODO - use FhirSystemDaoDstu3 to call transaction operation directly instead of building client
        provider.setEndpoint(details.getFhirServerBase());
        return provider.getFhirClient().transaction().withBundle(transactionBundle).execute();
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
