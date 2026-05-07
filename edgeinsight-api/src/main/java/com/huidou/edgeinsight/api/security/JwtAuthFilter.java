package com.huidou.edgeinsight.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huidou.edgeinsight.api.security.annotation.Anonymous;
import com.huidou.edgeinsight.common.dto.Result;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 优先级：{@code security.public-paths} 白名单 → {@link Anonymous} → JWT 校验。
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RequestMappingHandlerMapping handlerMapping;
    private final Environment environment;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();
    private final Set<String> anonymousPatterns = new HashSet<>();
    private final AtomicBoolean anonymousScanned = new AtomicBoolean(false);

    private List<String> configuredPublicPaths = Collections.emptyList();

    public JwtAuthFilter(
            JwtTokenProvider jwtTokenProvider,
            @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping,
            Environment environment,
            ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.handlerMapping = handlerMapping;
        this.environment = environment;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        this.configuredPublicPaths = Binder.get(environment)
                .bind("security.public-paths", Bindable.listOf(String.class))
                .orElse(Collections.emptyList());
    }

    @EventListener(ContextRefreshedEvent.class)
    public void registerAnonymousEndpoints(ContextRefreshedEvent event) {
        if (!anonymousScanned.compareAndSet(false, true)) {
            return;
        }
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo mappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();

            boolean isAnonymous = handlerMethod.hasMethodAnnotation(Anonymous.class)
                    || handlerMethod.getBeanType().isAnnotationPresent(Anonymous.class);

            if (!isAnonymous) {
                continue;
            }

            Set<String> patterns = mappingInfo.getPatternsCondition() != null
                    ? mappingInfo.getPatternsCondition().getPatterns()
                    : new HashSet<>();
            Set<RequestMethod> requestMethods = mappingInfo.getMethodsCondition() != null
                    ? mappingInfo.getMethodsCondition().getMethods()
                    : new HashSet<>();

            for (String pattern : patterns) {
                if (requestMethods.isEmpty()) {
                    anonymousPatterns.add("ANY:" + pattern);
                } else {
                    for (RequestMethod method : requestMethods) {
                        anonymousPatterns.add(method.name() + ":" + pattern);
                    }
                }
            }
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String lookupPath = urlPathHelper.getLookupPathForRequest(request);
            String method = request.getMethod();

            if (matchesRules(method, lookupPath, configuredPublicPaths)
                    || matchesRules(method, lookupPath, anonymousPatterns)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = extractBearerToken(request.getHeader("Authorization"));
            if (token == null) {
                sendUnauthorized(response, "Missing or malformed Authorization header");
                return;
            }

            if (!jwtTokenProvider.validateToken(token)) {
                sendUnauthorized(response, "Invalid or expired token");
                return;
            }

            JwtTokenProvider.JwtClaims claims = jwtTokenProvider.parseToken(token);
            List<String> roles = Optional.ofNullable(claims.getRoles()).orElse(Collections.emptyList());
            List<String> perms = Optional.ofNullable(claims.getPerms()).orElse(Collections.emptyList());

            List<GrantedAuthority> authorities = Stream.concat(
                    roles.stream().map(SimpleGrantedAuthority::new),
                    perms.stream().map(SimpleGrantedAuthority::new)
            ).collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(claims, null, authorities);
            authentication.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            sendUnauthorized(response, "Invalid or expired token");
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private boolean matchesRules(String httpMethod, String lookupPath, Iterable<String> rules) {
        for (String rule : rules) {
            if (rule == null || rule.isBlank()) {
                continue;
            }
            String[] parts = rule.trim().split(":", 2);
            if (parts.length != 2) {
                continue;
            }
            String ruleMethod = parts[0].trim();
            String pattern = parts[1].trim();
            boolean methodOk = "ANY".equalsIgnoreCase(ruleMethod) || ruleMethod.equalsIgnoreCase(httpMethod);
            if (methodOk && pathMatcher.match(pattern, lookupPath)) {
                return true;
            }
        }
        return false;
    }

    private static String extractBearerToken(String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return null;
        }
        String trimmed = authHeader.trim();
        if (!trimmed.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return null;
        }
        String token = trimmed.substring(7).trim();
        return token.isEmpty() ? null : token;
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        Result<?> result = Result.unauthorized(message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
