package com.huidou.edgeinsight.core.domain.notification;

public interface AndonClient {

    void sendAlarm(Long deviceId, String alarmCode, String alarmDesc, Long timestamp);
}
