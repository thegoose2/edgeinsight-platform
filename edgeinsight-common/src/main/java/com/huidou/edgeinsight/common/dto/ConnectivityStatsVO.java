package com.huidou.edgeinsight.common.dto;

public class ConnectivityStatsVO {
    private Long deviceId;
    private String deviceName;
    private Long periodSeconds;
    private Long onlineTotalS;
    private Long offlineTotalS;
    private Long offlineCount;
    private Double avgOfflineS;
    private Integer maxOfflineS;
    private Double availabilityRate;

    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public Long getPeriodSeconds() { return periodSeconds; }
    public void setPeriodSeconds(Long periodSeconds) { this.periodSeconds = periodSeconds; }
    public Long getOnlineTotalS() { return onlineTotalS; }
    public void setOnlineTotalS(Long onlineTotalS) { this.onlineTotalS = onlineTotalS; }
    public Long getOfflineTotalS() { return offlineTotalS; }
    public void setOfflineTotalS(Long offlineTotalS) { this.offlineTotalS = offlineTotalS; }
    public Long getOfflineCount() { return offlineCount; }
    public void setOfflineCount(Long offlineCount) { this.offlineCount = offlineCount; }
    public Double getAvgOfflineS() { return avgOfflineS; }
    public void setAvgOfflineS(Double avgOfflineS) { this.avgOfflineS = avgOfflineS; }
    public Integer getMaxOfflineS() { return maxOfflineS; }
    public void setMaxOfflineS(Integer maxOfflineS) { this.maxOfflineS = maxOfflineS; }
    public Double getAvailabilityRate() { return availabilityRate; }
    public void setAvailabilityRate(Double availabilityRate) { this.availabilityRate = availabilityRate; }
}