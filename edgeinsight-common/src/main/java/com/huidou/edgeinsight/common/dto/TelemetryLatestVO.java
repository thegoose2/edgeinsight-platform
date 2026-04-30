package com.huidou.edgeinsight.common.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TelemetryLatestVO {
    private Long deviceId;
    private List<TelemetryPointValueVO> values;

    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    public List<TelemetryPointValueVO> getValues() { return values; }
    public void setValues(List<TelemetryPointValueVO> values) { this.values = values; }

    public static class TelemetryPointValueVO {
        private String pointCode;
        private String name;
        private Object value;
        private String unit;
        private LocalDateTime ts;

        public String getPointCode() { return pointCode; }
        public void setPointCode(String pointCode) { this.pointCode = pointCode; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        public LocalDateTime getTs() { return ts; }
        public void setTs(LocalDateTime ts) { this.ts = ts; }
    }
}