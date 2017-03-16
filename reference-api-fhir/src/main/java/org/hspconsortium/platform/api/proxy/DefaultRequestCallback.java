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