package com.huidou.edgeinsight.common.dto;

import java.time.LocalDateTime;

public class ConnectivityQueryReq extends BaseQuery {
    private Long deviceId;
    private LocalDateTime from;
    private LocalDateTime to;

    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    public LocalDateTime getFrom() { return from; }
    public void setFrom(LocalDateTime from) { this.from = from; }
    public LocalDateTime getTo() { return to; }
    public void setTo(LocalDateTime to) { this.to = to; }
}