package org.dinky.ws;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class WsSendEvent {
    private GlobalWebSocketTopic topic;
    private Map<String, ?> paramsAndData;
}
