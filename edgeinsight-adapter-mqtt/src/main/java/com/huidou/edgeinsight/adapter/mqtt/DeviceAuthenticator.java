package com.huidou.edgeinsight.adapter.mqtt;

import org.springframework.stereotype.Component;

@Component
public class DeviceAuthenticator {

    public boolean authenticate(String connectId, String username, String password) {
        return false;
    }

    public boolean isWhitelisted(String connectId) {
        return false;
    }
}
