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