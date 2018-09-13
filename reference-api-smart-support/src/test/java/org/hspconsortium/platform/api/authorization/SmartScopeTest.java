package org.hspconsortium.platform.api.authorization;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SmartScopeTest {

    private String scope = "patient/*.read";

    SmartScope smartScope = new SmartScope(scope);

    @Before
    public void setup() {

    }

    @Test
    public void isPatientScopeTest() {
        Boolean returnedBool = smartScope.isPatientScope();
        assertEquals(true, returnedBool);
    }

    @Test
    public void isPatientScopeTestReturnsFalse() {
        scope = "user/*.*";
        SmartScope smartScope = new SmartScope(scope);
        Boolean returnedBool = smartScope.isPatientScope();
        assertEquals(false, returnedBool);
    }

    @Test
    public void isUserScopeTest() {
        scope = "user/*.*";
        SmartScope smartScope = new SmartScope(scope);
        Boolean returnedBool = smartScope.isUserScope();
        assertEquals(true, returnedBool);
    }

    @Test
    public void isUserScopeTestReturnsFalse() {
        scope = "patient/*.read";
        SmartScope smartScope = new SmartScope(scope);
        Boolean returnedBool = smartScope.isUserScope();
        assertEquals(false, returnedBool);
    }

}
