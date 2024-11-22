/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.ws;

import org.dinky.data.vo.SseDataVo;
import org.dinky.utils.JsonUtils;
import org.dinky.utils.ThreadUtil;
import org.dinky.ws.topic.BaseTopic;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ServerEndpoint(value = "/api/ws/global")
public class GlobalWebSocket {
    private static final ExecutorService executorService =
            Executors.newFixedThreadPool(GlobalWebSocketTopic.values().length);
    private static boolean isRunning = true;

    public GlobalWebSocket() {
        for (GlobalWebSocketTopic value : GlobalWebSocketTopic.values()) {
            executorService.execute(() -> {
                while (isRunning) {
                    Set<String> params = getRequestParamMap().get(value);
                    sendTopic(value, params, value.getInstance().autoDataSend(params));
                    ThreadUtil.sleep(value.getDelaySend());
                }
            });
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            isRunning = false;
            executorService.shutdown();
        }));
    }

    @Getter
    @Setter
    public static class RequestDTO {
        private Map<GlobalWebSocketTopic, Set<String>> topics;
        private String token;
        private EventType type;

        public enum EventType {
            SUBSCRIBE,
            PING,
            PONG
        }
    }

    private static final Map<Session, RequestDTO> TOPICS = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {}

    @OnClose
    public void onClose(Session session) {
        TOPICS.remove(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        try {
            RequestDTO requestDTO = JsonUtils.parseObject(message, RequestDTO.class);
            if (requestDTO == null || StpUtil.getLoginIdByToken(requestDTO.getToken()) == null) {
                // unregister
                TOPICS.remove(session);
                return;
            }

            if (requestDTO.getType() == RequestDTO.EventType.PING) {
                SseDataVo data = new SseDataVo(session.getId(), RequestDTO.EventType.PONG);
                session.getBasicRemote().sendText(JsonUtils.toJsonString(data));
                return;
            }

            Map<GlobalWebSocketTopic, Set<String>> topics = requestDTO.getTopics();
            if (MapUtil.isNotEmpty(topics)) {
                TOPICS.put(session, requestDTO);
            } else {
                TOPICS.remove(session);
            }
            // When a subscription is renewed, the latest message is sent globally
            firstSend();
        } catch (Exception e) {
            log.warn("bad ws message subscription msg:{}", message);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        onClose(session);
    }

    private Map<GlobalWebSocketTopic, Set<String>> getRequestParamMap() {
        Map<GlobalWebSocketTopic, Set<String>> temp = new HashMap<>();
        // Get all the parameters of the theme
        TOPICS.values()
                .forEach(requestDTO -> requestDTO.topics.forEach((topic, params) -> {
                    if (temp.containsKey(topic)) {
                        temp.get(topic).addAll(params);
                    } else {
                        temp.put(topic, params);
                    }
                }));
        return temp;
    }

    private void firstSend() {
        Map<GlobalWebSocketTopic, Set<String>> allParams = getRequestParamMap();
        // Send data
        allParams.forEach(
                (topic, params) -> sendTopic(topic, params, topic.getInstance().firstDataSend(params)));
    }

    public void sendTopic(GlobalWebSocketTopic topic, Set<String> params, Map<String, Object> result) {
        TOPICS.forEach((session, topics) -> {
            if (topics.getTopics().containsKey(topic)) {
                try {
                    SseDataVo data = new SseDataVo(
                            session.getId(), topic.name(), params == null ? result.get(BaseTopic.NONE_PARAMS) : result);

                    session.getBasicRemote().sendText(JsonUtils.toJsonString(data));

                } catch (Exception e) {
                    log.error("Error sending sse data:{}", e.getMessage());
                    SpringUtil.getBean(GlobalWebSocket.class).onError(session, e);
                }
            }
        });
    }

    public static void sendTopic(GlobalWebSocketTopic topic, Map<String, Object> paramsAndData) {
        Map<Session, Set<String>> tempMap = new HashMap<>();
        TOPICS.forEach((session, requestDTO) -> paramsAndData.forEach((params, data) -> {
            Map<GlobalWebSocketTopic, Set<String>> topics = requestDTO.getTopics();
            if (topics.containsKey(topic) && topics.get(topic).contains(params)) {
                tempMap.computeIfAbsent(session, k -> topics.get(topic)).add(params);
            }
        }));

        tempMap.forEach((session, params) -> {
            try {
                Map<String, Object> sendData = new HashMap<>();
                params.forEach(p -> sendData.put(p, paramsAndData.get(p)));
                SseDataVo sseDataVo = new SseDataVo(session.getId(), topic.name(), sendData);
                session.getBasicRemote().sendText(JsonUtils.toJsonString(sseDataVo));
            } catch (IOException e) {
                log.error("Error sending sse data:{}", e.getMessage());
                SpringUtil.getBean(GlobalWebSocket.class).onError(session, e);
            }
        });
    }
}
