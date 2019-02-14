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

package org.hspconsortium.platform.api.controller;

import org.junit.Test;

import javax.servlet.ServletException;

import static org.mockito.Mockito.mock;

public class HapiFhirServletTest {

    private HapiFhirServletContextHolder hapiFhirServletContextHolder = mock(HapiFhirServletContextHolder.class);

    private HapiFhirServlet hapiFhirServlet = new HapiFhirServlet();

    //Can't test this class until HapiFhirServletContextHolder is refactored to make it testable

//    @Test
//    public void initialize() throws ServletException {
//        hapiFhirServlet.initialize();
//    }

}
