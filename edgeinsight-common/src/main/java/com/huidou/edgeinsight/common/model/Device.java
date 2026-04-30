package com.huidou.edgeinsight.common.model;

import com.huidou.edgeinsight.common.model.base.AuditableEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "device")
public class Device extends AuditableEntity {

    @Column(name = "device_code")
    private String deviceCode;

    @Column(name = "name")
    private String name;

    @Column(name = "location")
    private String location;

    @Column(name = "connect_id")
    private String connectId;

    @Column(name = "device_type_id")
    private Long deviceTypeId;

    @Column(name = "lifecycle_status")
    private String lifecycleStatus;
}
