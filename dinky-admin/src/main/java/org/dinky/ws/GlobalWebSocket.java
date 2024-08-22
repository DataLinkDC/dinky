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

import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
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
    public static class RequestDTO extends HashMap<GlobalWebSocketTopic, Set<String>> {}

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
            if (MapUtil.isNotEmpty(requestDTO)) {
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

    private RequestDTO getRequestParamMap() {
        RequestDTO temp = new RequestDTO();
        // Get all the parameters of the theme
        TOPICS.values().forEach(requestDTO -> {
            requestDTO.forEach((topic, params) -> {
                if (temp.containsKey(topic)) {
                    temp.get(topic).addAll(params);
                } else {
                    temp.put(topic, params);
                }
            });
        });
        return temp;
    }

    private void firstSend() {
        RequestDTO allParams = getRequestParamMap();
        // Send data
        allParams.forEach((topic, params) -> {
            sendTopic(topic, params, topic.getInstance().firstDataSend(params));
        });
    }

    public void sendTopic(GlobalWebSocketTopic topic, Set<String> params, Map<String, Object> result) {
        TOPICS.forEach((session, topics) -> {
            if (topics.containsKey(topic)) {
                try {
                    SseDataVo data = new SseDataVo(
                            session.getId(), topic.name(), params == null ? result.get(BaseTopic.NONE_PARAMS) : result);

                    session.getBasicRemote().sendText(JSONUtil.toJsonStr(data));

                } catch (Exception e) {
                    log.error("Error sending sse data:{}", e.getMessage());
                    SpringUtil.getBean(GlobalWebSocket.class).onError(session, e);
                }
            }
        });
    }

    public static void sendTopic(GlobalWebSocketTopic topic, Map<String, Object> paramsAndData) {
        Map<Session, Set<String>> tempMap = new HashMap<>();
        TOPICS.forEach((session, requestDTO) -> {
            paramsAndData.forEach((params, data) -> {
                if (requestDTO.containsKey(topic) && requestDTO.get(topic).contains(params)) {
                    tempMap.computeIfAbsent(session, k -> requestDTO.get(topic)).add(params);
                }
            });
        });

        tempMap.forEach((session, params) -> {
            try {
                Map<String, Object> sendData = new HashMap<>();
                params.forEach(p -> {
                    sendData.put(p, paramsAndData.get(p));
                });
                SseDataVo sseDataVo = new SseDataVo(session.getId(), topic.name(), sendData);
                session.getBasicRemote().sendText(JSONUtil.toJsonStr(sseDataVo));
            } catch (IOException e) {
                log.error("Error sending sse data:{}", e.getMessage());
                SpringUtil.getBean(GlobalWebSocket.class).onError(session, e);
            }
        });
    }
}
