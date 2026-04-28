package com.huidou.edgeinsight.common.model;

import com.huidou.edgeinsight.common.model.base.AuditableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "device_type_point")
public class DeviceTypePoint extends AuditableEntity {

    @Column(name = "device_type_id")
    private Long deviceTypeId;

    @Column(name = "point_code")
    private String pointCode;

    @Column(name = "name")
    private String name;

    @Column(name = "data_type")
    private String dataType;

    @Column(name = "unit")
    private String unit;

    @Column(name = "range_min", precision = 20, scale = 6)
    private BigDecimal rangeMin;

    @Column(name = "range_max", precision = 20, scale = 6)
    private BigDecimal rangeMax;

    @Column(name = "is_active")
    private Boolean isActive;
}
