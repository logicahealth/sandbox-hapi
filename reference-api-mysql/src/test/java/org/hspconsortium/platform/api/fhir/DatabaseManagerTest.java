/**
 *  * #%L
 *  *
 *  * %%
 *  * Copyright (C) 2014-2020 Healthcare Services Platform Consortium
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
