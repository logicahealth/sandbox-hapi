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

package org.hspconsortium.platform.api.proxy;

import org.springframework.http.client.ClientHttpRequest;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RequestCallback;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class DefaultRequestCallback extends LoggingObject implements RequestCallback {

    private HttpServletRequest httpRequest;
    private String[] headerNamesToCopy;

    /**
     * TODO May want the ability to specify header names to exclude instead of including.
     */
    public DefaultRequestCallback(HttpServletRequest httpRequest, String... headerNamesToCopy) {
        this.httpRequest = httpRequest;
        this.headerNamesToCopy = headerNamesToCopy;
    }

    @Override
    public void doWithRequest(ClientHttpRequest request) throws IOException {
        copyHeaders(httpRequest, request);
        FileCopyUtils.copy(httpRequest.getInputStream(), request.getBody());
    }

    protected void copyHeaders(HttpServletRequest httpRequest, ClientHttpRequest request) {
        if (headerNamesToCopy != null) {
            for (String name : headerNamesToCopy) {
                String value = httpRequest.getHeader(name);
                if (logger.isDebugEnabled()) {
                    logger.debug(format("Setting client HTTP header '%s' to '%s'", name, value));
                }
                request.getHeaders().set(name, value);
            }
        }
    }

    public void setHeaderNamesToCopy(String[] headerNamesToInclude) {
        this.headerNamesToCopy = headerNamesToInclude;
    }

}