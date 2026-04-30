package com.huidou.edgeinsight.api.rest;

import com.huidou.edgeinsight.api.security.JwtTokenProvider;
import com.huidou.edgeinsight.common.dto.login.LoginRequest;
import com.huidou.edgeinsight.common.dto.login.LoginResponse;
import com.huidou.edgeinsight.common.dto.Result;
import com.huidou.edgeinsight.common.dto.login.LoginResponseUserInfo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = jwtTokenProvider.generateToken(request.getUsername());
        LoginResponseUserInfo userInfo = new LoginResponseUserInfo(1L, request.getUsername(), "user@example.com");
        LoginResponse response = new LoginResponse(token, userInfo);
        return Result.ok(response);
    }

    @PostMapping("/logout")
    public Result<?> logout() {
        return Result.ok();
    }
}
