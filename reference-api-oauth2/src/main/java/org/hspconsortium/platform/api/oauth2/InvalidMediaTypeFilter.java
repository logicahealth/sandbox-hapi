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

package org.hspconsortium.platform.api.oauth2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class InvalidMediaTypeFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvalidMediaTypeFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            if (request.getContentType() != null) {
                MediaType.parseMediaType(request.getContentType());
            }

            // successfully parsed mediaType
            chain.doFilter(request, response);
        } catch (InvalidMediaTypeException e) {
            LOGGER.error("Unsupported media type: " + request.getContentType()
                    + " received for request: " + buildRequestLogMessage(request));
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Requested MIME type is not supported: " + request.getContentType());
        }
    }

    private String buildRequestLogMessage(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        Map<String, String> requestMap = this.getTypesafeRequestMap(httpServletRequest);

        final StringBuilder logMessage = new StringBuilder("REST Request - ")
                .append("[HTTP METHOD:")
                .append(httpServletRequest.getMethod())
                .append("] [PATH INFO:")
                .append(httpServletRequest.getPathInfo())
                .append("] [REQUEST PARAMETERS:")
                .append(requestMap)
                .append("] [REMOTE ADDRESS:")
                .append(httpServletRequest.getRemoteAddr())
                .append("]");

        return logMessage.toString();
    }

    private Map<String, String> getTypesafeRequestMap(HttpServletRequest request) {
        Map<String, String> typesafeRequestMap = new HashMap<>();
        Enumeration<?> requestParamNames = request.getParameterNames();
        while (requestParamNames.hasMoreElements()) {
            String requestParamName = (String) requestParamNames.nextElement();
            String requestParamValue = request.getParameter(requestParamName);
            typesafeRequestMap.put(requestParamName, requestParamValue);
        }
        return typesafeRequestMap;
    }


    @Override
    public void destroy() {
    }

}