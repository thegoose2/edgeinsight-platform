package com.huidou.edgeinsight.core.domain.telemetry;

import com.huidou.edgeinsight.common.dto.TelemetryLatestVO;
import com.huidou.edgeinsight.common.dto.TelemetryRecordVO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface TelemetryQueryService {

    TelemetryLatestVO getLatest(Long deviceId);

    Page<TelemetryRecordVO> queryHistory(Long deviceId, String pointCode,
                                          LocalDateTime from, LocalDateTime to,
                                          int pageNum, int pageSize);
}