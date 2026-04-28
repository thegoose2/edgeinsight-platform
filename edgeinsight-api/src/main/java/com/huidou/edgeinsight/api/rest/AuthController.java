package com.huidou.edgeinsight.api.rest;

import com.huidou.edgeinsight.common.dto.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public Result<?> login(@RequestBody Object body) {
        return Result.ok();
    }

    @PostMapping("/logout")
    public Result<?> logout() {
        return Result.ok();
    }
}
