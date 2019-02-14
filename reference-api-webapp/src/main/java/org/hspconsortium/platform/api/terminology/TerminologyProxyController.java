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

package org.hspconsortium.platform.api.terminology;

import org.hspconsortium.platform.api.proxy.HttpProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@RestController
@RequestMapping("/terminology")
public class TerminologyProxyController {

    private HttpProxy httpProxy;

    @Autowired
    public TerminologyProxyController(HttpProxy httpProxy) {
        this.httpProxy = httpProxy;
    }

    @RequestMapping(value = "/{startOfProxiedPath}/**", method = RequestMethod.GET)
    public void handleLaunchRequest(HttpServletRequest request, HttpServletResponse response,
                                    @Value("${hspc.platform.api.fhir.terminology.proxy.contextPath:}") String contextPath,
                                    @PathVariable String startOfProxiedPath) {
        String proxiedPath = contextPath + "/"
                + request.getRequestURL().substring(request.getRequestURL().indexOf(startOfProxiedPath));

        httpProxy.proxy(proxiedPath, request, response);
    }
}

