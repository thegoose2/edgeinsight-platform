package com.huidou.edgeinsight.core.config;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SystemConfigService {

    private Map<String, String> configCache;

    public String getConfig(String key) {
        return configCache.get(key);
    }

    public String getConfig(String key, String defaultValue) {
        return configCache.getOrDefault(key, defaultValue);
    }

    public void reload() {
    }
}
