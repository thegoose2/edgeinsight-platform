package com.huidou.edgeinsight.api.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;

public class SecurityContextHolder {

    private static final ThreadLocal<SecurityContext> contextHolder = new ThreadLocal<>();

    public static SecurityContext getContext() {
        SecurityContext ctx = contextHolder.get();
        if (ctx == null) {
            ctx = new SecurityContextImpl();
            contextHolder.set(ctx);
        }
        return ctx;
    }

    public static void setContext(SecurityContext context) {
        contextHolder.set(context);
    }

    public static void clearContext() {
        contextHolder.remove();
    }

    public static JwtTokenProvider.UserInfo getCurrentUser() {
        SecurityContext ctx = contextHolder.get();
        if (ctx != null && ctx.getAuthentication() instanceof JwtAuthenticationToken) {
            return ((JwtAuthenticationToken) ctx.getAuthentication()).getUserInfo();
        }
        return null;
    }
}