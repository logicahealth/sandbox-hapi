package org.hspconsortium.platform.api.controller;

import org.hspconsortium.platform.api.fhir.DataSourceRepository;
import org.hspconsortium.platform.api.fhir.service.SandboxService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;

public class SystemControllerTest {

    @Autowired
    private MockMvc mvc;

    private SystemController systemController;
    private SandboxService sandboxService = mock(SandboxService.class);
    private DataSourceRepository dataSourceRepository = mock(DataSourceRepository.class);

    @Before
    public void setUp() {
        systemController = new SystemController();
    }

    @Test
    public void systemTest() {
        Assert.assertEquals(systemController.system(), "System endpoint");
    }

}
