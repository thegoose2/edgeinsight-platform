package com.huidou.edgeinsight.core.domain.online;

import com.huidou.edgeinsight.common.dto.ConnectivityLogVO;
import com.huidou.edgeinsight.common.dto.ConnectivityStatsVO;
import com.huidou.edgeinsight.common.exception.NotFoundException;
import com.huidou.edgeinsight.common.model.Device;
import com.huidou.edgeinsight.common.model.DeviceConnectivityLog;
import com.huidou.edgeinsight.core.repository.jpa.JpaConnectivityLogRepository;
import com.huidou.edgeinsight.core.repository.jpa.JpaDeviceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConnectivityLogServiceImpl implements ConnectivityLogService {

    private final JpaConnectivityLogRepository logRepository;
    private final JpaDeviceRepository deviceRepository;

    public ConnectivityLogServiceImpl(JpaConnectivityLogRepository logRepository,
                                       JpaDeviceRepository deviceRepository) {
        this.logRepository = logRepository;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public void recordConnect(Long deviceId, String connectId) {
        // Called by OnlineStatusService - logs already managed there
    }

    @Override
    public void recordDisconnect(Long deviceId, String connectId) {
        // Called by OnlineStatusService - logs already managed there
    }

    @Override
    public Page<ConnectivityLogVO> getLogs(Long deviceId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Page<DeviceConnectivityLog> page = logRepository.findByDeviceIdAndWentOfflineAtBetween(
                deviceId, from, to, pageable);

        return page.map(log -> {
            ConnectivityLogVO vo = new ConnectivityLogVO();
            vo.setWentOfflineAt(log.getWentOfflineAt());
            vo.setCameOnlineAt(log.getCameOnlineAt());
            vo.setOfflineDurationS(log.getOfflineDurationS());
            vo.setOnlineDurationS(log.getOnlineDurationS());
            vo.setCause(log.getCause());
            return vo;
        });
    }

    @Override
    public List<ConnectivityLogVO> getLogs(Long deviceId) {
        return logRepository.findByDeviceId(deviceId).stream()
                .map(log -> {
                    ConnectivityLogVO vo = new ConnectivityLogVO();
                    vo.setWentOfflineAt(log.getWentOfflineAt());
                    vo.setCameOnlineAt(log.getCameOnlineAt());
                    vo.setOfflineDurationS(log.getOfflineDurationS());
                    vo.setOnlineDurationS(log.getOnlineDurationS());
                    vo.setCause(log.getCause());
                    return vo;
                }).collect(Collectors.toList());
    }

    public ConnectivityStatsVO getStats(Long deviceId, LocalDateTime from, LocalDateTime to) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new NotFoundException("Device not found: " + deviceId));

        long periodSeconds = Duration.between(from, to).getSeconds();
        Long onlineTotalS = logRepository.sumOnlineDurationS(deviceId, from, to);
        Long offlineTotalS = logRepository.sumOfflineDurationS(deviceId, from, to);
        long offlineCount = logRepository.countByDeviceIdAndPeriod(deviceId, from, to);
        Double avgOfflineS = logRepository.avgOfflineDurationS(deviceId, from, to);
        Integer maxOfflineS = logRepository.maxOfflineDurationS(deviceId, from, to);

        if (onlineTotalS == null) onlineTotalS = 0L;
        if (offlineTotalS == null) offlineTotalS = 0L;
        if (avgOfflineS == null) avgOfflineS = 0.0;
        if (maxOfflineS == null) maxOfflineS = 0;

        double availabilityRate = periodSeconds > 0
                ? (double) onlineTotalS / periodSeconds : 0.0;

        ConnectivityStatsVO vo = new ConnectivityStatsVO();
        vo.setDeviceId(deviceId);
        vo.setDeviceName(device.getName());
        vo.setPeriodSeconds(periodSeconds);
        vo.setOnlineTotalS(onlineTotalS);
        vo.setOfflineTotalS(offlineTotalS);
        vo.setOfflineCount(offlineCount);
        vo.setAvgOfflineS(avgOfflineS);
        vo.setMaxOfflineS(maxOfflineS);
        vo.setAvailabilityRate(Math.round(availabilityRate * 10000.0) / 10000.0);
        return vo;
    }
}