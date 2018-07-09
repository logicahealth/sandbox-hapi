package org.hspconsortium.platform.api.oauth2;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HspcAccessTokenConverter extends DefaultAccessTokenConverter {

    @Override
    @SuppressWarnings("unchecked")
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        Map<String, ?> newMap = (Map<String, ?>) (HspcAccessTokenConverter.convertScopeStringToCollection(map));

        OAuth2Authentication oAuth2Authentication = super.extractAuthentication(newMap);

        HspcOAuth2Authentication hspcOAuth2Authentication = new HspcOAuth2Authentication(oAuth2Authentication.getOAuth2Request(), oAuth2Authentication.getUserAuthentication());
        hspcOAuth2Authentication.setLaunchContextParams(extractLaunchContextParams(map));
        hspcOAuth2Authentication.setUserId((String) newMap.get("sub"));

        return hspcOAuth2Authentication;
    }

    private Map<String, String> extractLaunchContextParams(Map<String, ?> map) {
        Object launchContext = map.get("launch_context");
        if (launchContext != null && String.class.isInstance(launchContext)) {
            String launchContextString = (String) launchContext;
            Collection<String> launchContextCollection = Arrays.asList(launchContextString.split(","));

            Map<String, String> launchContextParams = new HashMap<>();

            for (String curParam : launchContextCollection) {
                int splitIndex = curParam.indexOf("=");

                String key = curParam.substring(0, splitIndex);
                String value = curParam.substring(splitIndex + 1);

                launchContextParams.put(key, value);
            }

            return launchContextParams;
        }

        return null;
    }


    @SuppressWarnings("unchecked")
    public static Map convertScopeStringToCollection(Map map) {
        Object scopeObj = map.get(SCOPE);
        if (scopeObj != null && scopeObj instanceof String) {
            Map newMap = new HashMap<>(map);
            String scopeStr = (String) scopeObj;
            Collection<String> scopeCollection = Arrays.asList(scopeStr.split(" "));
            newMap.put(SCOPE, scopeCollection);
            return newMap;
        } else {
            return map;
        }
    }
}
