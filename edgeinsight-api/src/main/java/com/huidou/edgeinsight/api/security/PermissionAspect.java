package com.huidou.edgeinsight.api.security;

import com.huidou.edgeinsight.api.security.annotation.RequiresPermission;
import com.huidou.edgeinsight.common.exception.ForbiddenException;
import com.huidou.edgeinsight.common.exception.UnauthorizedException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class PermissionAspect {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Before("@annotation(com.huidou.edgeinsight.api.security.annotation.RequiresPermission)")
    public void checkPermission(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequiresPermission annotation = signature.getMethod().getAnnotation(RequiresPermission.class);

        if (annotation == null) {
            return;
        }

        String requiredPermission = annotation.value();

        JwtTokenProvider.JwtClaims principal = currentJwtClaims();
        if (principal == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        List<String> roles = principal.getRoles();
        if (roles != null && roles.contains(ROLE_ADMIN)) {
            return;
        }

        List<String> perms = principal.getPerms();
        if (perms == null || !perms.contains(requiredPermission)) {
            throw new ForbiddenException("无权访问此操作，需要权限: " + requiredPermission);
        }
    }

    private JwtTokenProvider.JwtClaims currentJwtClaims() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object p = auth.getPrincipal();
        return p instanceof JwtTokenProvider.JwtClaims ? (JwtTokenProvider.JwtClaims) p : null;
    }
}
