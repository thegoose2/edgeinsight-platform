package com.huidou.edgeinsight.common.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "device_status")
public class DeviceStatus {

    @Id
    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "online_status")
    private String onlineStatus;

    @Column(name = "connected_at")
    private LocalDateTime connectedAt;

    @Column(name = "disconnected_at")
    private LocalDateTime disconnectedAt;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
