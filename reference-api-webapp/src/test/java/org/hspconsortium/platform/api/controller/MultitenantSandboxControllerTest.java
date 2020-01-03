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
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class MultitenantSandboxControllerTest {

    private SandboxService sandboxService = mock(SandboxService.class);
    private HttpServletRequest request = mock(HttpServletRequest.class);

    private MultitenantSandboxController multitenantSandboxController = new MultitenantSandboxController(sandboxService);

    private Sandbox sandbox;
    private String teamId = "teamId";
    private String snapshotId = "snapshotId";

    @Before
    public void setUp() {
        sandbox = new Sandbox(teamId);
        when(sandboxService.verifyUser(request, teamId)).thenReturn(true);
    }

//    @Test
//    public void saveTest() throws Exception {
//        when(sandboxService.save(any(), any())).thenReturn(sandbox);
//        Sandbox returnedSandbox = multitenantSandboxController.save(request, teamId, sandbox, null);
//        assertEquals(sandbox, returnedSandbox);
//    }
//
//    @Test
//    public void saveTestDataSetNotNull() throws Exception {
//        when(sandboxService.save(any(), any())).thenReturn(sandbox);
//        Sandbox returnedSandbox = multitenantSandboxController.save(request, teamId, sandbox, DataSet.NONE);
//        assertEquals(sandbox, returnedSandbox);
//    }

    @Test
    public void cloneTest() {
        HashMap<String, Sandbox> sandboxHashMap = new HashMap<>();
        sandboxHashMap.put("newSandbox", sandbox);
        sandboxHashMap.put("clonedSandbox", sandbox);
        Sandbox returnedSandbox = multitenantSandboxController.clone(request, sandboxHashMap);
        verify(sandboxService).clone(sandbox, sandbox);
        assertEquals(sandbox, returnedSandbox);
    }

    @Test(expected = UnauthorizedUserException.class)
    public void cloneTestUserNotAuthorized() {
        when(sandboxService.verifyUser(request, teamId)).thenReturn(false);
        HashMap<String, Sandbox> sandboxHashMap = new HashMap<>();
        sandboxHashMap.put("newSandbox", sandbox);
        sandboxHashMap.put("clonedSandbox", sandbox);
        multitenantSandboxController.clone(request, sandboxHashMap);
    }

    @Test
    public void getTest() {
        when(sandboxService.get(teamId)).thenReturn(sandbox);
        Sandbox returnedSandbox = multitenantSandboxController.get(request, teamId);
        assertEquals(sandbox, returnedSandbox);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getTestDoesntExist() {
        when(sandboxService.get(teamId)).thenReturn(null);
        multitenantSandboxController.get(request, teamId);
    }

    @Test
    public void deleteTest() {
        multitenantSandboxController.delete(request, teamId);
        verify(sandboxService).remove(teamId);
    }

    @Test(expected = UnauthorizedUserException.class)
    public void deleteTestUserNotAuthorized() {
        when(sandboxService.verifyUser(request, teamId)).thenReturn(false);
        multitenantSandboxController.delete(request, teamId);
    }

    @Test
    public void resetTest() {
        String returnedString = multitenantSandboxController.reset(request, teamId, new ResetSandboxCommand());
        assertEquals("Success", returnedString);
        verify(sandboxService).reset(anyString(), any());
    }

    @Test(expected = UnauthorizedUserException.class)
    public void resetTestUserNotAuthorized() {
        when(sandboxService.verifyUser(request, teamId)).thenReturn(false);
        multitenantSandboxController.reset(request, teamId, new ResetSandboxCommand());
    }

    @Test
    public void getSnapshotsTest() {
        Set<String> stringSet = new HashSet<>();
        when(sandboxService.getSandboxSnapshots(teamId)).thenReturn(stringSet);
        Set<String> returnedStringSet = multitenantSandboxController.getSnapshots(teamId);
        assertEquals(stringSet, returnedStringSet);
    }

    @Test
    public void snapshotTestTake() {
        SnapshotSandboxCommand snapshotSandboxCommand = new SnapshotSandboxCommand();
        snapshotSandboxCommand.setAction(SnapshotSandboxCommand.Action.Take);
        when(sandboxService.takeSnapshot(teamId, snapshotId)).thenReturn("Success");
        String returnedString = multitenantSandboxController.snapshot(teamId, snapshotId, snapshotSandboxCommand);
        assertEquals("Success", returnedString);
    }

    @Test
    public void snapshotTestRestore() {
        SnapshotSandboxCommand snapshotSandboxCommand = new SnapshotSandboxCommand();
        snapshotSandboxCommand.setAction(SnapshotSandboxCommand.Action.Restore);
        when(sandboxService.restoreSnapshot(teamId, snapshotId)).thenReturn("Success");
        String returnedString = multitenantSandboxController.snapshot(teamId, snapshotId, snapshotSandboxCommand);
        assertEquals("Success", returnedString);
    }

    @Test
    public void snapshotTestDelete() {
        SnapshotSandboxCommand snapshotSandboxCommand = new SnapshotSandboxCommand();
        snapshotSandboxCommand.setAction(SnapshotSandboxCommand.Action.Delete);
        when(sandboxService.deleteSnapshot(teamId, snapshotId)).thenReturn("Success");
        String returnedString = multitenantSandboxController.snapshot(teamId, snapshotId, snapshotSandboxCommand);
        assertEquals("Success", returnedString);
    }

    @Test(expected = RuntimeException.class)
    public void snapshotTestError() {
        SnapshotSandboxCommand snapshotSandboxCommand = new SnapshotSandboxCommand();
        when(sandboxService.takeSnapshot(teamId, snapshotId)).thenReturn("Success");
        multitenantSandboxController.snapshot(teamId, snapshotId, snapshotSandboxCommand);
    }

    // Skipping "echo" endpoints because they seem unused.

}
