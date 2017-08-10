package org.hspconsortium.platform.api.service;

import org.hspconsortium.platform.api.controller.SandboxController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("default")
public class SandboxInitializer {

    @Value("${hspc.platform.api.sandbox.name}")
    private String sandboxName;

    @Autowired
    private SandboxController sandboxController;

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        // calling "get()" on an empty database will cause the schema to be initialized
        sandboxController.get();
    }
}
