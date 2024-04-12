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

import lombok.Getter;
import org.dinky.daemon.pool.ScheduleThreadPool;
import org.dinky.data.constant.SseConstant;
import org.dinky.data.exception.BusException;
import org.dinky.data.vo.SseDataVo;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class SseSessionContextHolder {

    @Getter
    private static final Map<String, TopicSubscriber> sessionMap = new ConcurrentHashMap<>();

    public static void init(ScheduleThreadPool schedulePool) {
        log.info("start init sse heart schedule task");
        PeriodicTrigger trigger = new PeriodicTrigger(9 * 1000L);
        schedulePool.addSchedule(SseSessionContextHolder.class.toString(), SseSessionContextHolder::checkHeart, trigger);
    }

    public static void checkHeart() {
        sessionMap.forEach((sessionKey, topicSubscriber) -> {
            try {
                SseDataVo data = new SseDataVo(sessionKey, SseConstant.HEART_TOPIC, "heart");
                sendSse(sessionKey, data);
            } catch (Exception e) {
                log.error("Error sending sse data:{}", e.getMessage());
                onError(topicSubscriber.getEmitter(), sessionKey, e);
            }
        });
    }

    /**
     * Subscribes a session to the topics.
     * A session can subscribe to multiple topics
     *
     * @param sessionId The ID of the session.
     * @param topics    The list of topics to subscribe to.
     * @return The updated set of topics for the session.
     * @throws BusException If the session does not exist.
     */
    public static Set<String> subscribeTopic(String sessionId, List<String> topics) {
        if (exists(sessionId)) {
            return sessionMap.get(sessionId).updateTopics(topics);
        } else {
            HashSet<String> reconnectMessage = new HashSet<>(1);
            reconnectMessage.add(SseConstant.SSE_SESSION_INVALID);
            log.warn("session id is invalid");
            return reconnectMessage;
        }
    }

    /**
     * Connects a session with the given session key.
     *
     * @param sessionKey The session key of the new session.
     * @return The SseEmitter for the session.
     */
    public static synchronized SseEmitter connectSession(String sessionKey) {
        log.debug("New session wants to connect: {}", sessionKey);

        SseEmitter sseEmitter = new SseEmitter(5 * 60 * 1000L);
        sseEmitter.onError(err -> onError(sseEmitter, sessionKey, err));
        sseEmitter.onTimeout(() -> onTimeout(sseEmitter));
        sseEmitter.onCompletion(() -> onCompletion(sseEmitter, sessionKey));
        sessionMap.put(sessionKey, TopicSubscriber.of(sseEmitter));
        try {
            // Set the client reconnection interval, 0 to reconnect immediately
            sseEmitter.send(SseEmitter.event().reconnectTime(1000));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sseEmitter;
    }

    /**
     * Checks if a session with the given session key exists.
     *
     * @param sessionKey The session key to check.
     * @return true if the session exists, false otherwise.
     */
    public static boolean exists(String sessionKey) {
        return sessionMap.get(sessionKey) != null;
    }

    /**
     * Handles the timeout event for a session.
     */
    public static void onTimeout(SseEmitter sseEmitter) {
        try {
            sseEmitter.complete();
        } catch (Exception e) {
            log.warn("Failed to complete sseEmitter, Error: {}", e.getMessage());
        }
    }

    /**
     * Handles the error event for a session.
     *
     * @param sessionKey The session key of the session with the error.
     * @param throwable  The throwable representing the error.
     */
    private static void onError(SseEmitter sseEmitter, String sessionKey, Throwable throwable) {
        log.debug("Type: SseSession [{}] Error, Message: {}", sessionKey, throwable.getMessage());
        try {
            sseEmitter.completeWithError(throwable);
        } catch (Exception e) {
            log.debug("Failed to complete Sse With Error: {}", e.getMessage());
        }
        onCompletion(sseEmitter, sessionKey);
    }

    /**
     * Handles the completion event for a session.
     *
     * @param sessionKey The session key of the completed session.
     */
    private static synchronized void onCompletion(SseEmitter sseEmitter, String sessionKey) {
        log.debug("Type: SseSession Completion, Session ID: {}", sessionKey);
        if (exists(sessionKey)) {
            SseEmitter emitter = sessionMap.get(sessionKey).getEmitter();
            if (emitter == sseEmitter) {
                sessionMap.remove(sessionKey);
            }
        }
    }

    /**
     * Sends the specified content to all subscribers of the given topic.
     *
     * @param topic   The topic to send the content to.
     * @param content The content to send.
     */
    public static void sendTopic(String topic, Object content) {
        sessionMap.forEach((sessionKey, topicSubscriber) -> {
            if (topicSubscriber.getTopics().contains(topic)) {
                try {
                    SseDataVo data = new SseDataVo(sessionKey, topic, content);
                    sendSse(sessionKey, data);
                } catch (Exception e) {
                    log.error("Error sending sse data:{}", e.getMessage());
                    onError(topicSubscriber.getEmitter(), sessionKey, e);
                }
            }
        });
    }

    /**
     * Sends the specified SSE data to the session with the given session key.
     *
     * @param sessionKey The session key of the recipient session.
     * @param content    The SSE data to send.
     * @throws IOException If an I/O error occurs while sending the data.
     */
    public static void sendSse(String sessionKey, SseDataVo content) throws Exception {
        if (exists(sessionKey)) {
            sessionMap.get(sessionKey).getEmitter().send(content);
        } else {
            log.warn("SseEmitter not found for session key: {}", sessionKey);
        }
    }

    /**
     * Represents a topic subscriber with the subscribed topics and the associated SseEmitter.
     */
    @Data
    public static class TopicSubscriber {
        private Set<String> topics;
        private final SseEmitter emitter;

        /**
         * Creates a new TopicSubscriber with the specified SseEmitter.
         *
         * @param topics  The initial set of topics.
         * @param emitter The SseEmitter associated with the subscriber.
         */
        public TopicSubscriber(Set<String> topics, SseEmitter emitter) {
            this.topics = topics;
            this.emitter = emitter;
        }

        /**
         * Creates a new TopicSubscriber with the specified SseEmitter and an empty set of topics.
         *
         * @param emitter The SseEmitter associated with the subscriber.
         * @return The created TopicSubscriber.
         */
        public static TopicSubscriber of(SseEmitter emitter) {
            return new TopicSubscriber(new HashSet<>(), emitter);
        }

        /**
         * Updates the topics for the subscriber.
         *
         * @param topics The new list of topics.
         * @return The updated set of topics.
         */
        public Set<String> updateTopics(List<String> topics) {
            this.topics = new HashSet<>(topics);
            return this.topics;
        }
    }
}
