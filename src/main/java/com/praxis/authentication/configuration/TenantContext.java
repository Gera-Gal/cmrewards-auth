package com.praxis.authentication.configuration;

public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_CLIENT = new ThreadLocal<>();

    public static void setTenant(String tenant) {
        CURRENT_TENANT.set(tenant);
    }

    public static String getTenant() {
        return CURRENT_TENANT.get();
    }

    public static void setClientId(String clientId) {
        CURRENT_CLIENT.set(clientId);
    }

    public static String getClientId() {
        return CURRENT_CLIENT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
        CURRENT_CLIENT.remove();
    }
}