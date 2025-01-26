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

import org.dinky.data.vo.WsDataVo;
import org.dinky.utils.JsonUtils;
import org.dinky.ws.handler.WsMessageEventHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ServerEndpoint(value = "/api/ws/global/{token}")
public class GlobalWebSocket {
    private final Map<GlobalWebSocketTopic, WsMessageEventHandler> wsMessageEventHandlerMap = new HashMap<>();

    public GlobalWebSocket() {
        SpringUtil.getBeansOfType(WsMessageEventHandler.class).forEach((beanName, eventHandler) -> {
            wsMessageEventHandlerMap.put(eventHandler.getTopic(), eventHandler);
        });
    }

    @Getter
    @Setter
    public static class RequestDTO {
        private Map<GlobalWebSocketTopic, Set<String>> topics;
        private EventType type;

        public enum EventType {
            SUBSCRIBE,
            PING,
            PONG
        }
    }

    private static final Map<Session, RequestDTO> TOPICS = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(@PathParam("token") String token, Session session) throws IOException {
        Object loginIdByToken = StpUtil.getLoginIdByToken(token);
        if (ObjectUtil.isEmpty(loginIdByToken)) {
            // todo 这里应该发送认证失败信息，方便前端重定向到登录页
            onClose(session);
            return;
        }
        session.setMaxIdleTimeout(30000);
    }

    @OnClose
    public void onClose(Session session) {
        TOPICS.remove(session);
        try {
            session.close();
        } catch (IOException e) {
            log.error("close session error", e);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        try {
            RequestDTO requestDTO = JsonUtils.parseObject(message, RequestDTO.class);
            if (requestDTO == null) {
                // unregister
                TOPICS.remove(session);
                return;
            }

            if (requestDTO.getType() == RequestDTO.EventType.PING) {
                WsDataVo data = new WsDataVo(session.getId(), RequestDTO.EventType.PONG);
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
        allParams.forEach((topic, params) ->
                sendTopic(topic, params, wsMessageEventHandlerMap.get(topic).firstSubscribe(params)));
    }

    private void send(Session session, WsDataVo data) {
        try {
            session.getBasicRemote().sendText(JsonUtils.toJsonString(data));
        } catch (IOException e) {
            log.error("send ws data error", e);
            throw new RuntimeException(e);
        }
    }

    public void sendTopic(GlobalWebSocketTopic topic, Set<String> params, Map<String, Object> result) {
        TOPICS.forEach((session, topics) -> {
            if (topics.getTopics().containsKey(topic)) {
                WsDataVo data = new WsDataVo(
                        session.getId(),
                        topic.name(),
                        params == null ? result.get(WsMessageEventHandler.NONE_PARAMS) : result);
                send(session, data);
            }
        });
    }

    public void sendTopic(GlobalWebSocketTopic topic, Map<String, ?> paramsAndData) {
        Map<Session, Set<String>> tempMap = new HashMap<>();
        TOPICS.forEach((session, requestDTO) -> paramsAndData.forEach((params, data) -> {
            Map<GlobalWebSocketTopic, Set<String>> topics = requestDTO.getTopics();
            if ((topics.containsKey(topic) && topics.get(topic).contains(params))
                    || params.equals(WsMessageEventHandler.NONE_PARAMS)) {
                tempMap.computeIfAbsent(session, k -> topics.get(topic)).add(params);
            }
        }));

        tempMap.forEach((session, params) -> {
            Map<String, Object> sendData = new HashMap<>();
            params.forEach(p -> sendData.put(p, paramsAndData.get(p)));
            WsDataVo data = new WsDataVo(session.getId(), topic.name(), sendData);
            send(session, data);
        });
    }

    @EventListener
    @Async("wsSendExecutor")
    public void handleOrderCreatedEvent(WsSendEvent event) {
        sendTopic(event.getTopic(), event.getParamsAndData());
    }
}
