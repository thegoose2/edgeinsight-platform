package com.huidou.edgeinsight.api.rest;

import com.huidou.edgeinsight.common.dto.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/connectivity")
public class ConnectivityController {

    @GetMapping("/status/{deviceId}")
    public Result<?> getStatus(@PathVariable Long deviceId) {
        return Result.ok();
    }

    @GetMapping("/logs/{deviceId}")
    public Result<?> getLogs(@PathVariable Long deviceId) {
        return Result.ok();
    }

    @GetMapping("/logs/{deviceId}/history")
    public Result<?> getLogsHistory(@PathVariable Long deviceId,
                                     @RequestParam Long startTime, @RequestParam Long endTime) {
        return Result.ok();
    }
}
