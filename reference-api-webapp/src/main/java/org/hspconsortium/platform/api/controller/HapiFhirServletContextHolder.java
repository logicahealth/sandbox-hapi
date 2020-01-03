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
