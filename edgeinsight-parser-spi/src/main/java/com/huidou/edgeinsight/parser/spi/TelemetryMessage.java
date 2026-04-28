package com.huidou.edgeinsight.parser.spi;

public class TelemetryMessage extends ParsedMessage {

    private String pointCode;
    private Object value;

    public TelemetryMessage() {
        this.msgType = "TELEMETRY";
    }

    public String getPointCode() {
        return pointCode;
    }

    public void setPointCode(String pointCode) {
        this.pointCode = pointCode;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
