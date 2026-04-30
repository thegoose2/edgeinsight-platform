package com.huidou.edgeinsight.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expireHours;

    public JwtTokenProvider(
            @Value("${jwt.secret:default-secret-key-for-development-only}") String secret,
            @Value("${jwt.access_token_expire_hours:8}") long expireHours) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireHours = expireHours;
    }

    public String generateToken(UserInfo userInfo) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireHours * 3600 * 1000L);
        return Jwts.builder()
                .claim("userId", userInfo.getUserId())
                .claim("username", userInfo.getUsername())
                .claim("realName", userInfo.getRealName())
                .claim("roles", userInfo.getRoles())
                .claim("perms", userInfo.getPerms())
                .setSubject(userInfo.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }

    public UserInfo parseToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        UserInfo info = new UserInfo();
        info.setUserId(((Number) claims.get("userId")).longValue());
        info.setUsername(claims.getSubject());
        info.setRealName((String) claims.get("realName"));
        info.setRoles(claims.get("roles", List.class));
        info.setPerms(claims.get("perms", List.class));
        return info;
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    public static class UserInfo {
        private Long userId;
        private String username;
        private String realName;
        private List<String> roles = new ArrayList<>();
        private List<String> perms = new ArrayList<>();

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }
        public List<String> getPerms() { return perms; }
        public void setPerms(List<String> perms) { this.perms = perms; }
    }
}