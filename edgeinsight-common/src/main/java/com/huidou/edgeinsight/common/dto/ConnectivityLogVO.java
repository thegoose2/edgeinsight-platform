package com.huidou.edgeinsight.common.dto;

import java.time.LocalDateTime;

public class ConnectivityLogVO {
    private LocalDateTime wentOfflineAt;
    private LocalDateTime cameOnlineAt;
    private Integer offlineDurationS;
    private Integer onlineDurationS;
    private String cause;

    public LocalDateTime getWentOfflineAt() { return wentOfflineAt; }
    public void setWentOfflineAt(LocalDateTime wentOfflineAt) { this.wentOfflineAt = wentOfflineAt; }
    public LocalDateTime getCameOnlineAt() { return cameOnlineAt; }
    public void setCameOnlineAt(LocalDateTime cameOnlineAt) { this.cameOnlineAt = cameOnlineAt; }
    public Integer getOfflineDurationS() { return offlineDurationS; }
    public void setOfflineDurationS(Integer offlineDurationS) { this.offlineDurationS = offlineDurationS; }
    public Integer getOnlineDurationS() { return onlineDurationS; }
    public void setOnlineDurationS(Integer onlineDurationS) { this.onlineDurationS = onlineDurationS; }
    public String getCause() { return cause; }
    public void setCause(String cause) { this.cause = cause; }
}