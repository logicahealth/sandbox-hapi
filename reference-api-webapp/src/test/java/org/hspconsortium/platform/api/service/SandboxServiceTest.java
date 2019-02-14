/**
 *  * #%L
 *  *
 *  * %%
 *  * Copyright (C) 2014-2019 Healthcare Services Platform Consortium
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

package org.hspconsortium.platform.api.service;

import org.hspconsortium.platform.api.persister.SandboxPersister;
import org.hspconsortium.platform.api.security.TenantInfoRequestMatcher;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class SandboxServiceTest {

    private SandboxPersister sandboxPersister = mock(SandboxPersister.class);
    private TenantInfoRequestMatcher tenantInfoRequestMatcher = mock(TenantInfoRequestMatcher.class);
    private RestTemplate restTemplate = mock(RestTemplate.class);

    private SandboxServiceImpl sandboxService = new SandboxServiceImpl(sandboxPersister, tenantInfoRequestMatcher, restTemplate);

    @Before
    public void setup() {

    }

//    @Test
//    public void resetTest() {
//        sandboxService.reset();
//        verify(tenantInfoRequestMatcher).reset();
//    }
}
