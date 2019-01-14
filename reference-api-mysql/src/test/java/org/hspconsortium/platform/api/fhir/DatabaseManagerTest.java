package org.hspconsortium.platform.api.fhir;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

//import static org.mockito.Mockito.when;


public class DatabaseManagerTest {


    @Autowired
    private MockMvc mvc;
    DatabaseManager databaseManager = new DatabaseManager();

    @Before
    public void setup(){
        ReflectionTestUtils.setField(databaseManager, "username", "root");
        ReflectionTestUtils.setField(databaseManager, "password", "password");
    }

    @Test (expected = NullPointerException.class)
    public void testGetSchemasLikeThrowsExceptionIfConnectionIsNull(){
        databaseManager.getSchemasLike("123", "45");
    }

//    @Test //(expected = NullPointerException.class)
//    public void testGetSchemasLikeThrowsException(){
//        when(noSchemaDataSource.getConnection())
//        databaseManager.getSchemasLike("123", "45");
//    }
}
