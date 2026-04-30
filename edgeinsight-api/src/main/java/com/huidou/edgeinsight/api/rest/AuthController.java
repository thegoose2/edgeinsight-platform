package com.huidou.edgeinsight.api.rest;

import com.huidou.edgeinsight.api.security.annotation.Anonymous;
import com.huidou.edgeinsight.common.dto.LoginReq;
import com.huidou.edgeinsight.common.dto.LoginVO;
import com.huidou.edgeinsight.common.dto.Result;
import com.huidou.edgeinsight.core.domain.auth.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Anonymous
    public Result<LoginVO> login(@RequestBody LoginReq req) {
        LoginVO vo = authService.login(req.getUsername(), req.getPassword());
        return Result.ok(vo);
    }

    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
        }
        return Result.ok();
    }

    @GetMapping("/getUserInfo")
    public Result<LoginVO.UserInfoVO> getUserInfo() {
        LoginVO.UserInfoVO vo = authService.getUserInfo();
        return Result.ok(vo);
    }
}