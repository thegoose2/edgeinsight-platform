package com.huidou.edgeinsight.core.repository.jpa;

import com.huidou.edgeinsight.common.model.DeviceTypePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaDeviceTypePointRepository extends JpaRepository<DeviceTypePoint, Long> {

    List<DeviceTypePoint> findByDeviceTypeIdAndIsActive(Long deviceTypeId, Integer isActive);

    Optional<DeviceTypePoint> findByDeviceTypeIdAndPointCode(Long deviceTypeId, String pointCode);

    void deleteByDeviceTypeId(Long deviceTypeId);
}