package org.dinky.ws;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalWebSocketTopic {
    JVM_INFO("jvmInfo"),
    ;
    private final String topic;
}
