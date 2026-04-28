package com.huidou.edgeinsight.core.domain.device;

public interface DeviceRegistrationService {

    void registerDevice(String connectId);

    void unregisterDevice(String connectId);
}
