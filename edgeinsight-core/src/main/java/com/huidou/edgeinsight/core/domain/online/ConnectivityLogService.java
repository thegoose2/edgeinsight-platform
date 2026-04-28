package com.huidou.edgeinsight.core.domain.online;

import com.huidou.edgeinsight.common.model.DeviceConnectivityLog;

import java.util.List;

public interface ConnectivityLogService {

    void recordConnect(Long deviceId, String connectId);

    void recordDisconnect(Long deviceId, String connectId);

    List<DeviceConnectivityLog> getLogs(Long deviceId);

    List<DeviceConnectivityLog> getLogs(Long deviceId, Long startTime, Long endTime);
}
