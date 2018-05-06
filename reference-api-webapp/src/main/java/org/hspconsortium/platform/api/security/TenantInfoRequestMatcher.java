package org.hspconsortium.platform.api.security;

import org.hspconsortium.platform.api.fhir.model.Sandbox;
import org.hspconsortium.platform.api.fhir.service.SandboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TenantInfoRequestMatcher implements RequestMatcher {
    private static final Logger logger = LoggerFactory.getLogger(TenantInfoRequestMatcher.class);

    private String openContextPath;

    @Autowired
    @Lazy
    private SandboxService sandboxService;

    private List<String> openTeamIds = null;

    private Map<String, RequestMatcher> requestMatcherMap = null;

    public TenantInfoRequestMatcher() {
        logger.info("Configuring TenantInfoRequestMatcher");
    }

    public void reset() {
        openTeamIds = null;
        requestMatcherMap = null;
        logger.info("TenantInfoRequestMatcher reset");
    }

    public String getOpenContextPath() {
        return openContextPath;
    }

    public TenantInfoRequestMatcher setOpenContextPath(String openContextPath) {
        if (this.openContextPath != null) {
            if (!this.openContextPath.equals(openContextPath)) {
                throw new RuntimeException("Open Context Path cannot be changed once set");
            }
        }
        this.openContextPath = openContextPath;
        return this;
    }

    public boolean isInitialized() {
        return openTeamIds != null;
    }

    public void resetOpenTeamIds() {
        if (isInitialized()) {
            openTeamIds.clear();
            requestMatcherMap.clear();
        }
        openTeamIds = null;
        requestMatcherMap = null;
    }

    public void initOpenTeamIds() {
        resetOpenTeamIds();

        openTeamIds = new ArrayList<>();
        requestMatcherMap = new HashMap<>();
    }

    public void loadOpenTeamIds() {
        initOpenTeamIds();

        for (String teamId : findOpenTeamIds(sandboxService)) {
            addOpenTeamId(teamId);
        }
    }

    public List<String> getOpenTeamIds() {
        if (!isInitialized()) {
            initOpenTeamIds();
        }
        return openTeamIds;
    }

    private Collection<String> findOpenTeamIds(SandboxService sandboxService) {
        return sandboxService
                .all()
                .stream()
                .filter(teamId -> {
                    Sandbox sandbox = sandboxService.get(teamId);
                    return sandbox.isAllowOpenAccess();
                })
                .collect(Collectors.toList());
    }

    /**
     * Note, this should not load the full list, as the method to load the
     * full list will actually use this method.
     * Also, this method is used externally to add a single new entry to an
     * already loaded list
     */
    public RequestMatcher addOpenTeamId(String teamId) {
        if (!isInitialized()) {
            initOpenTeamIds();
        }
        if (requestMatcherMap.containsKey(teamId)) {
            return requestMatcherMap.get(teamId);
        } else {
            openTeamIds.add(teamId);
            RequestMatcher result = createAntPathRequestMatcher(teamId);
            requestMatcherMap.put(teamId, result);
            return result;
        }
    }

    private RequestMatcher createAntPathRequestMatcher(String teamId) {
        return new AntPathRequestMatcher("/" + teamId + "/" + openContextPath + "/**", null);
    }

    public void removeOpenTeamId(String teamId) {
        if (isInitialized()) {
            boolean removed = openTeamIds.remove(teamId);
            if (removed) {
                requestMatcherMap.remove(teamId);
            }
        }
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        logger.info("Evaluating TenantInfoRequestMatcher...");
        boolean result = false;
        if (openContextPath != null && openContextPath.length() > 0) {
            if (!isInitialized()) {
                loadOpenTeamIds();
            }
            // use Java 8 and run this in parallel
            result = requestMatcherMap.values()
                    .stream()
                    .anyMatch(r -> r.matches(request));
        }
        return result;
    }
}