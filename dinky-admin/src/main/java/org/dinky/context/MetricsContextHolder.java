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

import static org.dinky.data.constant.MonitorTableConstant.JOB_ID;

import org.dinky.data.constant.MonitorTableConstant;
import org.dinky.data.vo.MetricsVO;
import org.dinky.utils.SqliteUtil;
import org.dinky.ws.handler.ProcessConsole;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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
    private final AtomicLong lastDumpTime = new AtomicLong(0);

    static {
        String sql = String.format(
                "%s BIGINT, %s TEXT, %s TEXT, %s INTEGER",
                JOB_ID, MonitorTableConstant.VALUE, MonitorTableConstant.HEART_TIME, MonitorTableConstant.DATE);
        SqliteUtil.INSTANCE.createTable(MonitorTableConstant.DINKY_METRICS, sql);
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
            List<List<String>> values = convertMetricsVOsToStringList(metricsVOS);
            try {
                final List<String> columns = Arrays.asList(
                        JOB_ID, MonitorTableConstant.VALUE, MonitorTableConstant.HEART_TIME, MonitorTableConstant.DATE);
                SqliteUtil.INSTANCE.write(MonitorTableConstant.DINKY_METRICS, columns, values);
            } catch (SQLException e) {
                log.error("Failed to write metrics to SQLite", e);
                return;
            }
            metricsVOS.clear();
        }
        Map<String, Object> data = MapUtil.<String, Object>builder().put(key, o).build();

        // send ws event
        SpringUtil.getBean(ProcessConsole.class).sendData(data);
    }

    public List<List<String>> convertMetricsVOsToStringList(List<MetricsVO> metricsVOS) {
        List<List<String>> result = new ArrayList<>();

        for (MetricsVO metricsVO : metricsVOS) {
            Map<String, Object> content = (Map<String, Object>) metricsVO.getContent();
            try {
                List<String> row = new ArrayList<>();
                row.add(metricsVO.getModel());
                row.add(objectMapper.writeValueAsString(content));
                row.add(metricsVO.getHeartTime().toString());
                row.add(metricsVO.getDate());
                result.add(row);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize content of MetricsVO: {}", metricsVO, e);
            }
        }

        return result;
    }
}
