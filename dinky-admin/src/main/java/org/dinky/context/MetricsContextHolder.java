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

import org.dinky.data.vo.MetricsVO;
import org.dinky.utils.PaimonUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;

import lombok.extern.slf4j.Slf4j;

/**
 * The MetricsContextHolder class is used to manage the metric context,
 * including operations such as storing and sending metric data.
 */
@Slf4j
public class MetricsContextHolder {

    /**
     * Temporary cache monitoring information, mainly to prevent excessive buffering of write IO,
     * when metricsVOS data reaches 1000 or the time exceeds 5 seconds
     */
    private static final List<MetricsVO> metricsVOS = Collections.synchronizedList(new ArrayList<>());

    private static final Long lastDumpTime = System.currentTimeMillis();

    /**
     * Cache that stores SseEmitter objects for sending metric data,
     * prevents OOM with LoadingCache, and is automatically removed when objects
     * in the cache are not accessed or used for more than 60 seconds.
     */
    private static final LoadingCache<Object, List<SseEmitter>> sseList = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .removalListener(MetricsContextHolder::onRemove)
            .build(CacheLoader.from(key -> new ArrayList<>()));

    /**
     * The sendAsync method is used to send metric data asynchronously.
     *
     * @param metrics metric data object
     */
    public static void sendAsync(MetricsVO metrics) {
        CompletableFuture.runAsync(() -> {
            dumpMetrics(metrics);
            send(metrics);
        });
    }

    /**
     * The addSse method is used to add the SseEmitter object.
     *
     * @param keys       keyword list for indicator data
     * @param sseEmitter SseEmitter object
     * @param lastTime   initialization data intercepts the largest timestamp
     */
    public static void addSse(List<String> keys, SseEmitter sseEmitter, LocalDateTime lastTime) {
        keys.forEach(key -> {
            List<SseEmitter> sseEmitters = sseList.getIfPresent(key);
            if (sseEmitters == null) {
                sseEmitters = new ArrayList<>();
                sseList.put(key, sseEmitters);
            }
            sseEmitters.add(sseEmitter);
        });
        sendInitData(keys, sseEmitter, lastTime);
    }

    /**
     * The sendInitData method is used to send initialized indicator data.
     *
     * @param keys       keyword list for indicator data
     * @param sseEmitter SseEmitter object
     * @param lastTime's last timestamp
     */
    private static void sendInitData(List<String> keys, SseEmitter sseEmitter, LocalDateTime lastTime) {
        CompletableFuture.runAsync(() -> {
            synchronized (metricsVOS) {
                metricsVOS.forEach(metricsVO -> {
                    if (keys.contains(metricsVO.getModel())
                            && metricsVO.getHeartTime().isAfter(lastTime)) {
                        try {
                            sseEmitter.send(metricsVO);
                        } catch (Exception e) {
                            log.warn("send metrics error:{}", e.getMessage());
                            closeSse(sseEmitter);
                        }
                    }
                });
            }
        });
    }

    /**
     * The dumpMetrics method is used to dump metric data to paimon.
     *
     * @param metrics metric data object
     */
    private static void dumpMetrics(MetricsVO metrics) {
        metricsVOS.add(metrics);
        long duration = System.currentTimeMillis() - lastDumpTime;
        synchronized (metricsVOS) {
            if (metricsVOS.size() > 1000 || duration > 1000 * 5) {
                PaimonUtil.writeMetrics(metricsVOS);
                metricsVOS.clear();
            }
        }
    }

    /**
     * The send method is used to send metric data.
     *
     * @param metrics metric data object
     */
    private static void send(MetricsVO metrics) {
        List<SseEmitter> sseEmitters = sseList.getIfPresent(metrics.getModel());
        if (sseEmitters != null) {
            sseEmitters.forEach(sseEmitter -> {
                try {
                    sseEmitter.send(metrics);
                } catch (Exception e) {
                    log.warn("send metrics error:{}", e.getMessage());
                    closeSse(sseEmitter);
                    sseEmitters.remove(sseEmitter);
                }
            });
        }
    }

    /**
     * The onRemove method is used to remove the SseEmitter object from the cache.
     *
     * @param removalNotification RemovalNotification object
     */
    private static void onRemove(RemovalNotification<Object, List<SseEmitter>> removalNotification) {
        assert removalNotification.getValue() != null;
        removalNotification.getValue().forEach(MetricsContextHolder::closeSse);
    }

    /**
     * The closeSse method is used to close the SseEmitter object.
     *
     * @param sseEmitter SseEmitter object
     */
    private static void closeSse(SseEmitter sseEmitter) {
        try {
            sseEmitter.complete();
        } catch (Exception e) {
            log.warn("complete sseEmitter failed:{}", e.getMessage());
        }
    }
}
