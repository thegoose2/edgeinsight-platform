package com.huidou.edgeinsight.common.dto;

public class ProtocolProfileInsertReq {
    private String profileType;
    private String protocol;
    private String parserId;
    private String frameStrategy;
    private String description;

    public String getProfileType() { return profileType; }
    public void setProfileType(String profileType) { this.profileType = profileType; }
    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }
    public String getParserId() { return parserId; }
    public void setParserId(String parserId) { this.parserId = parserId; }
    public String getFrameStrategy() { return frameStrategy; }
    public void setFrameStrategy(String frameStrategy) { this.frameStrategy = frameStrategy; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}