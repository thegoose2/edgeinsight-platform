package com.huidou.edgeinsight.core.cache;

import java.util.Set;

public class ResolvedContext {

    private Long deviceId;
    private Long deviceTypeId;
    private String parserId;
    private String protocol;
    private Set<String> validPointCodes;
    private String lifecycleStatus;
    private int gracePeriodSecs;

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(Long deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public String getParserId() {
        return parserId;
    }

    public void setParserId(String parserId) {
        this.parserId = parserId;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Set<String> getValidPointCodes() {
        return validPointCodes;
    }

    public void setValidPointCodes(Set<String> validPointCodes) {
        this.validPointCodes = validPointCodes;
    }

    public String getLifecycleStatus() {
        return lifecycleStatus;
    }

    public void setLifecycleStatus(String lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
    }

    public int getGracePeriodSecs() {
        return gracePeriodSecs;
    }

    public void setGracePeriodSecs(int gracePeriodSecs) {
        this.gracePeriodSecs = gracePeriodSecs;
    }
}
