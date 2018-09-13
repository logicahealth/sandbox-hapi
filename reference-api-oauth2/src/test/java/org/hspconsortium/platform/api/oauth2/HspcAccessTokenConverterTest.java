package org.hspconsortium.platform.api.oauth2;

import org.apache.commons.collections.map.HashedMap;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class HspcAccessTokenConverterTest {

    private HspcAccessTokenConverter hspcAccessTokenConverter = new HspcAccessTokenConverter();

    private String scopeString = "scope1 scope2";
    private String SCOPE = "scope";

    @Before
    public void setup() {
//        ReflectionTestUtils.setField(hspcAccessTokenConverter, "SCOPE", SCOPE);
    }

    @Test
    public void convertScopeStringToCollectionTest() {
        Map<String, String> scopeMap = new HashMap<>();
        scopeMap.put(SCOPE, scopeString);
        Map newScopeMap = new HashMap<>(scopeMap);
        Collection<String> scopeCollection = Arrays.asList(scopeString.split(" "));
        newScopeMap.put(SCOPE, scopeCollection);
        Map returnedMap = hspcAccessTokenConverter.convertScopeStringToCollection(scopeMap);
        assertEquals(newScopeMap.get(SCOPE).toString(), returnedMap.get(SCOPE).toString());
    }
}
