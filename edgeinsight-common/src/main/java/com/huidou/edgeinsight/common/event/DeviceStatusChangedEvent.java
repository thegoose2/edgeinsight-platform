package com.huidou.edgeinsight.common.event;

import org.springframework.context.ApplicationEvent;

public class DeviceStatusChangedEvent extends ApplicationEvent {

    private final Long deviceId;
    private final String onlineStatus;
    private final Long eventTimestamp;

    public DeviceStatusChangedEvent(Object source, Long deviceId, String onlineStatus, Long eventTimestamp) {
        super(source);
        this.deviceId = deviceId;
        this.onlineStatus = onlineStatus;
        this.eventTimestamp = eventTimestamp;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public Long getEventTimestamp() {
        return eventTimestamp;
    }
}
