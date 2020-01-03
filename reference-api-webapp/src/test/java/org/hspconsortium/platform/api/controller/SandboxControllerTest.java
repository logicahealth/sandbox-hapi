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

package org.hspconsortium.platform.api.controller;

import org.hspconsortium.platform.api.fhir.model.DataSet;
import org.hspconsortium.platform.api.fhir.model.ResetSandboxCommand;
import org.hspconsortium.platform.api.fhir.model.Sandbox;
import org.hspconsortium.platform.api.fhir.model.SnapshotSandboxCommand;
import org.hspconsortium.platform.api.fhir.service.SandboxService;
import org.hspconsortium.platform.api.persister.SandboxPersister;
import org.hspconsortium.platform.api.security.TenantInfoRequestMatcher;
import org.hspconsortium.platform.api.service.SandboxServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SandboxControllerTest {

    @Autowired
    private MockMvc mvc;

    private String sandboxName;
    @MockBean
    private SandboxService sandboxService = mock(SandboxServiceImpl.class);
    private Sandbox sandbox;  //= mock(Sandbox.class);
    private HttpServletRequest request = mock(javax.servlet.http.HttpServletRequest .class);
    private HttpServletResponse response = mock(HttpServletResponse.class);
    private SandboxController sandboxController;
    private SandboxPersister sandboxPersister = mock(SandboxPersister.class);
    private TenantInfoRequestMatcher tenantInfoRequestMatcher = mock(TenantInfoRequestMatcher.class);
    private RestTemplate restTemplate = mock(RestTemplate.class);

    @Before
    public void setUp() {
        sandboxName = "Test Sandbox";
        sandboxController = new SandboxController(sandboxService);
        sandbox = mock(Sandbox.class);
    }

    @Test
    public void saveTest() throws Exception{

        DataSet dataset = DataSet.DEFAULT;
        when(sandbox.getTeamId()).thenReturn("123");
        when(sandboxPersister.findSandbox(sandbox.getTeamId())).thenReturn(sandbox);
        when(sandbox.isAllowOpenAccess()).thenReturn(true);
        when(tenantInfoRequestMatcher.addOpenTeamId("123")).thenReturn(null);
        when(sandboxService.save(sandbox, dataset)).thenReturn(sandbox);
        Sandbox sandboxResult = sandboxController.save(sandbox, dataset);
        Assert.assertNotNull(sandboxResult);
    }

    @Test(expected = NullPointerException.class)
    public void testSaveThrowsExceptionIfSandboxIsNull(){
        DataSet dataset = DataSet.DEFAULT;
        when(sandbox.getTeamId()).thenReturn("123");
        sandboxController.save(null, dataset);
    }

    @Test(expected = NullPointerException.class)
    public void testSaveThrowsExceptionIfTeamIdIsNull(){
        DataSet dataset = DataSet.DEFAULT;
        when(sandbox.getTeamId()).thenReturn(null);
        sandboxController.save(sandbox, dataset);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetThrowsExceptionIfSandboxIsNul(){
        when(sandboxService.get(sandboxName)).thenReturn(null);
        sandboxController.get();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGet(){
        when(sandboxService.get(any())).thenReturn(any());
        Sandbox sandboxResult = sandboxController.get();
        Assert.assertNotNull(sandboxResult);
    }

    @Test
    public void resetTest() {
        ResetSandboxCommand resetSandboxCommand = mock(ResetSandboxCommand.class);
        String result = sandboxController.reset(resetSandboxCommand);
        assertEquals(result, "Success");
    }

    @Test(expected = NullPointerException.class)
    public void testSnapshotThrowsExceptionIfSnapshotIdIsNull() {
        SnapshotSandboxCommand snapshotSandboxCommand = mock(SnapshotSandboxCommand.class);
        sandboxController.snapshot( null,snapshotSandboxCommand);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSnapshotThrowsExceptionIfSnapshotIdIsLongerThan20() {
        SnapshotSandboxCommand snapshotSandboxCommand = mock(SnapshotSandboxCommand.class);
        sandboxController.snapshot( "1234567890123456789012",snapshotSandboxCommand);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSnapshotThrowsExceptionIfSnapshotIdHasSpecialCharacters() {
        SnapshotSandboxCommand snapshotSandboxCommand = mock(SnapshotSandboxCommand.class);
        sandboxController.snapshot( "123456789@^",snapshotSandboxCommand);
    }

    @Test(expected = NullPointerException.class)
    public void testSnapshotThrowsExceptionIfSnapshotSandboxCommandIsNull() {
        sandboxController.snapshot( "1", null);
    }

    @Test(expected = NullPointerException.class)
    public void testSnapshotThrowsExceptionIfSnapshotSandboxCommandGetActionIsNull() {
        SnapshotSandboxCommand snapshotSandboxCommand = mock(SnapshotSandboxCommand.class);
        when(snapshotSandboxCommand.getAction()).thenReturn(null);
        sandboxController.snapshot( "1",snapshotSandboxCommand);
    }

    @Test(expected = RuntimeException.class)
    public void testSnapshotThrowsExceptionIfSnapshotSandboxCommandGetActionIsUnknown() {
        SnapshotSandboxCommand snapshotSandboxCommand = mock(SnapshotSandboxCommand.class);
        when(snapshotSandboxCommand.getAction()).thenReturn(any());
        sandboxController.snapshot( "1",snapshotSandboxCommand);
    }
    @Test
    public void testSnapshotActionTake(){
        SnapshotSandboxCommand snapshotSandboxCommand = mock(SnapshotSandboxCommand.class);
        SnapshotSandboxCommand.Action act = SnapshotSandboxCommand.Action.Take;
        when(snapshotSandboxCommand.getAction()).thenReturn(act);
        when(sandbox.getTeamId()).thenReturn("1");
        when(sandboxService.takeSnapshot(any(), any())).thenReturn("123");
        String result = sandboxController.snapshot( "1",snapshotSandboxCommand);
        Assert.assertEquals(result, "123");
    }

    @Test
    public void snapshotActionRestoreTest(){
        SnapshotSandboxCommand snapshotSandboxCommand = mock(SnapshotSandboxCommand.class);
        SnapshotSandboxCommand.Action act = SnapshotSandboxCommand.Action.Restore;
        when(snapshotSandboxCommand.getAction()).thenReturn(act);
        when(sandbox.getTeamId()).thenReturn("1");
        when(sandboxService.restoreSnapshot(any(), any())).thenReturn("123");
        String result = sandboxController.snapshot( "1",snapshotSandboxCommand);
        Assert.assertEquals(result, "123");
    }

    @Test
    public void snapshotActionDeleteTestIfTeamIdIsNull(){
        SnapshotSandboxCommand snapshotSandboxCommand = mock(SnapshotSandboxCommand.class);
        SnapshotSandboxCommand.Action act = SnapshotSandboxCommand.Action.Delete;
        when(snapshotSandboxCommand.getAction()).thenReturn(act);
        when(sandbox.getTeamId()).thenReturn("1");
        String result = sandboxController.snapshot( "1",snapshotSandboxCommand);
        Assert.assertNull(result);
    }

    @Test(expected = RuntimeException.class)
    public void snapshotActionDeleteTest(){
        SnapshotSandboxCommand snapshotSandboxCommand = mock(SnapshotSandboxCommand.class);
        SnapshotSandboxCommand.Action act = SnapshotSandboxCommand.Action.Delete;
        when(snapshotSandboxCommand.getAction()).thenReturn(act);
        when(sandbox.getTeamId()).thenReturn("1");
        when(sandboxService.deleteSnapshot(any(), any())).thenThrow(RuntimeException.class);
        String result = sandboxController.snapshot( "1",snapshotSandboxCommand);
    }
}
