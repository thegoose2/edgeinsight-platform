package com.huidou.edgeinsight.core.config;

import com.huidou.edgeinsight.common.model.SystemConfig;
import com.huidou.edgeinsight.core.repository.jpa.JpaSystemConfigRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class SystemConfigService {

    private final JpaSystemConfigRepository systemConfigRepository;
    private Map<String, String> configCache = new HashMap<>();

    public SystemConfigService(JpaSystemConfigRepository systemConfigRepository) {
        this.systemConfigRepository = systemConfigRepository;
    }

    @PostConstruct
    public void init() {
        reload();
    }

    public String getConfig(String key) {
        return configCache.get(key);
    }

    public String getConfig(String key, String defaultValue) {
        return configCache.getOrDefault(key, defaultValue);
    }

    public void reload() {
        Map<String, String> newCache = new HashMap<>();
        for (SystemConfig config : systemConfigRepository.findAll()) {
            newCache.put(config.getConfigKey(), config.getConfigValue());
        }
        this.configCache = newCache;
    }
}
