package com.huidou.edgeinsight.core.repository.jpa;

import com.huidou.edgeinsight.common.model.TelemetryLatest;
import com.huidou.edgeinsight.common.model.TelemetryRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaTelemetryRepository extends JpaRepository<TelemetryRecord, Long> {

    Optional<TelemetryLatest> findByDeviceIdAndPointCode(Long deviceId, String pointCode);

    List<TelemetryLatest> findByDeviceId(Long deviceId);

    @Modifying
    @Query(value = "INSERT INTO telemetry_latest (device_id, point_code, num_value, str_value, ts, updated_at) " +
                   "VALUES (:deviceId, :pointCode, :numValue, :strValue, :ts, NOW()) " +
                   "ON DUPLICATE KEY UPDATE num_value = IF(:ts > ts, :numValue, num_value), " +
                   "str_value = IF(:ts > ts, :strValue, str_value), " +
                   "ts = IF(:ts > ts, :ts, ts), updated_at = NOW()",
           nativeQuery = true)
    void upsertLatest(@Param("deviceId") Long deviceId,
                      @Param("pointCode") String pointCode,
                      @Param("numValue") Double numValue,
                      @Param("strValue") String strValue,
                      @Param("ts") LocalDateTime ts);

    Page<TelemetryRecord> findByDeviceIdAndPointCodeAndTsBetweenOrderByTsAsc(
            Long deviceId, String pointCode, LocalDateTime from, LocalDateTime to, Pageable pageable);
}