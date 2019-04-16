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

import org.hspconsortium.platform.api.fhir.model.FhirProfile;
import org.hspconsortium.platform.api.fhir.model.ProfileTask;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipFile;

public interface ProfileService {
    void saveZipFile (ZipFile zipFile, HttpServletRequest request, String sandboxId, String apiEndpoint, String id, String profileName, String profileId) throws IOException;
    ProfileTask getTaskRunning(String id);
    HashMap<String, ProfileTask> getIdProfileTask();
    void saveTGZfile (MultipartFile file, HttpServletRequest request, String sandboxId, String apiEndpoint, String id, String profileName, String profileId) throws IOException;
    void saveProfileToSandman (List<FhirProfile> fhirProfile, HttpServletRequest request);
}
