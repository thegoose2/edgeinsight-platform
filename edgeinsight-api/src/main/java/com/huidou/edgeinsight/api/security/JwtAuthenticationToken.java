package com.huidou.edgeinsight.api.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final JwtPrincipal principal;

    public JwtAuthenticationToken(JwtPrincipal principal, List<String> roles, List<String> perms) {
        super(convertToAuthorities(roles, perms));
        this.principal = principal;
        setAuthenticated(true);
    }

    private static Collection<? extends GrantedAuthority> convertToAuthorities(List<String> roles, List<String> perms) {
        return Stream.concat(
                roles.stream().map(SimpleGrantedAuthority::new),
                perms.stream().map(SimpleGrantedAuthority::new)
        ).collect(Collectors.toList());
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}