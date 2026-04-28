package com.huidou.edgeinsight.adapter.mqtt.impl.mqtt;

import com.huidou.edgeinsight.parser.spi.MessageParser;
import com.huidou.edgeinsight.parser.spi.ParsedMessage;
import com.huidou.edgeinsight.parser.spi.TelemetryMessage;
import org.springframework.stereotype.Component;

@Component
public class DefaultJsonParser implements MessageParser {

    @Override
    public ParsedMessage parse(String connectId, String rawPayload) {
        TelemetryMessage message = new TelemetryMessage();
        return message;
    }

    @Override
    public String getParserId() {
        return "default_json";
    }
}
