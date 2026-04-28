package com.huidou.edgeinsight.adapter.spi;

public interface ProtocolAdapter {

    void start();

    void stop();

    String getProtocol();
}
