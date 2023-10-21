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

package org.dinky.context;

import org.dinky.data.exception.BusException;
import org.dinky.data.vo.SseDataVo;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SseSessionContextHolder {

    private static final Map<String, TopicSubscriber> sessionMap = new ConcurrentHashMap<>();

    public static Set<String> subscribeTopic(String sessionId, List<String> topics) {
        if (exists(sessionId)) {
            return sessionMap.get(sessionId).updateTopics(topics);
        } else {
            throw new BusException("Session not exits");
        }
    }

    public static SseEmitter conectSession(String sessionKey) {
        log.info("new session want to connect ：{}", sessionKey);
        if (exists(sessionKey)) {
            log.warn("session key exits:{}", sessionKey);
            closeSse(sessionKey);
        }
        SseEmitter sseEmitter = new SseEmitter(60 * 1000L);
        sseEmitter.onError(err -> onError(sessionKey, err));
        sseEmitter.onTimeout(() -> onTimeOut(sessionKey));
        sseEmitter.onCompletion(() -> onCompletion(sessionKey));
        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sessionMap.put(sessionKey, TopicSubscriber.of(sseEmitter));
        return sseEmitter;
    }

    public static boolean exists(String sessionKey) {
        return sessionMap.get(sessionKey) != null;
    }

    public static void onTimeOut(String sessionKey) {
        log.info("type: SseSession Timeout, session Id : {}", sessionKey);
        closeSse(sessionKey);
    }

    public static void closeSse(String sessionKey) {
        if (exists(sessionKey)) {
            try {
                sessionMap.get(sessionKey).getEmitter().complete();
            } catch (Exception e) {
                log.warn("complete sseEmitter failed,sessionKey:{}, error:{}", sessionKey, e.getMessage());
            } finally {
                sessionMap.remove(sessionKey);
            }
        }
    }

    public static void onError(String sessionKey, Throwable throwable) {
        log.error("type: SseSession [{}] Error, msg: {}", sessionKey, throwable.getMessage());
        if (exists(sessionKey)) {
            try {
                sessionMap.get(sessionKey).getEmitter().completeWithError(throwable);
            } catch (Exception e) {
                log.error("complete Sse With Error:{}", e.getMessage());
            }
        }
        sessionMap.remove(sessionKey);
    }

    public static void onCompletion(String sessionKey) {
        log.info("type: SseSession Completion, session Id : {}", sessionKey);
        closeSse(sessionKey);
    }

    public static void sendTopic(String topic, Object content) {
        sessionMap.forEach((sessionKey, topicSubscriber) -> {
            if (topicSubscriber.getTopics().contains(topic)) {
                try {
                    SseDataVo data = new SseDataVo(sessionKey,topic,content);
                    sendSse(sessionKey, data);
                } catch (Exception e) {
                    log.error("send sse data error", e);
                    onError(sessionKey, e);
                }
            }
        });
    }

    public static void sendSse(String sessionKey, SseDataVo content) throws IOException {
        if (exists(sessionKey)) {
            sessionMap.get(sessionKey).getEmitter().send(content);
        } else {
            log.warn("sse not found:{}", sessionKey);
        }
    }

    @Data
    public static class TopicSubscriber {
        private Set<String> topics;
        private final SseEmitter emitter;

        public TopicSubscriber(Set<String> topics, SseEmitter emitter) {
            this.topics = topics;
            this.emitter = emitter;
        }

        public static TopicSubscriber of(SseEmitter emitter) {
            return new TopicSubscriber(new HashSet<>(), emitter);
        }

        public Set<String> updateTopics(List<String> topics) {
            this.topics = new HashSet<>();
            this.topics.addAll(topics);
            return this.topics;
        }
    }
}
