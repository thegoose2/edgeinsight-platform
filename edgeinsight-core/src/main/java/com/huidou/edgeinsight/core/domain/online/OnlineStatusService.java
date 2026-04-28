package com.huidou.edgeinsight.core.domain.online;

public interface OnlineStatusService {

    void updateOnlineStatus(String connectId, boolean online);

    String getOnlineStatus(Long deviceId);

    void processHeartbeat(String connectId);
}
