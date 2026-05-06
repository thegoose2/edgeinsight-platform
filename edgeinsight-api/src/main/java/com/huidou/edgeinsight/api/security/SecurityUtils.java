package com.huidou.edgeinsight.api.security;

import com.huidou.edgeinsight.common.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static JwtPrincipal getCurrentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        if (auth.getPrincipal() instanceof JwtPrincipal) {
            return (JwtPrincipal) auth.getPrincipal();
        }
        throw new UnauthorizedException("User not authenticated");
    }
}