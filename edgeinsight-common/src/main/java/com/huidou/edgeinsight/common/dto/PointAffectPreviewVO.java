package com.huidou.edgeinsight.common.dto;

public class PointAffectPreviewVO {
    private Long affectedDeviceCount;
    private String message;

    public Long getAffectedDeviceCount() { return affectedDeviceCount; }
    public void setAffectedDeviceCount(Long affectedDeviceCount) { this.affectedDeviceCount = affectedDeviceCount; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}