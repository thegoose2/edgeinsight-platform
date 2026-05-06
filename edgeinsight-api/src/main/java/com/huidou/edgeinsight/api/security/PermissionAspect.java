package com.huidou.edgeinsight.api.security;

import com.huidou.edgeinsight.api.security.annotation.RequiresPermission;
import com.huidou.edgeinsight.common.exception.ForbiddenException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            throw new ForbiddenException("无法获取请求上下文");
        }

        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) request.getAttribute("permissions");

        if (permissions == null || !permissions.contains(requiredPermission)) {
            throw new ForbiddenException("无权访问此操作，需要权限: " + requiredPermission);
        }
    }

    private HttpServletRequest getCurrentRequest() {
        org.springframework.web.context.request.RequestAttributes attributes =
                org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        if (attributes instanceof org.springframework.web.context.request.ServletRequestAttributes) {
            return ((org.springframework.web.context.request.ServletRequestAttributes) attributes).getRequest();
        }
        return null;
    }
}
