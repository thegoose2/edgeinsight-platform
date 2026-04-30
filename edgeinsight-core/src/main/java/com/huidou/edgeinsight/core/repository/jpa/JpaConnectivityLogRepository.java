package com.huidou.edgeinsight.core.repository.jpa;

import com.huidou.edgeinsight.common.model.DeviceConnectivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaConnectivityLogRepository extends JpaRepository<DeviceConnectivityLog, Long> {

    List<DeviceConnectivityLog> findByDeviceId(Long deviceId);

    Optional<DeviceConnectivityLog> findTopByDeviceIdAndCameOnlineAtIsNullOrderByWentOfflineAtDesc(Long deviceId);

    Page<DeviceConnectivityLog> findByDeviceIdAndWentOfflineAtBetween(
            Long deviceId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    @Query("SELECT SUM(c.onlineDurationS) FROM DeviceConnectivityLog c WHERE c.deviceId = :deviceId AND c.wentOfflineAt BETWEEN :from AND :to")
    Long sumOnlineDurationS(@Param("deviceId") Long deviceId,
                            @Param("from") LocalDateTime from,
                            @Param("to") LocalDateTime to);

    @Query("SELECT SUM(c.offlineDurationS) FROM DeviceConnectivityLog c WHERE c.deviceId = :deviceId AND c.wentOfflineAt BETWEEN :from AND :to")
    Long sumOfflineDurationS(@Param("deviceId") Long deviceId,
                             @Param("from") LocalDateTime from,
                             @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(c) FROM DeviceConnectivityLog c WHERE c.deviceId = :deviceId AND c.wentOfflineAt BETWEEN :from AND :to")
    long countByDeviceIdAndPeriod(@Param("deviceId") Long deviceId,
                                  @Param("from") LocalDateTime from,
                                  @Param("to") LocalDateTime to);

    @Query("SELECT AVG(c.offlineDurationS) FROM DeviceConnectivityLog c WHERE c.deviceId = :deviceId AND c.wentOfflineAt BETWEEN :from AND :to AND c.offlineDurationS IS NOT NULL")
    Double avgOfflineDurationS(@Param("deviceId") Long deviceId,
                                @Param("from") LocalDateTime from,
                                @Param("to") LocalDateTime to);

    @Query("SELECT MAX(c.offlineDurationS) FROM DeviceConnectivityLog c WHERE c.deviceId = :deviceId AND c.wentOfflineAt BETWEEN :from AND :to AND c.offlineDurationS IS NOT NULL")
    Integer maxOfflineDurationS(@Param("deviceId") Long deviceId,
                                @Param("from") LocalDateTime from,
                                @Param("to") LocalDateTime to);
}