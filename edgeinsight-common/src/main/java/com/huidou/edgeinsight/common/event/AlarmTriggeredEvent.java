package com.huidou.edgeinsight.common.event;

import org.springframework.context.ApplicationEvent;

public class AlarmTriggeredEvent extends ApplicationEvent {

    private final Long deviceId;
    private final String alarmCode;
    private final String alarmDesc;
    private final Long eventTimestamp;

    public AlarmTriggeredEvent(Object source, Long deviceId, String alarmCode, String alarmDesc, Long eventTimestamp) {
        super(source);
        this.deviceId = deviceId;
        this.alarmCode = alarmCode;
        this.alarmDesc = alarmDesc;
        this.eventTimestamp = eventTimestamp;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public String getAlarmCode() {
        return alarmCode;
    }

    public String getAlarmDesc() {
        return alarmDesc;
    }

    public Long getEventTimestamp() {
        return eventTimestamp;
    }
}
