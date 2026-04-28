package com.huidou.edgeinsight.core.repository.spi;

import com.huidou.edgeinsight.common.model.Device;

import java.util.Optional;

public interface DeviceRepository {

    Device save(Device device);

    Optional<Device> findById(Long id);

    Optional<Device> findByConnectId(String connectId);

    java.util.List<Device> findAll();

    void deleteById(Long id);
}
