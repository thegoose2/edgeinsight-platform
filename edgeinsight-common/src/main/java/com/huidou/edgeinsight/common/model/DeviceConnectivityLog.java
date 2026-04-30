package com.huidou.edgeinsight.common.model;

import com.huidou.edgeinsight.common.model.base.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "device_connectivity_log")
public class DeviceConnectivityLog extends BaseEntity {

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "went_offline_at")
    private LocalDateTime wentOfflineAt;

    @Column(name = "came_online_at")
    private LocalDateTime cameOnlineAt;

    @Column(name = "offline_duration_s")
    private Integer offlineDurationS;

    @Column(name = "online_duration_s")
    private Integer onlineDurationS;

    @Column(name = "cause")
    private String cause;
}
