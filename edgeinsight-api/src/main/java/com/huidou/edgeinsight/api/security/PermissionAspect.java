package com.huidou.edgeinsight.api.security;

import com.huidou.edgeinsight.api.security.annotation.RequiresPermission;
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
        if (annotation != null) {
            String requiredPermission = annotation.value();
        }
    }
}
