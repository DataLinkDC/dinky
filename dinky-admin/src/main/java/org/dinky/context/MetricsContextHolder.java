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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.metrics2.util.SampleQuantiles;
import org.dinky.data.constant.PaimonTableConstant;
import org.dinky.data.enums.SseTopic;
import org.dinky.data.vo.MetricsVO;
import org.dinky.utils.PaimonUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import cn.hutool.core.text.StrFormatter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dinky.utils.SqliteUtil;

/**
 * The MetricsContextHolder class is used to manage the metric context,
 * including operations such as storing and sending metric data.
 */
@Slf4j
public class MetricsContextHolder {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    protected static final MetricsContextHolder instance = new MetricsContextHolder();

    private final List<MetricsVO> metricsVOS = new CopyOnWriteArrayList<>();
    private final AtomicLong lastDumpTime = new AtomicLong(System.currentTimeMillis());

    // Create a ThreadFactory with custom naming
    ThreadFactory namedThreadFactory =
            new ThreadFactoryBuilder().setNameFormat("metrics-send-thread-%d").build();

    // Create a custom ThreadPoolExecutor
    ExecutorService pool = new ThreadPoolExecutor(
            5, // Core pool size
            10, // Maximum pool size, allows the pool to expand as needed
            60L, // Keep alive time for idle threads
            TimeUnit.SECONDS, // Unit of keep alive time
            new LinkedBlockingQueue<>(10), // Use a larger queue to hold excess tasks
            namedThreadFactory);

    public void sendAsync(String key, MetricsVO o) {
        Object content = o.getContent();
        if (content == null
                || (content instanceof ConcurrentHashMap && ((ConcurrentHashMap<?, ?>) content).isEmpty())) {
            return; // Return early to avoid unnecessary operations
        }
        pool.execute(() -> {
            metricsVOS.add(o);
            long current = System.currentTimeMillis();
            long duration = current - lastDumpTime.get();
            // Temporary cache monitoring information, mainly to prevent excessive buffering of write IO,
            // when metricsVOS data reaches 1000 or the time exceeds 15 seconds
            if (metricsVOS.size() >= 1000 || duration >= 15000) {
                List<MetricsVO> snapshot;
                synchronized (this) { // Enter synchronized block only when necessary
                    snapshot = new ArrayList<>(metricsVOS);
                    metricsVOS.clear();
                    lastDumpTime.set(current);
                }
                PaimonUtil.write(PaimonTableConstant.DINKY_METRICS, snapshot, MetricsVO.class);
            }
            String topic = StrFormatter.format("{}/{}", SseTopic.METRICS.getValue(), key);
            SseSessionContextHolder.sendTopic(topic, o); // Ensure only successfully added metrics are sent
        });
    }

    public void saveToSqlite(String key, MetricsVO o) {
        Object content = o.getContent();
        if (content == null
                || (content instanceof ConcurrentHashMap && ((ConcurrentHashMap<?, ?>) content).isEmpty())) {
            return;
        }

        metricsVOS.add(o);
        long current = System.currentTimeMillis();
        long duration = current - lastDumpTime.get();
        if (metricsVOS.size() >= 1000 || duration >= 15000) {
            lastDumpTime.set(current);
            final List<String> columns = Arrays.asList("job_id", "value", "heart_time,date");
            List<String> values = convertMetricsVOsToStringList(metricsVOS);
            try {
                SqliteUtil.INSTANCE.write(PaimonTableConstant.DINKY_METRICS, columns, values);
            } catch (SQLException e) {
                log.error("Failed to write metrics to SQLite", e);
                return;
            }
            metricsVOS.clear();
        }
        String topic = StrFormatter.format("{}/{}", SseTopic.METRICS.getValue(), key);
        SseSessionContextHolder.sendTopic(topic, o);
    }

    public List<String> convertMetricsVOsToStringList(List<MetricsVO> metricsVOS) {
        List<String> result = new ArrayList<>();

        for (MetricsVO metricsVO : metricsVOS) {
            Map<String, Object> content = (Map<String, Object>) metricsVO.getContent();
            try {
                String serializedContent = objectMapper.writeValueAsString(content);
                String keyValueString = metricsVO.getModel() + "," + serializedContent + "," + metricsVO.getHeartTime() + "," + metricsVO.getDate();
                result.add(keyValueString);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize content of MetricsVO: {}", metricsVO, e);
            }
        }

        return result;
    }
}
