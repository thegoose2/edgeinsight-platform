package com.huidou.edgeinsight.common.dto;

public class DeviceInsertReq {
    private String deviceCode;
    private String name;
    private String location;
    private String connectId;
    private Long deviceTypeId;

    public String getDeviceCode() { return deviceCode; }
    public void setDeviceCode(String deviceCode) { this.deviceCode = deviceCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getConnectId() { return connectId; }
    public void setConnectId(String connectId) { this.connectId = connectId; }
    public Long getDeviceTypeId() { return deviceTypeId; }
    public void setDeviceTypeId(Long deviceTypeId) { this.deviceTypeId = deviceTypeId; }
}