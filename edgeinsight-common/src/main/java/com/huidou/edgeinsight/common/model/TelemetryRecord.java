package com.huidou.edgeinsight.common.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "telemetry_record")
public class TelemetryRecord {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "point_code")
    private String pointCode;

    @Column(name = "ts")
    private LocalDateTime ts;

    @Column(name = "num_value")
    private Double numValue;

    @Column(name = "str_value")
    private String strValue;
}
