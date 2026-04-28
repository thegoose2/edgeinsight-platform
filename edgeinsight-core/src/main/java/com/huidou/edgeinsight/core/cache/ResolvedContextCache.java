package com.huidou.edgeinsight.core.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResolvedContextCache {

    private final Map<String, ResolvedContext> cache = new ConcurrentHashMap<>();

    public void put(String connectId, ResolvedContext context) {
        cache.put(connectId, context);
    }

    public ResolvedContext get(String connectId) {
        return cache.get(connectId);
    }

    public void remove(String connectId) {
        cache.remove(connectId);
    }

    public void clearByDeviceType(Long deviceTypeId) {
        cache.entrySet().removeIf(entry -> deviceTypeId.equals(entry.getValue().getDeviceTypeId()));
    }

    public boolean contains(String connectId) {
        return cache.containsKey(connectId);
    }
}
