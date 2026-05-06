package com.huidou.edgeinsight.api.security;

import com.huidou.edgeinsight.api.security.annotation.Anonymous;
import com.huidou.edgeinsight.api.security.config.SecurityProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PublicEndpointRegistry {

    private final RequestMappingHandlerMapping handlerMapping;
    private final SecurityProperties securityProperties;
    private final Set<String> publicEndpoints = new HashSet<>();

    public PublicEndpointRegistry(@Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping, SecurityProperties securityProperties) {
        this.handlerMapping = handlerMapping;
        this.securityProperties = securityProperties;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo mappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();

            boolean isAnonymous = handlerMethod.hasMethodAnnotation(Anonymous.class)
                    || handlerMethod.getBeanType().isAnnotationPresent(Anonymous.class);

            if (isAnonymous) {
                Set<String> patterns = mappingInfo.getPatternsCondition() != null
                        ? mappingInfo.getPatternsCondition().getPatterns()
                        : new HashSet<>();
                Set<RequestMethod> requestMethods = mappingInfo.getMethodsCondition() != null
                        ? mappingInfo.getMethodsCondition().getMethods()
                        : new HashSet<>();

                for (String pattern : patterns) {
                    if (requestMethods.isEmpty()) {
                        publicEndpoints.add("ANY:" + pattern);
                    } else {
                        for (RequestMethod method : requestMethods) {
                            publicEndpoints.add(method.name() + ":" + pattern);
                        }
                    }
                }
            }
        }

        if (securityProperties.getPublicPaths() != null) {
            publicEndpoints.addAll(securityProperties.getPublicPaths());
        }
    }

    public boolean isPublic(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        String key = method + ":" + path;

        if (publicEndpoints.contains(key)) {
            return true;
        }

        if (publicEndpoints.contains("ANY:" + path)) {
            return true;
        }

        return publicEndpoints.stream().anyMatch(p -> {
            String[] parts = p.split(":", 2);
            if (parts.length != 2) return false;
            String pMethod = parts[0];
            String pPattern = parts[1];
            return ("ANY".equals(pMethod) || pMethod.equalsIgnoreCase(method)) && path.startsWith(pPattern);
        });
    }
}