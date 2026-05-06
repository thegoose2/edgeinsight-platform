package com.huidou.edgeinsight.api.security;

import com.huidou.edgeinsight.api.security.annotation.RequiresPermission;
import com.huidou.edgeinsight.common.exception.ForbiddenException;
import com.huidou.edgeinsight.common.exception.UnauthorizedException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {

    @Before("@annotation(com.huidou.edgeinsight.api.security.annotation.RequiresPermission)")
    public void checkPermission(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequiresPermission annotation = signature.getMethod().getAnnotation(RequiresPermission.class);

        if (annotation == null) {
            return;
        }

        String requiredPermission = annotation.value();

        JwtPrincipal principal = SecurityUtils.getCurrentPrincipal();
        if (principal == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        if (principal.getPerms() == null || !principal.getPerms().contains(requiredPermission)) {
            throw new ForbiddenException("无权访问此操作，需要权限: " + requiredPermission);
        }
    }
}