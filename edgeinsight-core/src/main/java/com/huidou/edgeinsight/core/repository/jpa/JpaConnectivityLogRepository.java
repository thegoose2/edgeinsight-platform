package com.huidou.edgeinsight.core.repository.jpa;

import com.huidou.edgeinsight.common.model.DeviceConnectivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaConnectivityLogRepository extends JpaRepository<DeviceConnectivityLog, Long> {

    List<DeviceConnectivityLog> findByDeviceId(Long deviceId);
}
