package com.huidou.edgeinsight.api.security;

import com.huidou.edgeinsight.common.model.SysUser;
import com.huidou.edgeinsight.core.config.SystemConfigService;
import com.huidou.edgeinsight.core.repository.spi.SysUserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final String JWT_SECRET_KEY = "jwt.secret";
    private static final String JWT_EXPIRE_HOURS_KEY = "jwt.access_token_expire_hours";
    private static final int DEFAULT_ACCESS_TOKEN_EXPIRE_HOURS = 24;
    private static final String[] INSECURE_SECRETS = {"CHANGE_ME_IN_PROD", "CHANGE_ME", "your-secret-key", "your-256-bit-secret"};

    private final SecretKey secretKey;
    private final int accessTokenExpireHours;
    private final SysUserRepository sysUserRepository;

    //读取
    public JwtTokenProvider(SystemConfigService systemConfigService, SysUserRepository sysUserRepository) {
        String secret = systemConfigService.getConfig(JWT_SECRET_KEY);
        this.secretKey = resolveSecretKey(secret);
        this.accessTokenExpireHours = resolveExpireHours(systemConfigService);
        this.sysUserRepository = sysUserRepository;
    }

    //解析
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
        SysUser user = sysUserRepository.findByUsernameWithRolesAndPermissions(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        List<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getRoleCode())
                .collect(Collectors.toList());

        Set<String> permissions = user.getUserRoles().stream()
                .flatMap(ur -> ur.getRole().getRolePermissions().stream())
                .map(rp -> rp.getPermission().getPermCode())
                .collect(Collectors.toSet());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (accessTokenExpireHours * 60 * 60 * 1000L));

        return Jwts.builder()
                .claim("userId", user.getId())
                .claim("username", user.getUsername())
                .claim("realName", user.getRealName())
                .claim("roles", roles)
                .claim("permissions", permissions)
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

    public Map<String, Object> parseClaims(String token) {
        Jws<Claims> claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
        return claims.getPayload();
    }

    public Instant getExpirationInstant() {
        return Instant.now().plus(accessTokenExpireHours, ChronoUnit.HOURS);
    }
}
