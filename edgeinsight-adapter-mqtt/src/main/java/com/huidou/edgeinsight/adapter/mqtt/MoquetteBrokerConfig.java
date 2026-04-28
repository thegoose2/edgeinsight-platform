package com.huidou.edgeinsight.adapter.mqtt;

import io.moquette.broker.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MoquetteBrokerConfig {

    @Bean
    public Server mqttBroker() {
        Server server = new Server();
        return server;
    }
}
