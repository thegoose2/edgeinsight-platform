package com.huidou.edgeinsight.common.dto;

import java.time.LocalDateTime;

public class TelemetryRecordVO {
    private LocalDateTime ts;
    private Object value;

    public LocalDateTime getTs() { return ts; }
    public void setTs(LocalDateTime ts) { this.ts = ts; }
    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }
}