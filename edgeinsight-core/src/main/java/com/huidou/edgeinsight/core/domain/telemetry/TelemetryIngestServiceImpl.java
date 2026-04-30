package com.huidou.edgeinsight.core.domain.telemetry;

import com.huidou.edgeinsight.common.event.AlarmTriggeredEvent;
import com.huidou.edgeinsight.common.event.DeviceStatusChangedEvent;
import com.huidou.edgeinsight.common.event.TelemetryReceivedEvent;
import com.huidou.edgeinsight.common.exception.BusinessException;
import com.huidou.edgeinsight.common.model.*;
import com.huidou.edgeinsight.core.cache.ResolvedContext;
import com.huidou.edgeinsight.core.cache.ResolvedContextCache;
import com.huidou.edgeinsight.core.domain.online.OnlineStatusService;
import com.huidou.edgeinsight.core.repository.jpa.JpaDeviceRepository;
import com.huidou.edgeinsight.core.repository.jpa.JpaDeviceStatusRepository;
import com.huidou.edgeinsight.core.repository.jpa.JpaTelemetryRepository;
import com.huidou.edgeinsight.parser.spi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TelemetryIngestServiceImpl implements TelemetryIngestService {

    private static final Logger log = LoggerFactory.getLogger(TelemetryIngestServiceImpl.class);

    private final JpaTelemetryRepository telemetryRepository;
    private final JpaDeviceRepository deviceRepository;
    private final JpaDeviceStatusRepository deviceStatusRepository;
    private final ResolvedContextCache resolvedContextCache;
    private final ApplicationEventPublisher eventPublisher;
    private final OnlineStatusService onlineStatusService;

    public TelemetryIngestServiceImpl(JpaTelemetryRepository telemetryRepository,
                                      JpaDeviceRepository deviceRepository,
                                      JpaDeviceStatusRepository deviceStatusRepository,
                                      ResolvedContextCache resolvedContextCache,
                                      ApplicationEventPublisher eventPublisher,
                                      OnlineStatusService onlineStatusService) {
        this.telemetryRepository = telemetryRepository;
        this.deviceRepository = deviceRepository;
        this.deviceStatusRepository = deviceStatusRepository;
        this.resolvedContextCache = resolvedContextCache;
        this.eventPublisher = eventPublisher;
        this.onlineStatusService = onlineStatusService;
    }

    @Override
    @Transactional
    public void ingest(String connectId, ParsedMessage message) {
        ResolvedContext ctx = resolvedContextCache.get(connectId);
        if (ctx == null) {
            log.warn("No ResolvedContext for connectId: {}", connectId);
            return;
        }

        // INACTIVE device - discard
        if ("INACTIVE".equals(ctx.getLifecycleStatus())) {
            log.debug("Discarding message for INACTIVE device: {}", connectId);
            return;
        }

        if (message instanceof TelemetryMessage) {
            ingestTelemetryMessage(ctx, (TelemetryMessage) message);
        } else if (message instanceof HeartbeatMessage) {
            processHeartbeat(ctx, (HeartbeatMessage) message);
        } else if (message instanceof StatusMessage) {
            processStatusMessage(ctx, (StatusMessage) message);
        } else if (message instanceof DeviceAlarmMessage) {
            processAlarmMessage(ctx, (DeviceAlarmMessage) message);
        }
    }

    @Override
    @Transactional
    public void ingestTelemetry(Long deviceId, String pointCode, Object value, Long timestamp) {
        LocalDateTime ts = LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.ofHours(8));

        Double numValue = null;
        String strValue = null;
        if (value instanceof Number) {
            numValue = ((Number) value).doubleValue();
        } else {
            strValue = String.valueOf(value);
        }

        // Insert record
        TelemetryRecord record = new TelemetryRecord();
        record.setDeviceId(deviceId);
        record.setPointCode(pointCode);
        record.setTs(ts);
        record.setNumValue(numValue);
        record.setStrValue(strValue);
        telemetryRepository.save(record);

        // Upsert latest
        telemetryRepository.upsertLatest(deviceId, pointCode, numValue, strValue, ts);

        // Update last_seen_at
        deviceStatusRepository.findByDeviceId(deviceId).ifPresent(status -> {
            status.setLastSeenAt(LocalDateTime.now());
            deviceStatusRepository.save(status);
        });
    }

    private void ingestTelemetryMessage(ResolvedContext ctx, TelemetryMessage message) {
        String pointCode = message.getPointCode();

        // Validate point code against whitelist
        if (ctx.getValidPointCodes() != null && !ctx.getValidPointCodes().contains(pointCode)) {
            log.warn("Unknown point code {} for device {}, discarding", pointCode, ctx.getDeviceId());
            return;
        }

        Long deviceId = ctx.getDeviceId();
        Object value = message.getValue();
        LocalDateTime ts = message.getTimestamp() != null
                ? LocalDateTime.ofEpochSecond(message.getTimestamp() / 1000, 0, ZoneOffset.ofHours(8))
                : LocalDateTime.now();

        Double numValue = null;
        String strValue = null;
        if (value instanceof Number) {
            numValue = ((Number) value).doubleValue();
        } else {
            strValue = String.valueOf(value);
        }

        // Insert telemetry record
        TelemetryRecord record = new TelemetryRecord();
        record.setDeviceId(deviceId);
        record.setPointCode(pointCode);
        record.setTs(ts);
        record.setNumValue(numValue);
        record.setStrValue(strValue);
        telemetryRepository.save(record);

        // Upsert latest
        telemetryRepository.upsertLatest(deviceId, pointCode, numValue, strValue, ts);

        // Update last_seen_at
        deviceStatusRepository.findByDeviceId(deviceId).ifPresent(status -> {
            status.setLastSeenAt(LocalDateTime.now());
            deviceStatusRepository.save(status);
        });

        // Publish event
        eventPublisher.publishEvent(new TelemetryReceivedEvent(this, deviceId, pointCode, value, ts));
    }

    private void processHeartbeat(ResolvedContext ctx, HeartbeatMessage message) {
        onlineStatusService.processHeartbeat(ctx.getConnectId());
    }

    private void processStatusMessage(ResolvedContext ctx, StatusMessage message) {
        String status = message.getOnlineStatus();
        if ("OFFLINE".equals(status)) {
            onlineStatusService.handleOffline(ctx.getConnectId(), "GATEWAY_REPORTED");
        } else if ("ONLINE".equals(status)) {
            onlineStatusService.handleOnline(ctx.getConnectId());
        }
    }

    private void processAlarmMessage(ResolvedContext ctx, DeviceAlarmMessage message) {
        eventPublisher.publishEvent(new AlarmTriggeredEvent(this, ctx.getDeviceId(),
                message.getAlarmCode(), message.getAlarmDesc(), message.getTimestamp()));
    }
}