package com.huidou.edgeinsight.common.dto.login;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class LoginResponseData {
    private String token;
    private Instant expiresAt;
    private LoginResponseUserInfo userInfo;
}
