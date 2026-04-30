package com.huidou.edgeinsight.core.domain.telemetry;

import com.huidou.edgeinsight.common.dto.*;
import com.huidou.edgeinsight.common.model.DeviceTypePoint;
import com.huidou.edgeinsight.common.model.TelemetryLatest;
import com.huidou.edgeinsight.common.model.TelemetryRecord;
import com.huidou.edgeinsight.core.repository.jpa.JpaDeviceRepository;
import com.huidou.edgeinsight.core.repository.jpa.JpaDeviceTypePointRepository;
import com.huidou.edgeinsight.core.repository.jpa.JpaTelemetryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TelemetryQueryServiceImpl implements TelemetryQueryService {

    private final JpaTelemetryRepository telemetryRepository;
    private final JpaDeviceTypePointRepository pointRepository;
    private final JpaDeviceRepository deviceRepository;

    public TelemetryQueryServiceImpl(JpaTelemetryRepository telemetryRepository,
                                      JpaDeviceTypePointRepository pointRepository,
                                      JpaDeviceRepository deviceRepository) {
        this.telemetryRepository = telemetryRepository;
        this.pointRepository = pointRepository;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public TelemetryLatestVO getLatest(Long deviceId) {
        List<TelemetryLatest> latestList = telemetryRepository.findByDeviceId(deviceId);

        // Get point metadata
        var device = deviceRepository.findById(deviceId).orElse(null);
        Map<String, DeviceTypePoint> pointMap = Map.of();
        if (device != null) {
            List<DeviceTypePoint> points = pointRepository.findByDeviceTypeIdAndIsActive(device.getDeviceTypeId(), 1);
            pointMap = points.stream().collect(Collectors.toMap(DeviceTypePoint::getPointCode, p -> p));
        }

        TelemetryLatestVO vo = new TelemetryLatestVO();
        vo.setDeviceId(deviceId);

        List<TelemetryLatestVO.TelemetryPointValueVO> values = new ArrayList<>();
        for (TelemetryLatest latest : latestList) {
            TelemetryLatestVO.TelemetryPointValueVO pv = new TelemetryLatestVO.TelemetryPointValueVO();
            pv.setPointCode(latest.getPointCode());
            pv.setValue(latest.getNumValue() != null ? latest.getNumValue() : latest.getStrValue());
            pv.setTs(latest.getTs());

            DeviceTypePoint pointMeta = pointMap.get(latest.getPointCode());
            if (pointMeta != null) {
                pv.setName(pointMeta.getName());
                pv.setUnit(pointMeta.getUnit());
            }

            values.add(pv);
        }
        vo.setValues(values);
        return vo;
    }

    @Override
    public Page<TelemetryRecordVO> queryHistory(Long deviceId, String pointCode,
                                                  java.time.LocalDateTime from,
                                                  java.time.LocalDateTime to,
                                                  int pageNum, int pageSize) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(pageNum - 1, pageSize);
        Page<TelemetryRecord> page = telemetryRepository.findByDeviceIdAndPointCodeAndTsBetweenOrderByTsAsc(
                deviceId, pointCode, from, to, pageable);

        return page.map(record -> {
            TelemetryRecordVO vo = new TelemetryRecordVO();
            vo.setTs(record.getTs());
            vo.setValue(record.getNumValue() != null ? record.getNumValue() : record.getStrValue());
            return vo;
        });
    }
}