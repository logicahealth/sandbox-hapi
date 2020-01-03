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

package org.hspconsortium.platform.api.proxy;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestOperations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpProxy extends LoggingObject {

    private RestOperations restOperations;
    private String protocol;
    private String host;
    private Integer port;

    public HttpProxy(RestOperations restTemplate, String protocol, String host, Integer port) {
        this.restOperations = restTemplate;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
    }

    /**
     * Proxy a request without copying any headers.
     */
    public void proxy(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        proxy(httpRequest.getServletPath(), httpRequest, httpResponse);
    }

    /**
     * Proxy a request and copy the given headers on both the request and the response.
     */
    public void proxy(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String... headerNamesToCopy) {
        proxy(httpRequest.getServletPath(), httpRequest, httpResponse, new DefaultRequestCallback(httpRequest,
                headerNamesToCopy), new DefaultResponseExtractor(httpResponse, headerNamesToCopy));
    }

    /**
     * Proxy a request, using the given path instead of the servlet path in the HttpServletRequest.
     */
    public void proxy(String path, HttpServletRequest httpRequest, HttpServletResponse httpResponse,
                      String... headerNamesToCopy) {
        proxy(path, httpRequest, httpResponse, new DefaultRequestCallback(httpRequest, headerNamesToCopy),
                new DefaultResponseExtractor(httpResponse, headerNamesToCopy));
    }

    /**
     * Specify your own request callback and response extractor. This gives you the most flexibility, but does the least
     * for you.
     */
    public <T> T proxy(String path, HttpServletRequest httpRequest, HttpServletResponse httpResponse,
                       RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) {
        URI uri = buildUri(httpRequest, protocol, host, port, path);

        if (logger.isInfoEnabled()) {
            logger.info(format("Proxying to URI: %s", uri));
        }

        HttpMethod method = determineMethod(httpRequest);
        return restOperations.execute(uri, method, requestCallback, responseExtractor);
    }

    protected HttpMethod determineMethod(HttpServletRequest request) {
        return HttpMethod.valueOf(request.getMethod());
    }

    protected URI buildUri(HttpServletRequest httpRequest, String protocol, String host, Integer port, String path) {
        try {
            if (port != null) {
                return new URI(protocol, null, host, port, path, httpRequest.getQueryString(), null);
            } else {
                return new URI(protocol, host, path, httpRequest.getQueryString(), null);
            }
        } catch (URISyntaxException ex) {
            throw new RuntimeException("Unable to build URI, cause: " + ex.getMessage(), ex);
        }
    }
}