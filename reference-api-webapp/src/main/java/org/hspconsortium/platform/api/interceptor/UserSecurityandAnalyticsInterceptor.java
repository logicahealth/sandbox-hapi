package org.hspconsortium.platform.api.interceptor;

import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.api.server.ResponseDetails;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.hspconsortium.platform.api.security.TenantInfoRequestMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserSecurityandAnalyticsInterceptor extends InterceptorAdapter {

    @Value("${hspc.platform.api.sandboxManagerApi.url}")
    private String sandboxManagerApiUrl;

    @Value("${hspc.platform.api.sandboxManagerApi.transactionPath}")
    private String transactionPath;

    @Autowired
    private TenantInfoRequestMatcher tenantInfoRequestMatcher;

    @Override
    public boolean outgoingResponse(RequestDetails theRequestDetails, ResponseDetails theResponseDetails, HttpServletRequest theServletRequest, HttpServletResponse theServletResponse) throws AuthenticationException {
        theResponseDetails.getResponseCode();
        handleCallToSandboxAPI(theServletRequest);
        return true;
    }

    @Override
    public BaseServerResponseException preProcessOutgoingException(RequestDetails theRequestDetails, Throwable theException, HttpServletRequest theServletRequest) throws ServletException {
        theException.getMessage();
        handleCallToSandboxAPI(theServletRequest);
        return null;
    }

    private void handleCallToSandboxAPI(HttpServletRequest request) {
        checkIfUserIsAuthorized(request);

    }

    private void checkIfUserIsAuthorized(HttpServletRequest request) {
        String[] parameters = request.getRequestURI().split("/");
        String tenant = parameters[1];
        String secured = parameters[2];
        String endpointCall = parameters[3];
        if (secured.equals("open")) {
            checkIfOpenEndpointIsAllowed(tenant);
        } else if (endpointCall.equals("metadata")) {
            // do nothing, this will always be open
        } else {
            try {
                String authHeader = request.getHeader("Authorization");
                HttpClient httpclient = HttpClients.createDefault();
                HttpPost httppost = new HttpPost(sandboxManagerApiUrl + transactionPath + tenant);
                httppost.setHeader("Authorization", authHeader);
                HttpResponse returned = httpclient.execute(httppost);
                if (returned.getStatusLine().getStatusCode() == 401) {
                    throw new Exception();
                }
            } catch (Exception e) {
                throw new UnauthorizedUserException("User does not have privileges to this sandbox.");
            }
        }
    }

    private void checkIfOpenEndpointIsAllowed(String tenant) {
        Set<String> openTenants = tenantInfoRequestMatcher.getOpenTeamIds();
        if(!openTenants.contains(tenant)) {
            throw new RuntimeException("Sandbox " + tenant + " does not have an open endpoint.");
        }
    }

}
