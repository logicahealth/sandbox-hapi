package org.hspconsortium.platform.api.fhir.service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.ZipFile;

public interface SystemService {
    void saveZipFile (ZipFile zipFile, HttpServletRequest request, String sandboxId) throws IOException;
    HashMap<String, String> getAllUploadedProfiles(HttpServletRequest request, String sandboxId);
}
