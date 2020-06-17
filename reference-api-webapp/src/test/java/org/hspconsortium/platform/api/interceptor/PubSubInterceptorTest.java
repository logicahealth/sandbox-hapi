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

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import static org.mockito.Mockito.*;

public class PubSubInterceptorTest {
    private boolean enabled;
    private String forwardUrl = "";
    private Set<String> forSandboxes;
    private boolean includeSourceQueryParameter;
    private PubSubInterceptor pubSubInterceptor;

    @Before
    public void setUp() {
        pubSubInterceptor = new PubSubInterceptor();
        forSandboxes = mock(Set.class);
    }

    @Test
    public void testOutgoingResponseIfForwardUrlIsEmpty(){
        enabled = true;
        RequestDetails theRequestDetails = mock(RequestDetails.class);
        IBaseResource theResponseObject = mock(IBaseResource.class);
        HttpServletRequest theServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse theServletResponse = mock(HttpServletResponse.class);
        when(theRequestDetails.getRestOperationType()).thenReturn(RestOperationTypeEnum.CREATE);
        boolean result = pubSubInterceptor.outgoingResponse(theRequestDetails, theResponseObject, theServletRequest, theServletResponse);
        Assert.assertTrue(result);
    }

    @Test
    public void testOutgoingResponseForSandboxesIsEmpty(){
        enabled = true;
        forwardUrl = "/forwardUrl";
        RequestDetails theRequestDetails = mock(RequestDetails.class);
        IBaseResource theResponseObject = mock(IBaseResource.class);
        HttpServletRequest theServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse theServletResponse = mock(HttpServletResponse.class);
        when(theRequestDetails.getRestOperationType()).thenReturn(RestOperationTypeEnum.CREATE);
        when(forSandboxes.contains(any())).thenReturn(false);
        boolean result = pubSubInterceptor.outgoingResponse(theRequestDetails, theResponseObject, theServletRequest, theServletResponse);
        Assert.assertTrue(result);
    }

    @Test
    public void testOutgoingResponse(){
        enabled = true;
        forwardUrl = "/forwardUrl";
        RequestDetails theRequestDetails = mock(RequestDetails.class);
        IBaseResource theResponseObject = mock(IBaseResource.class);
        HttpServletRequest theServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse theServletResponse = mock(HttpServletResponse.class);
        when(theRequestDetails.getRestOperationType()).thenReturn(RestOperationTypeEnum.CREATE);
        when(forSandboxes.contains(any())).thenReturn(true);
        when(theResponseObject.getStructureFhirVersionEnum()).thenReturn(FhirVersionEnum.DSTU2);
        boolean result = pubSubInterceptor.outgoingResponse(theRequestDetails, theResponseObject, theServletRequest, theServletResponse);
        Assert.assertTrue(result);
    }

    @Test
    public void testFindTargetResourceDSTU2(){
        RequestDetails theRequestDetails = mock(RequestDetails.class);
        IBaseResource theResponseObject = mock(IBaseResource.class);
        when(theResponseObject.getStructureFhirVersionEnum()).thenReturn(FhirVersionEnum.DSTU2);
        IBaseResource result = pubSubInterceptor.findTargetResource(theRequestDetails, theResponseObject);
        Assert.assertNotNull(result);
    }

    @Test
    public void testFindTargetResourceDSTU3(){
        RequestDetails theRequestDetails = mock(RequestDetails.class);
        IBaseResource theResponseObject = mock(IBaseResource.class);
        when(theResponseObject.getStructureFhirVersionEnum()).thenReturn(FhirVersionEnum.DSTU2);
        IBaseResource result = pubSubInterceptor.findTargetResource(theRequestDetails, theResponseObject);
        Assert.assertNotNull(result);
    }

    @Test (expected = RuntimeException.class)
    public void testFindTargetResourceR4(){
        RequestDetails theRequestDetails = mock(RequestDetails.class);
        IBaseResource theResponseObject = mock(IBaseResource.class);
        when(theResponseObject.getStructureFhirVersionEnum()).thenReturn(FhirVersionEnum.R4);
        pubSubInterceptor.findTargetResource(theRequestDetails, theResponseObject);
    }

    @Test (expected = RuntimeException.class)
    public void testFindTargetResourceAnyOther(){
        RequestDetails theRequestDetails = mock(RequestDetails.class);
        IBaseResource theResponseObject = mock(IBaseResource.class);
        when(theResponseObject.getStructureFhirVersionEnum()).thenReturn(FhirVersionEnum.DSTU2_HL7ORG);
        pubSubInterceptor.findTargetResource(theRequestDetails, theResponseObject);
    }
}
