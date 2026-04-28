package com.huidou.edgeinsight.parser.spi;

public class StatusMessage extends ParsedMessage {

    private String onlineStatus;

    public StatusMessage() {
        this.msgType = "STATUS";
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
}
