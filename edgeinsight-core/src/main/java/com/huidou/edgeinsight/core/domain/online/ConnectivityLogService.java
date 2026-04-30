package com.huidou.edgeinsight.core.domain.online;

import com.huidou.edgeinsight.common.dto.ConnectivityLogVO;
import com.huidou.edgeinsight.common.dto.ConnectivityStatsVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ConnectivityLogService {

    void recordConnect(Long deviceId, String connectId);

    void recordDisconnect(Long deviceId, String connectId);

    Page<ConnectivityLogVO> getLogs(Long deviceId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<ConnectivityLogVO> getLogs(Long deviceId);

    ConnectivityStatsVO getStats(Long deviceId, LocalDateTime from, LocalDateTime to);
}