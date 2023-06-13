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

package org.dinky.configure;

import org.dinky.data.annotation.GaugeM;
import org.dinky.data.metrics.BaseMetrics;
import org.dinky.data.metrics.Cpu;
import org.dinky.data.metrics.Jvm;
import org.dinky.data.metrics.Mem;
import org.dinky.data.metrics.MetricsTotal;
import org.dinky.data.vo.MetricsVO;
import org.dinky.utils.PaimonUtil;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Configuration
@RequiredArgsConstructor
@Getter
@Setter
@EnableScheduling
public class MetricConfig {

    private final MeterRegistry registry;
    private ThreadPoolExecutor writePool =
            new ThreadPoolExecutor(5, 10, 1, TimeUnit.DAYS, new SynchronousQueue<>());
    private static final Queue<MetricsVO> metricsQueue = new ConcurrentLinkedQueue<>();
    public static final int SCHEDULED_RATE = 1000;

    /** Update status per second */
    @Scheduled(fixedRate = SCHEDULED_RATE)
    public void scheduled() {
        updateState();
    }

    /** Entering the lake every 10 states */
    @Scheduled(fixedRate = 10000)
    @PreDestroy
    public void writeScheduled() {
        PaimonUtil.writeMetrics(new ArrayList<>(metricsQueue));
        metricsQueue.clear();
        //        PaimonUtil.batchReadTable(PaimonUtil.METRICS_IDENTIFIER,Metrics.class,p->
        // Collections.singletonList(p.greaterThan(0,
        // Timestamp.fromLocalDateTime(DateUtil.toLocalDateTime(DateUtil.date(1686300257085L))))));
        PaimonUtil.batchReadTable(PaimonUtil.METRICS_IDENTIFIER, MetricsVO.class);
    }

    @PostConstruct
    public void init() {
        MetricsTotal metricsTotal = MetricsTotal.instance;
        registerMetrics(metricsTotal.getJvm());
        registerMetrics(metricsTotal.getCpu());
        registerMetrics(metricsTotal.getMem());
    }

    public void updateState() {
        MetricsTotal metricsTotal = MetricsTotal.instance;
        LocalDateTime now = LocalDateTime.now();

        metricsTotal.setJvm(Jvm.of());
        metricsTotal.setCpu(Cpu.of());
        metricsTotal.setMem(Mem.of());

        MetricsVO metrics = new MetricsVO();
        metrics.setContent(JSONUtil.toJsonStr(metricsTotal));
        metrics.setHeartTime(now);
        metrics.setModel("local");
        metricsQueue.add(metrics);

        //        writePool.execute(metrics::insert);
    }

    public void registerMetrics(BaseMetrics baseMetrics) {
        Field[] baseFields = ReflectUtil.getFields(this.getClass());
        Field baseField =
                Arrays.stream(baseFields)
                        .filter(field -> field.getType().equals(baseMetrics.getClass()))
                        .findFirst()
                        .orElse(null);
        if (baseField == null) {
            return;
        }
        Field[] fields = ReflectUtil.getFields(baseMetrics.getClass());
        for (Field field : fields) {
            GaugeM gaugeM = AnnotationUtil.getAnnotation(field, GaugeM.class);
            Opt.ofNullable(gaugeM)
                    .ifPresent(
                            g ->
                                    Gauge.builder(
                                                    gaugeM.name(),
                                                    () ->
                                                            (Number)
                                                                    ReflectUtil.getFieldValue(
                                                                            ReflectUtil
                                                                                    .getFieldValue(
                                                                                            this,
                                                                                            baseField),
                                                                            field))
                                            .baseUnit(g.baseUnit())
                                            .tags(g.tags())
                                            .description(gaugeM.description())
                                            .register(registry));
        }
    }

    public static Queue<MetricsVO> getMetricsQueue() {
        return metricsQueue;
    }
}
