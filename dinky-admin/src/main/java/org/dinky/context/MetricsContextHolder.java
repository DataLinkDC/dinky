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

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.dinky.data.constant.PaimonTableConstant;
import org.dinky.data.enums.SseTopic;
import org.dinky.data.vo.MetricsVO;
import org.dinky.utils.PaimonUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import cn.hutool.core.text.StrFormatter;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
/**
 * The MetricsContextHolder class is used to manage the metric context,
 * including operations such as storing and sending metric data.
 */
@Slf4j
public class MetricsContextHolder {

    protected static final MetricsContextHolder instance = new MetricsContextHolder();
    private final List<MetricsVO> metricsVOS = new CopyOnWriteArrayList<>();
    private final AtomicLong lastDumpTime = new AtomicLong(System.currentTimeMillis());

    public static MetricsContextHolder getInstances() {
        return instance;
    }
    // 创建具有自定义命名的ThreadFactory
    ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("metrics-send-thread-%d")
            .build();

    // 创建自定义的ThreadPoolExecutor
    ExecutorService pool = new ThreadPoolExecutor(
            5,               // 核心线程池大小
            10,              // 最大线程池大小，允许线程池按需扩展
            60L,             // 空闲线程的存活时间
            TimeUnit.SECONDS, // 存活时间的单位
            new LinkedBlockingQueue<Runnable>(10), // 使用更大的队列容纳多余任务
            namedThreadFactory
    );
    public void sendAsync(String key, MetricsVO o) {
        Object content = o.getContent();
        if (content == null || (content instanceof ConcurrentHashMap && ((ConcurrentHashMap) content).isEmpty())) {
            return; // 提前返回，避免不必要的操作
        }
        pool.execute(() -> {
            metricsVOS.add(o);
            long current = System.currentTimeMillis();
            long duration = current - lastDumpTime.get();
            //Temporary cache monitoring information, mainly to prevent excessive buffering of write IO,
            // when metricsVOS data reaches 1000 or the time exceeds 15 seconds
            if (metricsVOS.size() >= 1000 || duration >= 15000) {
                List<MetricsVO> snapshot;
                synchronized (this) { // 仅在需要操作时才进入同步块
                    snapshot = new ArrayList<>(metricsVOS);
                    metricsVOS.clear();
                    lastDumpTime.set(current);
                }
                PaimonUtil.write(PaimonTableConstant.DINKY_METRICS, snapshot, MetricsVO.class);
            }
            String topic = StrFormatter.format("{}/{}", SseTopic.METRICS.getValue(), key);
            SseSessionContextHolder.sendTopic(topic, o); // 确保只有成功添加的指标才发送
        });
    }
}
