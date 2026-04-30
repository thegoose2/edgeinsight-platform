package com.huidou.edgeinsight.common.event;

import org.springframework.context.ApplicationEvent;

public class TelemetryReceivedEvent extends ApplicationEvent {

    private final Long deviceId;
    private final String pointCode;
    private final Object value;
    private final Long timestamp;

    public TelemetryReceivedEvent(Object source, Long deviceId, String pointCode, Object value, Long timestamp) {
        super(source);
        this.deviceId = deviceId;
        this.pointCode = pointCode;
        this.value = value;
        this.timestamp = timestamp;
    }
}
