package com.huidou.edgeinsight.core.domain.device;

import com.huidou.edgeinsight.common.model.DeviceType;
import com.huidou.edgeinsight.common.model.DeviceTypePoint;

import java.util.List;

public interface DeviceTypeService {

    DeviceType save(DeviceType deviceType);

    DeviceType update(Long id, DeviceType deviceType);

    void delete(Long id);

    DeviceType findById(Long id);

    List<DeviceType> findAll();

    List<DeviceTypePoint> getPointTemplates(Long deviceTypeId);
}
