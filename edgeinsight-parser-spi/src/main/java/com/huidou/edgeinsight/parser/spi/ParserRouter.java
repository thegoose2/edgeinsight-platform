package com.huidou.edgeinsight.parser.spi;

public class ParserRouter {

    private final ParserRegistry registry;

    public ParserRouter(ParserRegistry registry) {
        this.registry = registry;
    }

    public ParsedMessage route(String connectId, String rawPayload, String parserId) {
        MessageParser parser = registry.get(parserId);
        if (parser == null) {
            throw new IllegalArgumentException("Parser not found: " + parserId);
        }
        return parser.parse(connectId, rawPayload);
    }
}
