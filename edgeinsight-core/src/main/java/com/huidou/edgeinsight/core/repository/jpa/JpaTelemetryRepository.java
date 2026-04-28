package com.huidou.edgeinsight.core.repository.jpa;

import com.huidou.edgeinsight.common.model.TelemetryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTelemetryRepository extends JpaRepository<TelemetryRecord, Long> {
}
