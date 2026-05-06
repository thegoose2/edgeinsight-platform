package com.huidou.edgeinsight.common.event;

import org.springframework.context.ApplicationEvent;

public class TelemetryReceivedEvent extends ApplicationEvent {

    private final Long deviceId;
    private final String pointCode;
    private final Object value;
    private final Long eventTimestamp;

    public TelemetryReceivedEvent(Object source, Long deviceId, String pointCode, Object value, Long eventTimestamp) {
        super(source);
        this.deviceId = deviceId;
        this.pointCode = pointCode;
        this.value = value;
        this.eventTimestamp = eventTimestamp;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public String getPointCode() {
        return pointCode;
    }

    public Object getValue() {
        return value;
    }

    public Long getEventTimestamp() {
        return eventTimestamp;
    }
}
