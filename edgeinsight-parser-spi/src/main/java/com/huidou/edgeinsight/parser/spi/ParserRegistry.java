package com.huidou.edgeinsight.parser.spi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParserRegistry {

    private final Map<String, MessageParser> parsers = new ConcurrentHashMap<>();

    public void register(MessageParser parser) {
        parsers.put(parser.getParserId(), parser);
    }

    public MessageParser get(String parserId) {
        return parsers.get(parserId);
    }

    public boolean contains(String parserId) {
        return parsers.containsKey(parserId);
    }
}
