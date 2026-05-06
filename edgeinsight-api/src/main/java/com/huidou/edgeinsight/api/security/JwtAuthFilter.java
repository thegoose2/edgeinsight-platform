package com.huidou.edgeinsight.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huidou.edgeinsight.common.dto.Result;
import io.jsonwebtoken.JwtException;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final PublicEndpointRegistry publicEndpointRegistry;
    private final ObjectMapper objectMapper;

    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider, PublicEndpointRegistry publicEndpointRegistry, ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.publicEndpointRegistry = publicEndpointRegistry;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (publicEndpointRegistry.isPublic(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            String authHeader = request.getHeader("Authorization");
            String token = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            if (token == null || token.isEmpty()) {
                sendUnauthorized(response, "Missing token");
                return;
            }

            if (!jwtTokenProvider.validateToken(token)) {
                sendUnauthorized(response, "Invalid or expired token");
                return;
            }

            JwtPrincipal principal = jwtTokenProvider.parseToken(token);
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                    principal,
                    principal.getRoles(),
                    principal.getPerms()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            sendUnauthorized(response, "Invalid or expired token");
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Result<?> result = Result.unauthorized(message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}