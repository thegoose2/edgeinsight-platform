package com.huidou.edgeinsight.api.security;

import com.huidou.edgeinsight.common.model.SysUser;
import com.huidou.edgeinsight.core.config.SystemConfigService;
import com.huidou.edgeinsight.core.repository.spi.SysUserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    /**
     * JWT Payload 解析结果，供过滤器与权限切面使用。
     */
    @Data
    @Builder
    public static class JwtClaims {
        private Long userId;
        private String username;
        private String realName;
        private List<String> roles;
        private List<String> perms;
    }

    private static final String JWT_SECRET_KEY = "jwt.secret";
    private static final String JWT_EXPIRE_HOURS_KEY = "jwt.access_token_expire_hours";
    private static final int DEFAULT_ACCESS_TOKEN_EXPIRE_HOURS = 8;
    private static final String[] INSECURE_SECRETS = {"CHANGE_ME_IN_PROD", "CHANGE_ME", "your-secret-key", "your-256-bit-secret"};

    private final SecretKey secretKey;
    private final int accessTokenExpireHours;
    private final SysUserRepository sysUserRepository;

    public JwtTokenProvider(SystemConfigService systemConfigService, SysUserRepository sysUserRepository) {
        String secret = systemConfigService.getConfig(JWT_SECRET_KEY);
        this.secretKey = resolveSecretKey(secret);
        this.accessTokenExpireHours = resolveExpireHours(systemConfigService);
        this.sysUserRepository = sysUserRepository;
    }

    private SecretKey resolveSecretKey(String secret) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException(
                "JWT secret is not configured. " +
                "Please set '" + JWT_SECRET_KEY + "' in system_config table. " +
                "In production, this MUST be a secure random key of at least 256 bits."
            );
        }

        for (String insecure : INSECURE_SECRETS) {
            if (secret.equalsIgnoreCase(insecure)) {
                throw new IllegalStateException(
                    "JWT secret '" + secret + "' is insecure and must be changed in production. " +
                    "Please set a secure value for '" + JWT_SECRET_KEY + "' in system_config table."
                );
            }
        }

        if (secret.length() < 32) {
            throw new IllegalStateException(
                "JWT secret must be at least 32 characters (256 bits) for HS256. " +
                "Current length: " + secret.length()
            );
        }

        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private int resolveExpireHours(SystemConfigService systemConfigService) {
        String expireHoursStr = systemConfigService.getConfig(JWT_EXPIRE_HOURS_KEY);
        if (expireHoursStr == null || expireHoursStr.trim().isEmpty()) {
            return DEFAULT_ACCESS_TOKEN_EXPIRE_HOURS;
        }
        try {
            int hours = Integer.parseInt(expireHoursStr.trim());
            if (hours <= 0) {
                return DEFAULT_ACCESS_TOKEN_EXPIRE_HOURS;
            }
            return hours;
        } catch (NumberFormatException e) {
            return DEFAULT_ACCESS_TOKEN_EXPIRE_HOURS;
        }
    }

    public String generateToken(String username) {
        SysUser user = sysUserRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleCode())
                .collect(Collectors.toList());
        java.util.Set<String> perms = sysUserRepository.findPermissionCodesByUsername(username);
        return generateToken(user, roles, new java.util.ArrayList<>(perms));
    }

    public String generateToken(SysUser user, List<String> roles, List<String> perms) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (accessTokenExpireHours * 60 * 60 * 1000L));

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("username", user.getUsername())
                .claim("realName", user.getRealName())
                .claim("roles", roles)
                .claim("perms", perms)
                .claim("iat", now)
                .claim("exp", expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public JwtClaims parseToken(String token) {
        Jws<Claims> claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

        Claims payload = claims.getPayload();

        Long userId = payload.get("userId", Long.class);
        String username = payload.get("username", String.class);
        String realName = payload.get("realName", String.class);

        @SuppressWarnings("unchecked")
        List<String> roles = payload.get("roles", List.class);
        @SuppressWarnings("unchecked")
        List<String> perms = payload.get("perms", List.class);

        return JwtClaims.builder()
                .userId(userId)
                .username(username)
                .realName(realName)
                .roles(roles)
                .perms(perms)
                .build();
    }

    public Instant getExpirationInstant() {
        return Instant.now().plus(accessTokenExpireHours, ChronoUnit.HOURS);
    }
}