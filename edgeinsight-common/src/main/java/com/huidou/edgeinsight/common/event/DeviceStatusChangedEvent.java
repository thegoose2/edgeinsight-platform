package com.huidou.edgeinsight.common.event;

import org.springframework.context.ApplicationEvent;

public class DeviceStatusChangedEvent extends ApplicationEvent {

    private final Long deviceId;
    private final String onlineStatus;
    private final Long timestamp;

    public DeviceStatusChangedEvent(Object source, Long deviceId, String onlineStatus, Long timestamp) {
        super(source);
        this.deviceId = deviceId;
        this.onlineStatus = onlineStatus;
        this.timestamp = timestamp;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
