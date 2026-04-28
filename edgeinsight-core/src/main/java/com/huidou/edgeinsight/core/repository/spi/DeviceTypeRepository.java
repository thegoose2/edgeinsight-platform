package com.huidou.edgeinsight.core.repository.spi;

import com.huidou.edgeinsight.common.model.DeviceType;

public interface DeviceTypeRepository {

    DeviceType save(DeviceType deviceType);

    java.util.Optional<DeviceType> findById(Long id);

    java.util.List<DeviceType> findAll();

    void deleteById(Long id);
}
