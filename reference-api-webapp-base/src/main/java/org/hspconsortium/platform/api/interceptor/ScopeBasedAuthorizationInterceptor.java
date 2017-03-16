package org.hspconsortium.platform.api.interceptor;

import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.method.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import org.hspconsortium.platform.api.authorization.ScopeBasedAuthorizationParams;
import org.hspconsortium.platform.api.authorization.SmartScope;
import org.hspconsortium.platform.api.oauth2.HspcOAuth2Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Component
public class ScopeBasedAuthorizationInterceptor extends InterceptorAdapter {

    public static final String LAUNCH_CONTEXT_PATIENT_PARAM_NAME = "patient";

    @Autowired
    private ScopeBasedAuthorizationParams scopeBasedAuthorizationParams;

    @Override
    public boolean incomingRequestPostProcessed(RequestDetails theRequestDetails, HttpServletRequest theRequest, HttpServletResponse theResponse) throws AuthenticationException {

        // Authorization filtering only applies to searching a particular type
        if (theRequestDetails.getRestOperationType() != RestOperationTypeEnum.SEARCH_TYPE)
            return true;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // if the user is not authenticated, we can't do any authorization
        if (authentication == null || !(authentication instanceof HspcOAuth2Authentication)) {
            return true;
        }

        HspcOAuth2Authentication hspcOAuth2Authentication = (HspcOAuth2Authentication) authentication;


        Set<SmartScope> smartScopes = getSmartScopes(hspcOAuth2Authentication);

        // we currently treat a user as if it has access to the entire system, so if a user scope exists we don't do
        // any further authorization filtering
        for (SmartScope smartScope : smartScopes) {
            if (smartScope.isUserScope())
                return true;
        }

        // finally, apply filtering for patient scoped queries
        for (SmartScope smartScope : smartScopes) {
            if (smartScope.isPatientScope()) {
                String patientId = hspcOAuth2Authentication.getLaunchContextParams().get(LAUNCH_CONTEXT_PATIENT_PARAM_NAME);
                filterToPatientScope(patientId, theRequestDetails);
                return true;
            }
        }

        return true;
    }


    private void filterToPatientScope(String patientId, RequestDetails requestDetails) {
        if (patientId == null) {
            throw new SecurityException("For patient scope, a launch_context parameter indicating the in-context" +
                    " patient is required, but none was found.");
        }

        String scopeParam = scopeBasedAuthorizationParams.getParamForResource(requestDetails.getResourceName());

        if (scopeParam == null) {
            // https://www.hl7.org/fhir/compartment-patient.html
            // if we get here, the resource being accessed is one described as "...never in [the patient] compartment"
            return;
        }

        Map<String, String[]> requestParams = requestDetails.getParameters();
        String[] existingScopeParamValue = requestParams.get(scopeParam);

        if (existingScopeParamValue == null) {
            // parameter doesn't exist with name 'scopeParam'
            requestParams.put(scopeParam, new String[]{patientId});
        } else if (!valueAlreadyInParameter(existingScopeParamValue, patientId)) {
            // parameter exists, but is different than the current patientId
            requestParams.put(scopeParam, addValueToStringArray(existingScopeParamValue, patientId));
        }
    }


    ////
    // private methods

    private String[] addValueToStringArray(String[] stringArray, String newValue) {
        String[] newArray = new String[stringArray.length + 1];

        for (int x = 0; x < newArray.length - 1; x++) {
            newArray[x] = stringArray[x];
        }

        newArray[newArray.length - 1] = newValue;

        return newArray;
    }

    private boolean valueAlreadyInParameter(String[] existingScopeParamValue, String valueToSearch) {
        for (String anExistingScopeParamValue : existingScopeParamValue) {
            if (valueToSearch.equals(anExistingScopeParamValue))
                return true;
        }
        return false;
    }

    /**
     * Scopes are stored as strings in the authentication object. Take those out and add them to the "SmartScope"
     * wrapper which adds some convenience methods for extracting meaning from the scope.
     */
    private Set<SmartScope> getSmartScopes(HspcOAuth2Authentication hspcOAuth2Authentication) {

        Set<SmartScope> scopes = new HashSet<>();

        for (String scope : hspcOAuth2Authentication.getOAuth2Request().getScope()) {
            scopes.add(new SmartScope(scope));
        }

        return scopes;
    }
}
