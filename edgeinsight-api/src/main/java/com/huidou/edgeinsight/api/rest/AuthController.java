package com.huidou.edgeinsight.api.rest;

import com.huidou.edgeinsight.api.security.JwtTokenProvider;
import com.huidou.edgeinsight.api.security.annotation.Anonymous;
import com.huidou.edgeinsight.common.dto.Result;
import com.huidou.edgeinsight.common.dto.login.LoginRequest;
import com.huidou.edgeinsight.common.dto.login.LoginResponse;
import com.huidou.edgeinsight.common.dto.login.LoginResponseData;
import com.huidou.edgeinsight.common.dto.login.LoginResponseUserInfo;
import com.huidou.edgeinsight.core.domain.auth.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Anonymous
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponseUserInfo userInfo = authService.login(request);

        String token = jwtTokenProvider.generateToken(userInfo.getUsername());

        Instant expiresAt = jwtTokenProvider.getExpirationInstant();

        LoginResponseData data = LoginResponseData.builder()
                .token(token)
                .expiresAt(expiresAt)
                .userInfo(userInfo)
                .build();

        LoginResponse response = LoginResponse.builder()
                .code("200")
                .data(data)
                .build();

        return Result.ok(response);
    }
}
