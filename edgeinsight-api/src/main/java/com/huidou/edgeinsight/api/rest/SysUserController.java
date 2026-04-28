package com.huidou.edgeinsight.api.rest;

import com.huidou.edgeinsight.common.dto.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class SysUserController {

    @GetMapping
    public Result<?> list() {
        return Result.ok();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.ok();
    }

    @PostMapping
    public Result<?> create(@RequestBody Object body) {
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Object body) {
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return Result.ok();
    }
}
