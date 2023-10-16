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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseSseContext<K, V> {

    /**
     * Cache that stores SseEmitter objects for sending metric data,
     * prevents OOM with LoadingCache, and is automatically removed when objects
     * in the cache are not accessed or used for more than 60 seconds.
     */
    protected LoadingCache<K, List<SseEmitter>> sseList = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .removalListener(this::onRemove)
            .build(CacheLoader.from(key -> new ArrayList<>()));

    /**
     * Called during an asynchronous send procedure
     */
    public abstract void append(K key, V o);

    /**
     * send  data asynchronously.
     */
    public void sendAsync(K key, V o) {
        CompletableFuture.runAsync(() -> {
            append(key, o);
            send(key, o);
        });
    }

    /**
     * send data.
     */
    protected void send(K key, V o) {
        List<SseEmitter> sseEmitters = sseList.getIfPresent(key);
        if (sseEmitters != null) {
            sseEmitters.forEach(sseEmitter -> {
                try {
                    sseEmitter.send(o);
                } catch (Exception e) {
                    log.warn("send metrics error:{}", e.getMessage());
                    closeSse(sseEmitter);
                    sseEmitters.remove(sseEmitter);
                }
            });
        }
    }

    /**
     * remove the SseEmitter object from the cache
     * When the connection times out or actively exits.
     *
     * @param removalNotification RemovalNotification object
     */
    protected void onRemove(RemovalNotification<K, List<SseEmitter>> removalNotification) {
        assert removalNotification.getValue() != null;
        removalNotification.getValue().forEach(this::closeSse);
    }

    /**
     * close the SseEmitter object.
     *
     * @param sseEmitter SseEmitter object
     */
    protected void closeSse(SseEmitter sseEmitter) {
        try {
            sseEmitter.complete();
        } catch (Exception e) {
            log.warn("complete sseEmitter failed:{}", e.getMessage());
        }
    }
}
