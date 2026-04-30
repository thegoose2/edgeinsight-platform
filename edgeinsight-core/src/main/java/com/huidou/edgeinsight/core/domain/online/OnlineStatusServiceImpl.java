package com.huidou.edgeinsight.core.domain.online;

import com.huidou.edgeinsight.common.event.DeviceStatusChangedEvent;
import com.huidou.edgeinsight.common.model.Device;
import com.huidou.edgeinsight.common.model.DeviceConnectivityLog;
import com.huidou.edgeinsight.common.model.DeviceStatus;
import com.huidou.edgeinsight.core.cache.ResolvedContext;
import com.huidou.edgeinsight.core.cache.ResolvedContextCache;
import com.huidou.edgeinsight.core.repository.jpa.JpaConnectivityLogRepository;
import com.huidou.edgeinsight.core.repository.jpa.JpaDeviceRepository;
import com.huidou.edgeinsight.core.repository.jpa.JpaDeviceStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Executors;

@Service
public class OnlineStatusServiceImpl implements OnlineStatusService {

    private static final Logger log = LoggerFactory.getLogger(OnlineStatusServiceImpl.class);

    private final JpaDeviceRepository deviceRepository;
    private final JpaDeviceStatusRepository deviceStatusRepository;
    private final JpaConnectivityLogRepository connectivityLogRepository;
    private final ResolvedContextCache resolvedContextCache;
    private final ApplicationEventPublisher eventPublisher;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> offlineTasks = new ConcurrentHashMap<>();

    public OnlineStatusServiceImpl(JpaDeviceRepository deviceRepository,
                                    JpaDeviceStatusRepository deviceStatusRepository,
                                    JpaConnectivityLogRepository connectivityLogRepository,
                                    ResolvedContextCache resolvedContextCache,
                                    ApplicationEventPublisher eventPublisher) {
        this.deviceRepository = deviceRepository;
        this.deviceStatusRepository = deviceStatusRepository;
        this.connectivityLogRepository = connectivityLogRepository;
        this.resolvedContextCache = resolvedContextCache;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void handleOnline(String connectId) {
        Device device = deviceRepository.findByConnectId(connectId).orElse(null);
        if (device == null) return;

        // Skip INACTIVE devices
        if ("INACTIVE".equals(device.getLifecycleStatus())) return;

        // Cancel any pending offline task
        ScheduledFuture<?> task = offlineTasks.remove(device.getId());
        if (task != null) {
            task.cancel(false);
        }

        // Update device_status
        DeviceStatus status = deviceStatusRepository.findByDeviceId(device.getId())
                .orElseGet(() -> {
                    DeviceStatus ns = new DeviceStatus();
                    ns.setDeviceId(device.getId());
                    return ns;
                });

        status.setOnlineStatus("ONLINE");
        status.setConnectedAt(LocalDateTime.now());
        deviceStatusRepository.save(status);

        // Update connectivity_log - find latest open record
        Optional<DeviceConnectivityLog> openLog = connectivityLogRepository
                .findTopByDeviceIdAndCameOnlineAtIsNullOrderByWentOfflineAtDesc(device.getId());

        if (openLog.isPresent()) {
            DeviceConnectivityLog log = openLog.get();
            log.setCameOnlineAt(LocalDateTime.now());
            if (log.getWentOfflineAt() != null) {
                long offlineSeconds = Duration.between(log.getWentOfflineAt(), log.getCameOnlineAt()).getSeconds();
                log.setOfflineDurationS((int) offlineSeconds);
            }
            connectivityLogRepository.save(log);
        }

        // Publish event
        eventPublisher.publishEvent(new DeviceStatusChangedEvent(this, device.getId(), "ONLINE"));
    }

    @Override
    @Transactional
    public void handleOffline(String connectId, String cause) {
        Device device = deviceRepository.findByConnectId(connectId).orElse(null);
        if (device == null) return;

        // Skip INACTIVE devices
        if ("INACTIVE".equals(device.getLifecycleStatus())) return;

        // Cancel any pending offline task
        ScheduledFuture<?> task = offlineTasks.remove(device.getId());
        if (task != null) {
            task.cancel(false);
        }

        // Calculate online duration
        DeviceStatus status = deviceStatusRepository.findByDeviceId(device.getId()).orElse(null);
        Integer onlineDurationS = null;
        if (status != null && status.getConnectedAt() != null) {
            onlineDurationS = (int) Duration.between(status.getConnectedAt(), LocalDateTime.now()).getSeconds();
        }

        // Update device_status
        if (status != null) {
            status.setOnlineStatus("OFFLINE");
            status.setDisconnectedAt(LocalDateTime.now());
            deviceStatusRepository.save(status);
        }

        // Insert connectivity_log
        DeviceConnectivityLog log = new DeviceConnectivityLog();
        log.setDeviceId(device.getId());
        log.setWentOfflineAt(LocalDateTime.now());
        log.setCameOnlineAt(null);
        log.setOnlineDurationS(onlineDurationS);
        log.setCause(cause);
        connectivityLogRepository.save(log);

        // Publish event
        eventPublisher.publishEvent(new DeviceStatusChangedEvent(this, device.getId(), "OFFLINE"));
    }

    @Override
    public String getOnlineStatus(Long deviceId) {
        return deviceStatusRepository.findByDeviceId(deviceId)
                .map(DeviceStatus::getOnlineStatus)
                .orElse("OFFLINE");
    }

    @Override
    public void processHeartbeat(String connectId) {
        Device device = deviceRepository.findByConnectId(connectId).orElse(null);
        if (device == null) return;

        if ("INACTIVE".equals(device.getLifecycleStatus())) return;

        // Update last_seen_at
        deviceStatusRepository.findByDeviceId(device.getId()).ifPresent(status -> {
            status.setLastSeenAt(LocalDateTime.now());
            deviceStatusRepository.save(status);
        });
    }

    public void scheduleOfflineTask(Long deviceId, int delaySeconds, String cause) {
        ScheduledFuture<?> existing = offlineTasks.get(deviceId);
        if (existing != null) {
            existing.cancel(false);
        }

        ScheduledFuture<?> future = scheduler.schedule(() -> {
            Device device = deviceRepository.findById(deviceId).orElse(null);
            if (device != null) {
                handleOffline(device.getConnectId(), cause);
            }
        }, delaySeconds, java.util.concurrent.TimeUnit.SECONDS);

        offlineTasks.put(deviceId, future);
    }

    public void cancelOfflineTask(Long deviceId) {
        ScheduledFuture<?> task = offlineTasks.remove(deviceId);
        if (task != null) {
            task.cancel(false);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // Recovery logic for service restart
        log.info("Running online status recovery for active devices...");
    }
}