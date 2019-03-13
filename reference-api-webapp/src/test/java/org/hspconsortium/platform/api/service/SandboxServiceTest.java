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


public class SandboxServiceTest {

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
    public void saveTest() throws Exception{
        DataSet dt = DataSet.DEFAULT;
        when(sandbox.getTeamId()).thenReturn("123");
        when(sandboxPersister.findSandbox(sandbox.getTeamId())).thenReturn(sandbox);
        when(sandboxPersister.saveSandbox(sandbox)).thenReturn(sandbox);
        Sandbox result = sandboxService.save(sandbox, dt);
        verify(tenantInfoRequestMatcher).removeOpenTeamId(any());
        Assert.assertNotNull(result);
    }

    @Test
    public void saveTestIfExistingIsNull() throws Exception{
        DataSet dt = DataSet.DEFAULT;
        when(sandbox.getTeamId()).thenReturn("123");
        when(sandboxPersister.findSandbox(sandbox.getTeamId())).thenReturn(null);
        when(sandboxPersister.saveSandbox(sandbox)).thenReturn(sandbox);
        when(sandboxPersister.isTeamIdUnique(sandbox.getTeamId())).thenReturn(true);
        Sandbox result = sandboxService.save(sandbox, dt);
        verify(sandboxPersister).loadInitialDataset(any(), any());
        verify(tenantInfoRequestMatcher).removeOpenTeamId(any());
        Assert.assertNotNull(result);
    }

    @Test (expected = RuntimeException.class)
    public void testSaveThrowsExceptionTeamIdNotUnique() throws Exception{
        DataSet dataSet = DataSet.DEFAULT;
        when(sandbox.getTeamId()).thenReturn("123");
        when(sandboxPersister.findSandbox(sandbox.getTeamId())).thenReturn(sandbox);
        sandboxService.save(sandbox, dataSet);
    }

    @Test (expected = NullPointerException.class)
    public void testSaveThrowsExceptionTeamIdIsNull() throws Exception{
        DataSet dataSet = DataSet.DEFAULT;
        when(sandbox.getTeamId()).thenReturn(null);
        when(sandboxPersister.findSandbox(sandbox.getTeamId())).thenReturn(sandbox);
        sandboxService.save(sandbox, dataSet);
    }

    @Test (expected = NullPointerException.class)
    public void testSaveThrowsExceptionDSIsNull() throws Exception{
        when(sandbox.getTeamId()).thenReturn("123");
        when(sandboxPersister.findSandbox(sandbox.getTeamId())).thenReturn(sandbox);
        sandboxService.save(sandbox, null);
    }

    @Test (expected = RuntimeException.class)
    public void testCloneThrowsExceptionIfTeamIdNotUnique() throws Exception{
        when(sandbox.getTeamId()).thenReturn("12");
        Sandbox clonedSandbox = mock(Sandbox.class);
        when(clonedSandbox.getTeamId()).thenReturn("123");
        when(sandboxPersister.findSandbox(sandbox.getTeamId())).thenReturn(null);
        sandboxService.clone(sandbox, clonedSandbox);
    }

    @Test
    public void testCloneWhenTeamIdAndExistsAreNull() throws Exception{
        when(sandbox.getTeamId()).thenReturn("12");
        Sandbox clonedSandbox = mock(Sandbox.class);
        when(clonedSandbox.getTeamId()).thenReturn("123");
        when(sandboxPersister.findSandbox(sandbox.getTeamId())).thenReturn(null);
        when(sandboxPersister.isTeamIdUnique(sandbox.getTeamId())).thenReturn(true);
        sandboxService.clone(sandbox, clonedSandbox);
        verify(tenantInfoRequestMatcher).removeOpenTeamId(any());
    }

    @Test
    public void testCloneWhenTeamIdAndExistAreNullAndAllowOpenAccessIsTrue() throws Exception{
        when(sandbox.getTeamId()).thenReturn("12");
        Sandbox clonedSandbox = mock(Sandbox.class);
        when(clonedSandbox.getTeamId()).thenReturn("123");
        when(sandboxPersister.findSandbox(sandbox.getTeamId())).thenReturn(null);
        when(sandboxPersister.isTeamIdUnique(sandbox.getTeamId())).thenReturn(true);
        when(sandbox.isAllowOpenAccess()).thenReturn(true);
        sandboxService.clone(sandbox, clonedSandbox);
        verify(tenantInfoRequestMatcher).addOpenTeamId(any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCloneThrowsExceptionWhenExistingIsNotNull() throws Exception{
        when(sandbox.getTeamId()).thenReturn("12");
        Sandbox clonedSandbox = mock(Sandbox.class);
        when(clonedSandbox.getTeamId()).thenReturn("123");
        when(sandboxPersister.findSandbox(sandbox.getTeamId())).thenReturn(sandbox);
        sandboxService.clone(sandbox, clonedSandbox);
        verify(tenantInfoRequestMatcher).removeOpenTeamId(any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCloneThrowsExceptionWhenExistingIsNotNullAndAllowOpenAccessIsTrue() throws Exception{
        when(sandbox.getTeamId()).thenReturn("12");
        Sandbox clonedSandbox = mock(Sandbox.class);
        when(clonedSandbox.getTeamId()).thenReturn("123");
        when(sandboxPersister.findSandbox(sandbox.getTeamId())).thenReturn(sandbox);
        when(sandbox.isAllowOpenAccess()).thenReturn(true);
        sandboxService.clone(sandbox, clonedSandbox);
        verify(tenantInfoRequestMatcher).addOpenTeamId(any());
    }

    @Test (expected = NullPointerException.class)
    public void testCloneThrowsExceptionWhenSandboxIsNull(){
        when(sandbox.getTeamId()).thenReturn("12");
        Sandbox clonedSandbox = mock(Sandbox.class);
        when(clonedSandbox.getTeamId()).thenReturn("123");
        sandboxService.clone(null, clonedSandbox);;
    }

    @Test (expected = NullPointerException.class)
    public void testCloneThrowsExceptionWhenSandboxTeamIdIsNull(){
        when(sandbox.getTeamId()).thenReturn(null);
        Sandbox clonedSandbox = mock(Sandbox.class);
        when(clonedSandbox.getTeamId()).thenReturn("123");
        sandboxService.clone(sandbox, clonedSandbox);;
    }

    @Test (expected = NullPointerException.class)
    public void testCloneThrowsExceptionWhenClonedSandboxIsNull() {
        when(sandbox.getTeamId()).thenReturn("12");
        Sandbox clonedSandbox = mock(Sandbox.class);
        when(clonedSandbox.getTeamId()).thenReturn("123");
        sandboxService.clone(sandbox, null);;
    }

    @Test (expected = NullPointerException.class)
    public void testCloneThrowsExceptionWhenClonedSandboxTeamIdIsNull(){
        when(sandbox.getTeamId()).thenReturn("12");
        Sandbox clonedSandbox = mock(Sandbox.class);
        when(clonedSandbox.getTeamId()).thenReturn(null);
        sandboxService.clone(sandbox, clonedSandbox);;
    }

    @Test
    public void getTest() throws Exception{
        when(sandboxPersister.findSandbox("1")).thenReturn(sandbox);
        Sandbox result = sandboxService.get("1");
        Assert.assertEquals(result, sandbox);
    }

    @Test
    public void removeTest() {
        when(sandboxService.get("1")).thenReturn(sandbox);
        boolean result = sandboxService.remove("1");
        verify(tenantInfoRequestMatcher).removeOpenTeamId(any());
        Assert.assertFalse(result);
    }

    @Test
    public void testRemoveExistingIsNull() {
        when(sandboxService.get("1")).thenReturn(null);
        boolean result = sandboxService.remove("1");
        Assert.assertTrue(result);
    }

    @Test (expected = RuntimeException.class)
    public void testResetThrowsExceptionIfExistingIsNull(){
        Sandbox result = sandboxService.reset("1", null);
    }

    @Test (expected = RuntimeException.class)
    public void testResetThrowsExceptionWhenExistingCannotBeDeleted() {
        DataSet dataSet = DataSet.DEFAULT;
        when(sandboxService.get("1")).thenReturn(sandbox);
        Sandbox result = sandboxService.reset("1", dataSet);
    }

    @Test
    public void testReset() {
        DataSet dataSet = DataSet.DEFAULT;
        when(sandboxService.get("1")).thenReturn(sandbox);
        when(sandboxPersister.removeSandbox(sandbox.getSchemaVersion(), sandbox.getTeamId())).thenReturn(true);
        when(sandboxPersister.saveSandbox(any())).thenReturn(sandbox);
        Sandbox result = sandboxService.reset("1", dataSet);
        Assert.assertNotNull(result);
    }

    @Test (expected = RuntimeException.class)
    public void testGetSandboxSnapshotsThrowsExceptionIfExistingIsNull(){
        when(sandboxService.get("123")).thenReturn(null);
        sandboxService.getSandboxSnapshots("123");
    }

    @Test
    public void testGetSandboxSnapshots(){
        when(sandboxService.get("123")).thenReturn(sandbox);
        when(sandboxPersister.getSnapshots(any())).thenReturn(anySet());
        Set<String> result = sandboxService.getSandboxSnapshots("123");
        Assert.assertNotNull(result);
    }

    @Test (expected = RuntimeException.class)
    public void testTakeSnapshotThrowsExceptionIfExistingIsNull(){
        when(sandboxService.get("123")).thenReturn(null);
        sandboxService.takeSnapshot("123", "12");
    }

    @Test
    public void testTakeSnapshot(){
        when(sandboxService.get("123")).thenReturn(sandbox);
        String result = sandboxService.takeSnapshot("123", "12");
        Assert.assertNull(result);
    }

    @Test (expected = RuntimeException.class)
    public void testRestoreSnapshotThrowsExceptionIfExistingIsNull(){
        when(sandboxService.get("123")).thenReturn(null);
        sandboxService.restoreSnapshot("123", "12");
    }

    @Test (expected = RuntimeException.class)
    public void testRestoreSnapshotThrowsExceptionIfTeamIdIsNull(){
        sandboxService.restoreSnapshot(null, "12");
    }

    @Test
    public void testRestoreSnapshot(){
        when(sandboxService.get("123")).thenReturn(sandbox);
        String result = sandboxService.restoreSnapshot("123", "12");
    }

    @Test
    public void testDeleteSnapshotWhenExistingIsNull(){
        when(sandboxService.get("123")).thenReturn(null);
        String result = sandboxService.deleteSnapshot("123", "12");
        Assert.assertNull(result);
    }

    @Test
    public void testDeleteSnapshotThrowsExceptionOnDelete(){
        when(sandboxService.get("123")).thenReturn(sandbox);
        when(sandboxPersister.deleteSnapshot(sandbox, "12")).thenThrow(RuntimeException.class);
        String result = sandboxService.deleteSnapshot("123", "12");
        Assert.assertNull(result);
    }

    @Test
    public void testDeleteSnapshot(){
        when(sandboxService.get("123")).thenReturn(sandbox);
        when(sandboxPersister.deleteSnapshot(sandbox, "12")).thenReturn(any());
        String result = sandboxService.deleteSnapshot("123", "12");
        Assert.assertNull(result);
    }

    @Test
    public void testVerifyUserIfAuthTokenIsNull(){
        request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);
        boolean result = sandboxService.verifyUser(request, "12");
        Assert.assertFalse(result);
    }

    @Test (expected = NullPointerException.class)
    public void testVerifyUserThrowsExceptionIfRequestIsNull(){
        boolean result = sandboxService.verifyUser(null, "12");
        Assert.assertFalse(result);
    }

    @Test
    public void testVerifyUserIfResponseThrowsException(){
        request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(any());
        when(restTemplate.exchange(any(), any(), any(), String.class)).thenThrow(HttpClientErrorException.class);
        boolean result = sandboxService.verifyUser(request, "12");
        Assert.assertFalse(result);
    }

}
