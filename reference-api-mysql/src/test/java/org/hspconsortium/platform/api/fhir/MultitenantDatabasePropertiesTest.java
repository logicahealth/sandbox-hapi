package org.hspconsortium.platform.api.fhir;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;

public class MultitenantDatabasePropertiesTest {

    MultitenantDatabaseProperties multitenantDatabaseProperties = new MultitenantDatabaseProperties();

    @Before
    public void setup() {
        ReflectionTestUtils.setField(multitenantDatabaseProperties, "dataSourceCacheSize", 10);
    }

    @Test
    public void getDataSourceCacheSizeTest() {
        Integer size = multitenantDatabaseProperties.getDataSourceCacheSize();
        assertEquals((Integer) 10, size);
    }

}
