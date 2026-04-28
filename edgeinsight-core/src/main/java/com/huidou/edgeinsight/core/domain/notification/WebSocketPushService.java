package com.huidou.edgeinsight.core.domain.notification;

public interface WebSocketPushService {

    void pushDeviceStatusChange(Long deviceId, String status);

    void pushAlarm(Long deviceId, String alarmCode, String alarmDesc);
}
