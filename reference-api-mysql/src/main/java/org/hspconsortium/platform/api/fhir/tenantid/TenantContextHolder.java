package org.hspconsortium.platform.api.fhir.tenantid;

import org.springframework.util.Assert;


public class TenantContextHolder {
    private static final InheritableThreadLocal<String> contextHolder = new InheritableThreadLocal<>();

    public static void setTenant(String tenant) {
        Assert.notNull(tenant, "Tenant cannot be null");
        contextHolder.set(tenant);
    }

    public static String getTenant() {
        return contextHolder.get();
    }

    public static void clearTenant() {
        contextHolder.remove();
    }
}
