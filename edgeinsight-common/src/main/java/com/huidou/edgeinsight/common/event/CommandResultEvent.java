package com.huidou.edgeinsight.common.event;

import org.springframework.context.ApplicationEvent;

public class CommandResultEvent extends ApplicationEvent {

    private final String connectId;
    private final String commandId;
    private final int statusCode;
    private final String result;

    public CommandResultEvent(Object source, String connectId, String commandId, int statusCode, String result) {
        super(source);
        this.connectId = connectId;
        this.commandId = commandId;
        this.statusCode = statusCode;
        this.result = result;
    }

    public String getConnectId() {
        return connectId;
    }

    public String getCommandId() {
        return commandId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResult() {
        return result;
    }
}
