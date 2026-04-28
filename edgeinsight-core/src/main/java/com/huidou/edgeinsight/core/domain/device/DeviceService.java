package com.huidou.edgeinsight.core.domain.device;

import com.huidou.edgeinsight.common.model.Device;

import java.util.List;

public interface DeviceService {

    Device save(Device device);

    Device update(Long id, Device device);

    void delete(Long id);

    Device findById(Long id);

    List<Device> findAll();

    Device findByConnectId(String connectId);

    void importDevices(List<Device> devices);
}
