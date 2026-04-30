package com.huidou.edgeinsight.core.domain.device;

import com.huidou.edgeinsight.common.dto.*;
import com.huidou.edgeinsight.common.exception.BusinessException;
import com.huidou.edgeinsight.common.exception.NotFoundException;
import com.huidou.edgeinsight.common.model.*;
import com.huidou.edgeinsight.core.cache.ResolvedContext;
import com.huidou.edgeinsight.core.cache.ResolvedContextCache;
import com.huidou.edgeinsight.core.repository.jpa.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final JpaDeviceRepository deviceRepository;
    private final JpaDeviceTypeRepository deviceTypeRepository;
    private final JpaDeviceTypePointRepository pointRepository;
    private final JpaDeviceStatusRepository deviceStatusRepository;
    private final JpaProtocolProfileRepository protocolProfileRepository;
    private final ResolvedContextCache resolvedContextCache;

    public DeviceServiceImpl(JpaDeviceRepository deviceRepository,
                             JpaDeviceTypeRepository deviceTypeRepository,
                             JpaDeviceTypePointRepository pointRepository,
                             JpaDeviceStatusRepository deviceStatusRepository,
                             JpaProtocolProfileRepository protocolProfileRepository,
                             ResolvedContextCache resolvedContextCache) {
        this.deviceRepository = deviceRepository;
        this.deviceTypeRepository = deviceTypeRepository;
        this.pointRepository = pointRepository;
        this.deviceStatusRepository = deviceStatusRepository;
        this.protocolProfileRepository = protocolProfileRepository;
        this.resolvedContextCache = resolvedContextCache;
    }

    @Override
    @Transactional
    public Device save(Device device) {
        Device saved = deviceRepository.save(device);

        // Initialize DeviceStatus
        DeviceStatus status = new DeviceStatus();
        status.setDeviceId(saved.getId());
        status.setOnlineStatus("OFFLINE");
        deviceStatusRepository.save(status);

        // Initialize ResolvedContext
        initializeResolvedContext(saved);

        return saved;
    }

    @Override
    @Transactional
    public Device update(Long id, Device device) {
        Device existing = deviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Device not found: " + id));

        boolean contextChanged = false;

        if (device.getName() != null) existing.setName(device.getName());
        if (device.getLocation() != null) existing.setLocation(device.getLocation());
        if (device.getConnectId() != null && !device.getConnectId().equals(existing.getConnectId())) {
            resolvedContextCache.remove(existing.getConnectId());
            existing.setConnectId(device.getConnectId());
            contextChanged = true;
        }
        if (device.getDeviceTypeId() != null && !device.getDeviceTypeId().equals(existing.getDeviceTypeId())) {
            if (existing.getConnectId() != null) {
                resolvedContextCache.remove(existing.getConnectId());
            }
            existing.setDeviceTypeId(device.getDeviceTypeId());
            contextChanged = true;
        }

        Device saved = deviceRepository.save(existing);

        if (contextChanged && saved.getConnectId() != null) {
            initializeResolvedContext(saved);
        }

        return saved;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Device not found: " + id));

        if (device.getConnectId() != null) {
            resolvedContextCache.remove(device.getConnectId());
        }
        deviceRepository.delete(device);
    }

    @Override
    public Device findById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Device not found: " + id));
    }

    @Override
    public List<Device> findAll() {
        return deviceRepository.findAll();
    }

    @Override
    public Device findByConnectId(String connectId) {
        return deviceRepository.findByConnectId(connectId)
                .orElseThrow(() -> new NotFoundException("Device not found with connectId: " + connectId));
    }

    @Override
    @Transactional
    public void importDevices(List<Device> devices) {
        // Implemented in controller with batch processing
    }

    @Transactional
    public void updateLifecycle(Long id, String status) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Device not found: " + id));

        device.setLifecycleStatus(status);
        deviceRepository.save(device);

        if (device.getConnectId() != null) {
            resolvedContextCache.remove(device.getConnectId());
        }
    }

    public DeviceVO toVO(Device device) {
        DeviceVO vo = new DeviceVO();
        vo.setId(device.getId());
        vo.setDeviceCode(device.getDeviceCode());
        vo.setName(device.getName());
        vo.setLocation(device.getLocation());
        vo.setConnectId(device.getConnectId());
        vo.setDeviceTypeId(device.getDeviceTypeId());
        vo.setLifecycleStatus(device.getLifecycleStatus());

        if (device.getDeviceTypeId() != null) {
            DeviceType dt = deviceTypeRepository.findById(device.getDeviceTypeId()).orElse(null);
            if (dt != null) {
                vo.setDeviceTypeName(dt.getName());
                vo.setProfileType(dt.getProfileType());
            }
        }

        // Online status
        DeviceStatus status = deviceStatusRepository.findByDeviceId(device.getId()).orElse(null);
        if (status != null) {
            vo.setOnlineStatus(status.getOnlineStatus());
            vo.setConnectedAt(status.getConnectedAt());
            vo.setDisconnectedAt(status.getDisconnectedAt());
            vo.setLastSeenAt(status.getLastSeenAt());
        }

        // Points
        if (device.getDeviceTypeId() != null) {
            List<DeviceTypePoint> points = pointRepository.findByDeviceTypeIdAndIsActive(device.getDeviceTypeId(), 1);
            vo.setPoints(points.stream().map(this::toPointVO).collect(Collectors.toList()));
        }

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

    private void initializeResolvedContext(Device device) {
        if (device.getDeviceTypeId() == null || device.getConnectId() == null) return;

        DeviceType deviceType = deviceTypeRepository.findById(device.getDeviceTypeId()).orElse(null);
        if (deviceType == null) return;

        String profileType = deviceType.getProfileType();
        ProtocolProfile profile = profileType != null ? protocolProfileRepository.findByProfileType(profileType).orElse(null) : null;

        List<String> validPointCodes = pointRepository.findByDeviceTypeIdAndIsActive(device.getDeviceTypeId(), 1)
                .stream().map(DeviceTypePoint::getPointCode).collect(Collectors.toList());

        ResolvedContext ctx = new ResolvedContext();
        ctx.setDeviceId(device.getId());
        ctx.setDeviceTypeId(device.getDeviceTypeId());
        ctx.setConnectId(device.getConnectId());
        ctx.setParserId(profile != null ? profile.getParserId() : null);
        ctx.setProtocol(profile != null ? profile.getProtocol() : null);
        ctx.setValidPointCodes(validPointCodes);
        ctx.setLifecycleStatus(device.getLifecycleStatus() != null ? device.getLifecycleStatus() : "ACTIVE");

        resolvedContextCache.put(device.getConnectId(), ctx);
    }
}