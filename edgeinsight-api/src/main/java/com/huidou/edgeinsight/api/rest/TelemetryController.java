package com.huidou.edgeinsight.api.rest;

import com.huidou.edgeinsight.common.dto.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/telemetry")
public class TelemetryController {

    @GetMapping("/latest/{deviceId}")
    public Result<?> getLatest(@PathVariable Long deviceId, @RequestParam String pointCode) {
        return Result.ok();
    }

    @GetMapping("/history/{deviceId}")
    public Result<?> getHistory(@PathVariable Long deviceId, @RequestParam String pointCode,
                                @RequestParam Long startTime, @RequestParam Long endTime) {
        return Result.ok();
    }
}
