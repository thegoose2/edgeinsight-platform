package com.huidou.edgeinsight.core.domain.online;

public interface OnlineStatusService {

    void handleOnline(String connectId);

    void handleOffline(String connectId, String cause);

    String getOnlineStatus(Long deviceId);

    void processHeartbeat(String connectId);
}