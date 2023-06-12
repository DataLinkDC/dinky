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
import org.dinky.data.model.Metrics;
import org.dinky.data.vo.MetricsVO;
import org.dinky.process.exception.DinkyException;
import org.dinky.service.MonitorService;
import org.dinky.utils.PaimonUtil;

import org.apache.paimon.data.Timestamp;
import org.apache.paimon.predicate.Predicate;
import org.apache.paimon.predicate.PredicateBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.thread.ThreadUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitorServiceImpl implements MonitorService {
    private final Executor scheduleRefreshMonitorDataExecutor;
    private final MetricConfig metricConfig;

    public List<MetricsVO> getData(Date startTime, Date endTime) {
        endTime = Opt.ofNullable(endTime).orElse(DateUtil.date());
        if (endTime.compareTo(startTime) < 1) {
            throw new DinkyException("The end date must be greater than the start date!");
        }
        Function<PredicateBuilder, List<Predicate>> filter =
                p -> {
                    Timestamp timestamp =
                            Timestamp.fromLocalDateTime(DateUtil.toLocalDateTime(startTime));
                    Predicate greaterThan = p.greaterThan(0, timestamp);
                    Predicate local = p.equal(2, "local");
                    return CollUtil.newArrayList(local, greaterThan);
                };
        return PaimonUtil.batchReadTable(PaimonUtil.METRICS_IDENTIFIER, MetricsVO.class, filter);
    }

    public SseEmitter sendLatestData(SseEmitter sseEmitter, Date lastDate) {
        Queue<Metrics> metricsQueue = MetricConfig.getMetricsQueue();
        scheduleRefreshMonitorDataExecutor.execute(
                () -> {
                    try {
                        for (Metrics metrics : metricsQueue) {
                            if (metrics.getHeartTime()
                                    .isAfter(DateUtil.toLocalDateTime(lastDate))) {
                                sseEmitter.send(MetricsVO.of(metrics));
                            }
                        }
                        while (true) {
                            if (CollUtil.isEmpty(metricsQueue)) {
                                continue;
                            }
                            for (Metrics metrics : metricsQueue) {
                                sseEmitter.send(MetricsVO.of(metrics));
                            }
                            ThreadUtil.sleep(MetricConfig.SCHEDULED_RATE - 200);
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
}
