package org.hspconsortium.platform.api.fhir.service;

import org.json.simple.JSONObject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipFile;

public interface ProfileService {
    void saveZipFile (ZipFile zipFile, HttpServletRequest request, String sandboxId) throws IOException;
    HashMap<String, HashMap<String, String>> getAllUploadedProfilesOriginal(HttpServletRequest request, String sandboxId);
    HashMap<String, List<JSONObject>> getAllUploadedProfiles(HttpServletRequest request, String sandboxId);
}
