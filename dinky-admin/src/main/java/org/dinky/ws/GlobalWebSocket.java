package org.dinky.ws;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.dinky.data.metrics.Jvm;
import org.dinky.data.vo.SseDataVo;
import org.dinky.utils.JsonUtils;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@ServerEndpoint(value = "/api/ws/global")
public class GlobalWebSocket {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(1);

    public GlobalWebSocket(){
        executorService.execute(()->{
            while (true){
                sendTopic(GlobalWebSocketTopic.JVM_INFO.getTopic(), Jvm.of());
                ThreadUtil.safeSleep(TimeUnit.SECONDS.toMillis(5));
            }
        });
    }
    @Getter
    @Setter
    public static class RequestDTO {
        private List<String> topics;
    }

    private final static Map<Session, Set<String>> TOPICS = new ConcurrentHashMap<>();


    @OnOpen
    public void onOpen(Session session) {
    }

    @OnClose
    public void onClose(Session session) {
        TOPICS.remove(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        try {
            RequestDTO requestDTO = JsonUtils.parseObject(message, RequestDTO.class);
            if (requestDTO != null && CollUtil.isNotEmpty(requestDTO.getTopics())) {
                TOPICS.put(session,new HashSet<>(requestDTO.getTopics()));
            }else {
                TOPICS.remove(session);
            }
        } catch (Exception e) {
            log.warn("bad ws message subscription msg:{}", message);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        onClose(session);
    }


    /**
     * Sends the specified content to all subscribers of the given topic.
     *
     * @param topic   The topic to send the content to.
     * @param content The content to send.
     */
    public static void sendTopic(String topic, Object content) {
        TOPICS.forEach((session, topics) -> {
            if (topics.contains(topic)) {
                try {
                    SseDataVo data = new SseDataVo(session.getId(), topic, content);
                    session.getBasicRemote().sendText(JSONUtil.toJsonStr(data));
                } catch (Exception e) {
                    log.error("Error sending sse data:{}", e.getMessage());
                    SpringUtil.getBean(GlobalWebSocket.class).onError(session, e);
                }
            }
        });
    }


}

