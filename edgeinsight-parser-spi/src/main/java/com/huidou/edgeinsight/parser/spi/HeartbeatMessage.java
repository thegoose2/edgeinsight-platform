package com.huidou.edgeinsight.parser.spi;

public class HeartbeatMessage extends ParsedMessage {

    public HeartbeatMessage() {
        this.msgType = "HEARTBEAT";
    }
}
