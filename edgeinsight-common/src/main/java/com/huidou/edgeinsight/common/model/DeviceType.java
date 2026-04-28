package com.huidou.edgeinsight.common.model;

import com.huidou.edgeinsight.common.model.base.AuditableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "device_type")
public class DeviceType extends AuditableEntity {

    @Column(name = "type_code")
    private String typeCode;

    @Column(name = "name")
    private String name;