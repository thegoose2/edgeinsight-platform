package com.huidou.edgeinsight.core.repository.jpa;

import com.huidou.edgeinsight.common.model.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDeviceTypeRepository extends JpaRepository<DeviceType, Long> {
}
