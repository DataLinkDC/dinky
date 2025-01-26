package org.dinky.ws.handler;

import org.dinky.ws.GlobalWebSocketTopic;
import org.dinky.ws.WsSendEvent;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Resource;
import java.util.Map;

public abstract class WsBaseMessageEventHandler implements WsMessageEventHandler {
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public void sendData(Map<String,Object> paramsAndData){
        GlobalWebSocketTopic topic = getTopic();
        WsSendEvent data = WsSendEvent.builder().topic(topic).paramsAndData(paramsAndData).build();
        applicationEventPublisher.publishEvent(data);
    }
}
