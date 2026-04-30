package com.huidou.edgeinsight.common.dto;

public class DeviceTypeInsertReq {
    private String typeCode;
    private String name;
    private String profileType;
    private String description;

    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProfileType() { return profileType; }
    public void setProfileType(String profileType) { this.profileType = profileType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}