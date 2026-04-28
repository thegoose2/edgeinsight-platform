package com.huidou.edgeinsight.api.rest;

import com.huidou.edgeinsight.common.dto.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system-config")
public class SystemConfigController {

    @GetMapping
    public Result<?> getAll() {
        return Result.ok();
    }

    @GetMapping("/{key}")
    public Result<?> getByKey(@PathVariable String key) {
        return Result.ok();
    }

    @PutMapping("/{key}")
    public Result<?> update(@PathVariable String key, @RequestBody Object body) {
        return Result.ok();
    }
}
