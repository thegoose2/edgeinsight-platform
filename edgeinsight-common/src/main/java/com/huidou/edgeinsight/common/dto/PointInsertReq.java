package com.huidou.edgeinsight.common.dto;

import java.math.BigDecimal;

public class PointInsertReq {
    private Long deviceTypeId;
    private String pointCode;
    private String name;
    private String dataType;
    private String unit;
    private BigDecimal rangeMin;
    private BigDecimal rangeMax;

    public Long getDeviceTypeId() { return deviceTypeId; }
    public void setDeviceTypeId(Long deviceTypeId) { this.deviceTypeId = deviceTypeId; }
    public String getPointCode() { return pointCode; }
    public void setPointCode(String pointCode) { this.pointCode = pointCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getRangeMin() { return rangeMin; }
    public void setRangeMin(BigDecimal rangeMin) { this.rangeMin = rangeMin; }
    public BigDecimal getRangeMax() { return rangeMax; }
    public void setRangeMax(BigDecimal rangeMax) { this.rangeMax = rangeMax; }
}