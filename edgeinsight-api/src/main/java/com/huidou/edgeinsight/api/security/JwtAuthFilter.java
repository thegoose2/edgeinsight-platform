package com.huidou.edgeinsight.api.security;

import com.huidou.edgeinsight.api.security.annotation.Anonymous;
import com.huidou.edgeinsight.api.security.config.SecurityProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

@Component
public class JwtAuthFilter implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityProperties securityProperties;

    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider, SecurityProperties securityProperties) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.securityProperties = securityProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBeanType().getName();
        String path = request.getRequestURI();
        String httpMethod = request.getMethod();

        // Check @Anonymous annotation on method or class
        if (handlerMethod.hasMethodAnnotation(Anonymous.class) ||
            handlerMethod.getBeanType().isAnnotationPresent(Anonymous.class)) {
            return true;
        }

        // Check public-paths whitelist
        if (isPublicPath(httpMethod, path)) {
            return true;
        }

        // Extract token from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":401,\"message\":\"Missing or invalid Authorization header\"}");
            return false;
        }

        String token = authHeader.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":401,\"message\":\"Invalid or expired token\"}");
            return false;
        }

        if (jwtTokenProvider.isTokenExpired(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":401,\"message\":\"Token expired\"}");
            return false;
        }

        // Parse token and set security context
        JwtTokenProvider.UserInfo userInfo = jwtTokenProvider.parseToken(token);
        request.setAttribute("userInfo", userInfo);
        SecurityContextHolder.getContext().setAuthentication(
            new JwtAuthenticationToken(userInfo)
        );

        return true;
    }

    private boolean isPublicPath(String httpMethod, String path) {
        if (securityProperties.getPublicPaths() == null) {
            return false;
        }
        for (String publicPath : securityProperties.getPublicPaths()) {
            String[] parts = publicPath.split(":", 2);
            if (parts.length == 2) {
                String method = parts[0];
                String pattern = parts[1];
                if (method.equalsIgnoreCase(httpMethod) && matchPath(pattern, path)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matchPath(String pattern, String path) {
        String regex = pattern.replace("**", ".*").replace("*", "[^/]*");
        return Pattern.matches(regex, path);
    }
}