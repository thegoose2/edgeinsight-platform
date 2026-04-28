package com.huidou.edgeinsight.core.domain.telemetry;

import com.huidou.edgeinsight.common.model.TelemetryRecord;

import java.util.List;

public interface TelemetryQueryService {

    TelemetryRecord getLatest(Long deviceId, String pointCode);

    List<TelemetryRecord> queryHistory(Long deviceId, String pointCode, Long startTime, Long endTime);
}
