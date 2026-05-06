package com.huidou.edgeinsight.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huidou.edgeinsight.api.security.config.SecurityProperties;
import com.huidou.edgeinsight.common.dto.Result;
import io.jsonwebtoken.JwtException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthFilter implements Filter {

    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityProperties securityProperties;
    private final ObjectMapper objectMapper;

    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider, SecurityProperties securityProperties, ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.securityProperties = securityProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // 1. 检查路径白名单
        if (isPublicPath(path, method)) {
            chain.doFilter(request, response);
            return;
        }

        // 2. 检查 @Anonymous 注解
        if (isAnonymousAnnotated(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        // 3. 提取并验证 Token
        String authHeader = httpRequest.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null || token.isEmpty()) {
            sendUnauthorized(httpResponse, "Missing token");
            return;
        }

        try {
            if (!jwtTokenProvider.validateToken(token)) {
                sendUnauthorized(httpResponse, "Invalid or expired token");
                return;
            }

            // 4. 解析 Token 并写入 request attribute
            Map<String, Object> claims = jwtTokenProvider.parseClaims(token);
            httpRequest.setAttribute("userId", claims.get("userId"));
            httpRequest.setAttribute("username", claims.get("username"));
            httpRequest.setAttribute("realName", claims.get("realName"));
            httpRequest.setAttribute("roles", claims.get("roles"));
            httpRequest.setAttribute("permissions", claims.get("permissions"));

            chain.doFilter(request, response);

        } catch (JwtException e) {
            sendUnauthorized(httpResponse, "Invalid or expired token");
        }
    }

    private boolean isPublicPath(String path, String method) {
        List<String> publicPaths = securityProperties.getPublicPaths();
        if (publicPaths == null || publicPaths.isEmpty()) {
            return false;
        }
        String key = method + ":" + path;
        return publicPaths.stream().anyMatch(p -> {
            String[] parts = p.split(":", 2);
            if (parts.length != 2) return false;
            return parts[0].equalsIgnoreCase(method) && path.startsWith(parts[1]);
        });
    }

    private boolean isAnonymousAnnotated(HttpServletRequest request) throws ServletException {
        HandlerMapping handlerMapping = (HandlerMapping) request.getAttribute("org.springframework.web.servlet.HandlerMapping");
        if (handlerMapping == null) {
            return false;
        }
        try {
            HandlerExecutionChain handlerChain = handlerMapping.getHandler(request);
            if (handlerChain == null) {
                return false;
            }
            Object handler = handlerChain.getHandler();
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                // 检查方法上的 @Anonymous
                if (AnnotationUtils.findAnnotation(handlerMethod.getMethod(), com.huidou.edgeinsight.api.security.annotation.Anonymous.class) != null) {
                    return true;
                }
                // 检查类上的 @Anonymous
                if (AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), com.huidou.edgeinsight.api.security.annotation.Anonymous.class) != null) {
                    return true;
                }
            }
        } catch (Exception e) {
            // 忽略异常，继续处理
        }
        return false;
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Result<?> result = Result.unauthorized(message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
