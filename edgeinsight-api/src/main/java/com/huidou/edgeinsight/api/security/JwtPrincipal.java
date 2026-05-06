package com.huidou.edgeinsight.api.security;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JwtPrincipal {
    private Long userId;
    private String username;
    private String realName;
    private List<String> roles;
    private List<String> perms;
}