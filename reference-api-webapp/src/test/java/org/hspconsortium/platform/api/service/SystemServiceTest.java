package org.hspconsortium.platform.api.service;

import org.hspconsortium.platform.api.fhir.model.DataSet;
import org.hspconsortium.platform.api.fhir.model.Sandbox;
import org.hspconsortium.platform.api.persister.SandboxPersister;
import org.hspconsortium.platform.api.security.TenantInfoRequestMatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

import static org.mockito.Mockito.*;

public class SystemServiceTest {
    private SandboxPersister sandboxPersister = mock(SandboxPersister.class);
    private TenantInfoRequestMatcher tenantInfoRequestMatcher = mock(TenantInfoRequestMatcher.class);
    private RestTemplate restTemplate = mock(RestTemplate.class);
    private SandboxServiceImpl sandboxService;
    private Sandbox sandbox;
    private HttpServletRequest request;

    @Before
    public void setup() {
        sandboxService = new SandboxServiceImpl(sandboxPersister, tenantInfoRequestMatcher, restTemplate);
        sandbox = mock(Sandbox.class);
        sandbox.setTeamId("1");
    }
    @Test
    public void saveProfileTest () {

    }
}
