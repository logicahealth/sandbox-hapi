package org.hspconsortium.platform.api.controller;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.springframework.web.context.WebApplicationContext;

public class HapiFhirServletContextHolder {

    private static HapiFhirServletContextHolder hapiFhirServletContextHolder = new HapiFhirServletContextHolder();

    public static HapiFhirServletContextHolder getInstance() {
        return hapiFhirServletContextHolder;
    }

    private WebApplicationContext myAppCtx;


    private String fhirMappingPath;

    private String openMappingPath;

    private FhirVersionEnum fhirVersionEnum;

    public void init(WebApplicationContext myAppCtx, String fhirMappingPath, String openMappingPath,
                     FhirVersionEnum fhirVersionEnum) {
        this.myAppCtx = myAppCtx;
        this.fhirMappingPath = fhirMappingPath;
        this.openMappingPath = openMappingPath;
        this.fhirVersionEnum = fhirVersionEnum;
    }

    public WebApplicationContext getMyAppCtx() {
        return myAppCtx;
    }

    public String getFhirMappingPath() {
        return fhirMappingPath;
    }

    public String getOpenMappingPath() {
        return openMappingPath;
    }

    public FhirVersionEnum getFhirVersionEnum() {
        return fhirVersionEnum;
    }
}
