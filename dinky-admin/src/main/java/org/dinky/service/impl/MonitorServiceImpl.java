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

package org.dinky.service.impl;

import org.dinky.context.MetricsContextHolder;
import org.dinky.data.dto.MetricsLayoutDTO;
import org.dinky.data.metrics.Jvm;
import org.dinky.data.model.Metrics;
import org.dinky.data.vo.MetricsVO;
import org.dinky.mapper.MetricsMapper;
import org.dinky.process.exception.DinkyException;
import org.dinky.service.MonitorService;
import org.dinky.utils.PaimonUtil;

import org.apache.paimon.data.BinaryString;
import org.apache.paimon.data.Timestamp;
import org.apache.paimon.predicate.Predicate;
import org.apache.paimon.predicate.PredicateBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitorServiceImpl extends ServiceImpl<MetricsMapper, Metrics> implements MonitorService {
    private final Executor scheduleRefreshMonitorDataExecutor;

    @Override
    public List<MetricsVO> getData(Date startTime, Date endTime, List<String> jobIds) {
        endTime = Opt.ofNullable(endTime).orElse(DateUtil.date());
        Timestamp startTS = Timestamp.fromLocalDateTime(DateUtil.toLocalDateTime(startTime));
        Timestamp endTS = Timestamp.fromLocalDateTime(DateUtil.toLocalDateTime(endTime));

        if (endTime.compareTo(startTime) < 1) {
            throw new DinkyException("The end date must be greater than the start date!");
        }

        Function<PredicateBuilder, List<Predicate>> filter = p -> {
            Predicate greaterOrEqual = p.greaterOrEqual(0, startTS);
            Predicate lessOrEqual = p.lessOrEqual(0, endTS);
            Predicate local =
                    p.in(1, jobIds.stream().map(BinaryString::fromString).collect(Collectors.toList()));
            return CollUtil.newArrayList(local, greaterOrEqual, lessOrEqual);
        };
        List<MetricsVO> metricsVOList =
                PaimonUtil.batchReadTable(PaimonUtil.METRICS_IDENTIFIER, MetricsVO.class, filter);
        return metricsVOList.stream()
                .filter(x -> x.getHeartTime().isAfter(startTS.toLocalDateTime()))
                .filter(x -> x.getHeartTime().isBefore(endTS.toLocalDateTime()))
                .peek(vo -> vo.setContent(new JSONObject(vo.getContent().toString())))
                .collect(Collectors.toList());
    }

    @Override
    public SseEmitter sendLatestData(SseEmitter sseEmitter, LocalDateTime lastDate, List<String> keys) {
        MetricsContextHolder.getInstances().addSse(keys, sseEmitter, lastDate);
        return sseEmitter;
    }

    @Override
    public SseEmitter sendJvmInfo(SseEmitter sseEmitter) {
        scheduleRefreshMonitorDataExecutor.execute(() -> {
            try {
                while (true) {
                    sseEmitter.send(JSONUtil.toJsonStr(Jvm.of()));
                    ThreadUtil.sleep(10000);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sseEmitter.complete();
            }
        });
        return sseEmitter;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFlinkMetricLayout(String layout, List<MetricsLayoutDTO> metricsList) {
        QueryWrapper<Metrics> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Metrics::getLayoutName, layout);
        remove(wrapper);
        if (CollUtil.isEmpty(metricsList)) {
            return;
        }
        saveBatch(BeanUtil.copyToList(metricsList, Metrics.class));
    }

    @Override
    public Map<String, List<Metrics>> getMetricsLayout() {
        List<Metrics> list = list();
        Map<String, List<Metrics>> result = new HashMap<>();
        list.forEach(x -> {
            String layoutName = x.getLayoutName();
            result.computeIfAbsent(layoutName, (k) -> new ArrayList<>());
            result.get(layoutName).add(x);
        });
        return result;
    }

    @Override
    public List<Metrics> getMetricsLayoutByName(String layoutName) {
        QueryWrapper<Metrics> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Metrics::getLayoutName, layoutName);
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    public List<Metrics> getJobMetrics(Integer taskId) {
        QueryWrapper<Metrics> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Metrics::getTaskId, taskId);
        return this.baseMapper.selectList(wrapper);
    }
}
