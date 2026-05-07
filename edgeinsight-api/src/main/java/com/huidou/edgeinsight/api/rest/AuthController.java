package com.huidou.edgeinsight.api.rest;

import com.huidou.edgeinsight.api.security.JwtTokenProvider;
import com.huidou.edgeinsight.api.security.annotation.Anonymous;
import com.huidou.edgeinsight.common.dto.Result;
import com.huidou.edgeinsight.common.exception.UnauthorizedException;
import com.huidou.edgeinsight.common.dto.login.LoginRequest;
import com.huidou.edgeinsight.common.dto.login.LoginResponse;
import com.huidou.edgeinsight.common.dto.login.LoginResponseUserInfo;
import com.huidou.edgeinsight.core.domain.auth.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
        AuthService.LoginContext ctx = authService.login(request);

        String token = jwtTokenProvider.generateToken(ctx.getUserInfo().getUsername());

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .expireAt(jwtTokenProvider.getExpirationInstant())
                .userInfo(ctx.getUserInfo())
                .build();

        return Result.ok(response);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        SecurityContextHolder.clearContext();
        return Result.ok();
    }

    @GetMapping("/getUserInfo")
    public Result<LoginResponseUserInfo> getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof JwtTokenProvider.JwtClaims)) {
            throw new UnauthorizedException("User not authenticated");
        }
        JwtTokenProvider.JwtClaims p = (JwtTokenProvider.JwtClaims) auth.getPrincipal();
        return Result.ok(LoginResponseUserInfo.builder()
                .userId(p.getUserId())
                .username(p.getUsername())
                .realName(p.getRealName())
                .roles(p.getRoles())
                .perms(p.getPerms())
                .build());
    }
}