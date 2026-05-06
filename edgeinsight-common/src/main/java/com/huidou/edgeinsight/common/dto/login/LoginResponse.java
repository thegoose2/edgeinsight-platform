package com.huidou.edgeinsight.common.dto.login;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class LoginResponse {
    private String token;
    private Instant expireAt;
    private LoginResponseUserInfo userInfo;
}