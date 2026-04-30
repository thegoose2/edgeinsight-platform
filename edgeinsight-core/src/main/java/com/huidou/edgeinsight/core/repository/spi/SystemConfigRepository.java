package com.huidou.edgeinsight.core.repository.spi;

import com.huidou.edgeinsight.common.model.SystemConfig;
import java.util.Optional;

public interface SystemConfigRepository {

    Optional<SystemConfig> findByConfigKey(String configKey);

    SystemConfig save(SystemConfig systemConfig);
}
