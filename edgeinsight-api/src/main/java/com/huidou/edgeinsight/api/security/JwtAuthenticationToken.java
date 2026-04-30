package com.huidou.edgeinsight.api.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final JwtTokenProvider.UserInfo userInfo;

    public JwtAuthenticationToken(JwtTokenProvider.UserInfo userInfo) {
        super(userInfo.getPerms());
        this.userInfo = userInfo;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userInfo;
    }

    public JwtTokenProvider.UserInfo getUserInfo() {
        return userInfo;
    }
}