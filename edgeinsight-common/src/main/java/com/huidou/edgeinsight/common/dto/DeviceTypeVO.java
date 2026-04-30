package com.huidou.edgeinsight.common.dto;

import java.util.List;

public class DeviceTypeVO {
    private Long id;
    private String typeCode;
    private String name;
    private String profileType;
    private String description;
    private Long deviceCount;
    private List<DeviceTypePointVO> points;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProfileType() { return profileType; }
    public void setProfileType(String profileType) { this.profileType = profileType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getDeviceCount() { return deviceCount; }
    public void setDeviceCount(Long deviceCount) { this.deviceCount = deviceCount; }
    public List<DeviceTypePointVO> getPoints() { return points; }
    public void setPoints(List<DeviceTypePointVO> points) { this.points = points; }
}