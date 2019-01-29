package org.hspconsortium.platform.api.fhir.service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.zip.ZipFile;

public interface SystemService {
    void saveZipFile (ZipFile zipFile, HttpServletRequest request, String sandboxId) throws IOException;
}
