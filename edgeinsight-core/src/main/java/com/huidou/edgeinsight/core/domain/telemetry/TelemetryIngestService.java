package com.huidou.edgeinsight.core.domain.telemetry;

import com.huidou.edgeinsight.parser.spi.ParsedMessage;

public interface TelemetryIngestService {

    void ingest(String connectId, ParsedMessage message);

    void ingestTelemetry(Long deviceId, String pointCode, Object value, Long timestamp);
}
