package com.huidou.edgeinsight.core.repository.jpa;

import com.huidou.edgeinsight.common.model.SystemConfig;
import com.huidou.edgeinsight.core.repository.spi.SystemConfigRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaSystemConfigRepository extends JpaRepository<SystemConfig, String>, SystemConfigRepository {

    Optional<SystemConfig> findByConfigKey(String configKey);
}
