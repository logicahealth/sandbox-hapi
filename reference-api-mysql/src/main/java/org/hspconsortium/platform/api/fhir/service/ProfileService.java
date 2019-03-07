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

package org.hspconsortium.platform.api.fhir.service;

import org.json.simple.JSONObject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.zip.ZipFile;

public interface ProfileService {
//    Future<HashMap<List<String>, List<String>>> saveZipFile (ZipFile zipFile, HttpServletRequest request, String sandboxId, String apiEndpoint, String fileId) throws IOException;
    HashMap<List<String>, List<String>> saveZipFile (ZipFile zipFile, HttpServletRequest request, String sandboxId, String apiEndpoint, String fileId) throws IOException;
    HashMap<String, List<JSONObject>> getAllUploadedProfiles(HttpServletRequest request, String sandboxId);
    HashMap<String, Boolean> getTaskRunning();
    void setTaskRunning(HashMap<String, Boolean> taskRunning);
}
