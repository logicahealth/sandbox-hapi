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

import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.hspconsortium.platform.api.authorization.ScopeBasedAuthorizationParams;
import org.hspconsortium.platform.api.oauth2.HspcOAuth2Authentication;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

public class ScopeBasedAuthorizationInterceptorTest {

    private ScopeBasedAuthorizationParams scopeBasedAuthorizationParams;
    private ScopeBasedAuthorizationInterceptor scopeBasedAuthorizationInterceptor;

    @Before
    public void setUp() {
        scopeBasedAuthorizationParams = mock(ScopeBasedAuthorizationParams.class);
        scopeBasedAuthorizationInterceptor = new ScopeBasedAuthorizationInterceptor();
    }

    @Test
    public void testIncomingRequestPostProcessedRestOperationTypeEnumSEARCHSystem(){
        RequestDetails theRequestDetails = mock(RequestDetails.class);
        HttpServletRequest theRequest = mock(HttpServletRequest.class);
        HttpServletResponse theResponse = mock(HttpServletResponse.class);
        when(theRequestDetails.getRestOperationType()).thenReturn(RestOperationTypeEnum.SEARCH_SYSTEM);
        boolean result = scopeBasedAuthorizationInterceptor.incomingRequestPostProcessed(theRequestDetails, theRequest, theResponse);
        Assert.assertTrue(result);
    }

    @Test
    public void testIncomingRequestPostProcessedAuthenticationIsNull(){
        RequestDetails theRequestDetails = mock(RequestDetails.class);
        HttpServletRequest theRequest = mock(HttpServletRequest.class);
        HttpServletResponse theResponse = mock(HttpServletResponse.class);
        when(theRequestDetails.getRestOperationType()).thenReturn(RestOperationTypeEnum.SEARCH_TYPE);
        boolean result = scopeBasedAuthorizationInterceptor.incomingRequestPostProcessed(theRequestDetails, theRequest, theResponse);
        Assert.assertTrue(result);
    }
}
