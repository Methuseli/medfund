package com.medfund.shared.tenant;

import reactor.util.context.Context;
import reactor.util.context.ContextView;

/**
 * Reactive tenant context — stores tenant ID in Project Reactor context (not ThreadLocal).
 * Used by TenantWebFilter and TenantAwareConnectionFactory.
 */
public final class TenantContext {

    public static final String KEY = "TENANT_ID";

    private TenantContext() {}

    public static Context put(Context ctx, String tenantId) {
        return ctx.put(KEY, tenantId);
    }

    public static String get(ContextView ctx) {
        return ctx.getOrDefault(KEY, null);
    }
}
