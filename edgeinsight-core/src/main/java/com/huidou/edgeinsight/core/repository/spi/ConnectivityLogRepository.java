package com.huidou.edgeinsight.core.repository.spi;

import com.huidou.edgeinsight.common.model.DeviceConnectivityLog;

import java.util.List;

public interface ConnectivityLogRepository {

    DeviceConnectivityLog save(DeviceConnectivityLog log);

    List<DeviceConnectivityLog> findByDeviceId(Long deviceId);

    List<DeviceConnectivityLog> findByDeviceIdAndTimeRange(Long deviceId, Long startTime, Long endTime);
}
