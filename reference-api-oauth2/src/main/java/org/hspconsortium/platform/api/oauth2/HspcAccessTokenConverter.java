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

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import java.util.*;

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

            for (int i = 0; i < launchContextCollection.size(); i++) {
                String curParam = ((List<String>) launchContextCollection).get(i);
                int splitIndex = curParam.indexOf("=");
                if (splitIndex == -1) {
                    int pastSplitIndex = ((List<String>) launchContextCollection).get(i-1).indexOf("=");
                    String pastKey = ((List<String>) launchContextCollection).get(i-1).substring(0, pastSplitIndex);
                    String pastValue = ((List<String>) launchContextCollection).get(i-1).substring(pastSplitIndex + 1);
                    String newValue = pastValue + "," + curParam;
                    launchContextParams.put(pastKey, newValue);
                } else {
                    String key = curParam.substring(0, splitIndex);
                    String value = curParam.substring(splitIndex + 1);
                    launchContextParams.put(key, value);
                }
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
