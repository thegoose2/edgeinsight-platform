package com.huidou.edgeinsight.common.dto;

import java.time.LocalDateTime;
import java.util.List;

public class DeviceVO {
    private Long id;
    private String deviceCode;
    private String name;
    private String location;
    private String connectId;
    private Long deviceTypeId;
    private String deviceTypeName;
    private String profileType;
    private String lifecycleStatus;
    private String onlineStatus;
    private LocalDateTime connectedAt;
    private LocalDateTime disconnectedAt;
    private LocalDateTime lastSeenAt;
    private List<DeviceTypePointVO> points;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public String getDeviceTypeName() { return deviceTypeName; }
    public void setDeviceTypeName(String deviceTypeName) { this.deviceTypeName = deviceTypeName; }
    public String getProfileType() { return profileType; }
    public void setProfileType(String profileType) { this.profileType = profileType; }
    public String getLifecycleStatus() { return lifecycleStatus; }
    public void setLifecycleStatus(String lifecycleStatus) { this.lifecycleStatus = lifecycleStatus; }
    public String getOnlineStatus() { return onlineStatus; }
    public void setOnlineStatus(String onlineStatus) { this.onlineStatus = onlineStatus; }
    public LocalDateTime getConnectedAt() { return connectedAt; }
    public void setConnectedAt(LocalDateTime connectedAt) { this.connectedAt = connectedAt; }
    public LocalDateTime getDisconnectedAt() { return disconnectedAt; }
    public void setDisconnectedAt(LocalDateTime disconnectedAt) { this.disconnectedAt = disconnectedAt; }
    public LocalDateTime getLastSeenAt() { return lastSeenAt; }
    public void setLastSeenAt(LocalDateTime lastSeenAt) { this.lastSeenAt = lastSeenAt; }
    public List<DeviceTypePointVO> getPoints() { return points; }
    public void setPoints(List<DeviceTypePointVO> points) { this.points = points; }
}