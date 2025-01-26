package org.dinky.ws.handler;

import org.dinky.ws.GlobalWebSocketTopic;

import java.util.Map;
import java.util.Set;

public interface WsMessageEventHandler {
     String NONE_PARAMS = "none-params";

    Map<String, ?> autoMessageSend();

    /**
     * First subscription
     * @return data
     */
    Map<String, Object> firstSubscribe(Set<String> allParams);

    void run();

    GlobalWebSocketTopic getTopic();
}
