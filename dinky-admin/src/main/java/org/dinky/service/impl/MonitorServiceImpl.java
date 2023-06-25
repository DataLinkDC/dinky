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

import org.dinky.configure.MetricConfig;
import org.dinky.data.dto.MetricsLayoutDTO;
import org.dinky.data.model.Metrics;
import org.dinky.data.vo.MetricsVO;
import org.dinky.mapper.MetricsMapper;
import org.dinky.process.exception.DinkyException;
import org.dinky.service.MonitorService;
import org.dinky.utils.PaimonUtil;

import org.apache.paimon.data.Timestamp;
import org.apache.paimon.predicate.Predicate;
import org.apache.paimon.predicate.PredicateBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.thread.ThreadUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitorServiceImpl extends ServiceImpl<MetricsMapper, Metrics>
        implements MonitorService {
    private final Executor scheduleRefreshMonitorDataExecutor;

    @Override
    public List<MetricsVO> getData(Date startTime, Date endTime) {
        endTime = Opt.ofNullable(endTime).orElse(DateUtil.date());
        Timestamp startTS = Timestamp.fromLocalDateTime(DateUtil.toLocalDateTime(startTime));
        Timestamp endTS = Timestamp.fromLocalDateTime(DateUtil.toLocalDateTime(endTime));

        if (endTime.compareTo(startTime) < 1) {
            throw new DinkyException("The end date must be greater than the start date!");
        }
        Function<PredicateBuilder, List<Predicate>> filter =
                p -> {
                    Predicate greaterOrEqual = p.greaterOrEqual(0, startTS);
                    Predicate lessOrEqual = p.lessOrEqual(0, endTS);
                    Predicate local = p.equal(2, "local");
                    return CollUtil.newArrayList(local, greaterOrEqual, lessOrEqual);
                };
        List<MetricsVO> metricsVOList =
                PaimonUtil.batchReadTable(PaimonUtil.METRICS_IDENTIFIER, MetricsVO.class, filter);
        return metricsVOList.stream()
                .filter(x -> x.getHeartTime().isAfter(startTS.toLocalDateTime()))
                .filter(x -> x.getHeartTime().isBefore(endTS.toLocalDateTime()))
                .collect(Collectors.toList());
    }

    @Override
    public SseEmitter sendLatestData(SseEmitter sseEmitter, Date lastDate) {
        Queue<MetricsVO> metricsQueue = MetricConfig.getMetricsQueue();
        scheduleRefreshMonitorDataExecutor.execute(
                () -> {
                    try {
                        LocalDateTime maxDate = DateUtil.toLocalDateTime(lastDate);
                        while (true) {
                            if (CollUtil.isEmpty(metricsQueue)) {
                                continue;
                            }
                            for (MetricsVO metrics : metricsQueue) {
                                if (metrics.getHeartTime().isAfter(maxDate)) {
                                    sseEmitter.send(metrics);
                                    maxDate = metrics.getHeartTime();
                                }
                            }
                            ThreadUtil.sleep(800);
                        }
                    } catch (IOException e) {
                        sseEmitter.complete();
                    } catch (Exception e) {
                        e.printStackTrace();
                        sseEmitter.complete();
                    }
                });
        return sseEmitter;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFlinkMetricLayout(List<MetricsLayoutDTO> metricsList) {
        if (CollUtil.isEmpty(metricsList)) {
            return;
        }
        saveBatch(BeanUtil.copyToList(metricsList, Metrics.class));
    }

    @Override
    public Map<String, List<Metrics>> getMetricsLayout() {
        List<Metrics> list = list();
        Map<String, List<Metrics>> result = new HashMap<>();
        list.forEach(
                x -> {
                    String layoutName = x.getLayoutName();
                    result.computeIfAbsent(layoutName, (k) -> new ArrayList<>());
                    result.get(layoutName).add(x);
                });
        return result;
    }
}
