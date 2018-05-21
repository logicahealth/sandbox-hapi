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

