package org.dinky.ws.handler;

import org.dinky.ws.GlobalWebSocketTopic;
import org.dinky.ws.WsSendEvent;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public abstract class ScheduleMessageEventHandler implements WsMessageEventHandler {
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     *
     * @return timed scheduling intervals; Unit: milliseconds
     */
    protected abstract long scheduleDelay();

    @Override
    public void run() {
        Timer timer = new Timer();
        long delay = scheduleDelay();
        GlobalWebSocketTopic topic = getTopic();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Map<String, ?> data = autoMessageSend();
                WsSendEvent event = WsSendEvent.builder().topic(topic).paramsAndData(data).build();
                applicationEventPublisher.publishEvent(event);
            }
        },0, delay);
    }
}
