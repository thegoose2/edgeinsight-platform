package com.huidou.edgeinsight.core.repository.spi;

import com.huidou.edgeinsight.common.model.TelemetryRecord;

import java.util.List;

public interface TelemetryRepository {

    void save(TelemetryRecord record);

    void batchSave(List<TelemetryRecord> records);

    TelemetryRecord findLatest(Long deviceId, String pointCode);

    List<TelemetryRecord> findHistory(Long deviceId, String pointCode, Long startTime, Long endTime);
}
