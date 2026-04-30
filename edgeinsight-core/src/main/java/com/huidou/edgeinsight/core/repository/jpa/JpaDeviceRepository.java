package com.huidou.edgeinsight.core.repository.jpa;

import com.huidou.edgeinsight.common.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaDeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByConnectId(String connectId);

    long countByDeviceTypeId(Long deviceTypeId);
}
