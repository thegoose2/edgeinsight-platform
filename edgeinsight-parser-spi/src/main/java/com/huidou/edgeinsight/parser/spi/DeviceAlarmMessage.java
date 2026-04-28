package com.huidou.edgeinsight.parser.spi;

public class DeviceAlarmMessage extends ParsedMessage {

    private String alarmCode;
    private String alarmDesc;

    public DeviceAlarmMessage() {
        this.msgType = "ALARM";
    }

    public String getAlarmCode() {
        return alarmCode;
    }

    public void setAlarmCode(String alarmCode) {
        this.alarmCode = alarmCode;
    }

    public String getAlarmDesc() {
        return alarmDesc;
    }

    public void setAlarmDesc(String alarmDesc) {
        this.alarmDesc = alarmDesc;
    }
}
