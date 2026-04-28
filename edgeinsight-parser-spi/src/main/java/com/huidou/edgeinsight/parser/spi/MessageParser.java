package com.huidou.edgeinsight.parser.spi;

public interface MessageParser {

    ParsedMessage parse(String connectId, String rawPayload);

    String getParserId();
}
