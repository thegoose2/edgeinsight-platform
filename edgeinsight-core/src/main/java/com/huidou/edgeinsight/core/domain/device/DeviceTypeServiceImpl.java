package com.huidou.edgeinsight.core.domain.device;

import com.huidou.edgeinsight.common.dto.*;
import com.huidou.edgeinsight.common.exception.BusinessException;
import com.huidou.edgeinsight.common.exception.NotFoundException;
import com.huidou.edgeinsight.common.model.DeviceType;
import com.huidou.edgeinsight.common.model.DeviceTypePoint;
import com.huidou.edgeinsight.core.repository.jpa.JpaDeviceRepository;
import com.huidou.edgeinsight.core.repository.jpa.JpaDeviceTypePointRepository;
import com.huidou.edgeinsight.core.repository.jpa.JpaDeviceTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceTypeServiceImpl implements DeviceTypeService {

    private final JpaDeviceTypeRepository deviceTypeRepository;
    private final JpaDeviceTypePointRepository pointRepository;
    private final JpaDeviceRepository deviceRepository;

    public DeviceTypeServiceImpl(JpaDeviceTypeRepository deviceTypeRepository,
                                  JpaDeviceTypePointRepository pointRepository,
                                  JpaDeviceRepository deviceRepository) {
        this.deviceTypeRepository = deviceTypeRepository;
        this.pointRepository = pointRepository;
        this.deviceRepository = deviceRepository;
    }

    @Override
    @Transactional
    public DeviceType save(DeviceType deviceType) {
        return deviceTypeRepository.save(deviceType);
    }

    @Override
    @Transactional
    public DeviceType update(Long id, DeviceType deviceType) {
        DeviceType existing = deviceTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("DeviceType not found: " + id));
        if (deviceType.getName() != null) existing.setName(deviceType.getName());
        if (deviceType.getDescription() != null) existing.setDescription(deviceType.getDescription());
        return deviceTypeRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        DeviceType dt = deviceTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("DeviceType not found: " + id));

        long deviceCount = deviceRepository.countByDeviceTypeId(id);
        if (deviceCount > 0) {
            throw new BusinessException("Cannot delete type with " + deviceCount + " associated devices");
        }

        pointRepository.deleteByDeviceTypeId(id);
        deviceTypeRepository.delete(dt);
    }

    @Override
    public DeviceType findById(Long id) {
        return deviceTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("DeviceType not found: " + id));
    }

    @Override
    public List<DeviceType> findAll() {
        return deviceTypeRepository.findAll();
    }

    @Override
    public List<DeviceTypePoint> getPointTemplates(Long deviceTypeId) {
        return pointRepository.findByDeviceTypeIdAndIsActive(deviceTypeId, 1);
    }

    public DeviceTypeVO toVO(DeviceType dt) {
        DeviceTypeVO vo = new DeviceTypeVO();
        vo.setId(dt.getId());
        vo.setTypeCode(dt.getTypeCode());
        vo.setName(dt.getName());
        vo.setProfileType(dt.getProfileType());
        vo.setDescription(dt.getDescription());
        vo.setDeviceCount(deviceRepository.countByDeviceTypeId(dt.getId()));

        List<DeviceTypePoint> points = pointRepository.findByDeviceTypeIdAndIsActive(dt.getId(), 1);
        vo.setPoints(points.stream().map(this::toPointVO).collect(Collectors.toList()));
        return vo;
    }

    private DeviceTypePointVO toPointVO(DeviceTypePoint p) {
        DeviceTypePointVO vo = new DeviceTypePointVO();
        vo.setId(p.getId());
        vo.setPointCode(p.getPointCode());
        vo.setName(p.getName());
        vo.setDataType(p.getDataType());
        vo.setUnit(p.getUnit());
        vo.setRangeMin(p.getRangeMin());
        vo.setRangeMax(p.getRangeMax());
        return vo;
    }

    @Transactional
    public DeviceTypePointVO addPoint(PointInsertReq req) {
        DeviceTypePoint point = new DeviceTypePoint();
        point.setDeviceTypeId(req.getDeviceTypeId());
        point.setPointCode(req.getPointCode());
        point.setName(req.getName());
        point.setDataType(req.getDataType());
        point.setUnit(req.getUnit());
        point.setRangeMin(req.getRangeMin());
        point.setRangeMax(req.getRangeMax());
        point.setIsActive(true);
        point = pointRepository.save(point);
        return toPointVO(point);
    }

    @Transactional
    public PointAffectPreviewVO previewUpdatePoint(Long id) {
        DeviceTypePoint point = pointRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Point not found: " + id));
        long deviceCount = deviceRepository.countByDeviceTypeId(point.getDeviceTypeId());
        PointAffectPreviewVO vo = new PointAffectPreviewVO();
        vo.setAffectedDeviceCount(deviceCount);
        vo.setMessage("This operation will affect " + deviceCount + " active devices");
        return vo;
    }

    @Transactional
    public DeviceTypePointVO updatePoint(PointUpdateReq req, boolean confirmed) {
        DeviceTypePoint point = pointRepository.findById(req.getId())
                .orElseThrow(() -> new NotFoundException("Point not found: " + req.getId()));

        if (!confirmed) {
            return previewUpdatePoint(req.getId());
        }

        if (req.getName() != null) point.setName(req.getName());
        if (req.getDataType() != null) point.setDataType(req.getDataType());
        if (req.getUnit() != null) point.setUnit(req.getUnit());
        if (req.getRangeMin() != null) point.setRangeMin(req.getRangeMin());
        if (req.getRangeMax() != null) point.setRangeMax(req.getRangeMax());

        return toPointVO(pointRepository.save(point));
    }

    @Transactional
    public PointAffectPreviewVO previewDeletePoint(Long id) {
        DeviceTypePoint point = pointRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Point not found: " + id));
        long deviceCount = deviceRepository.countByDeviceTypeId(point.getDeviceTypeId());
        PointAffectPreviewVO vo = new PointAffectPreviewVO();
        vo.setAffectedDeviceCount(deviceCount);
        vo.setMessage("This operation will affect " + deviceCount + " active devices");
        return vo;
    }

    @Transactional
    public void deletePoint(Long id, boolean confirmed) {
        DeviceTypePoint point = pointRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Point not found: " + id));

        if (!confirmed) {
            previewDeletePoint(id);
            return;
        }

        point.setIsActive(false);
        pointRepository.save(point);
    }
}