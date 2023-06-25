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

package org.dinky.configure.schedule.metrics;

import org.dinky.configure.MetricConfig;
import org.dinky.configure.schedule.BaseSchedule;
import org.dinky.data.annotation.GaugeM;
import org.dinky.data.metrics.BaseMetrics;
import org.dinky.data.metrics.Cpu;
import org.dinky.data.metrics.Jvm;
import org.dinky.data.metrics.Mem;
import org.dinky.data.metrics.MetricsTotal;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.vo.MetricsVO;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GatherSysIndicator extends BaseSchedule {
    private final MeterRegistry registry;

    @PostConstruct
    public void init() {
        MetricsTotal metricsTotal = MetricsTotal.instance;
        registerMetrics(metricsTotal.getJvm());
        registerMetrics(metricsTotal.getCpu());
        registerMetrics(metricsTotal.getMem());

        org.dinky.data.model.Configuration<Boolean> metricsSysEnable =
                SystemConfiguration.getInstances().getMetricsSysEnable();
        String key = metricsSysEnable.getKey();

        metricsSysEnable.addChangeEvent(
                x -> {
                    if (x) {
                        addSchedule(
                                key,
                                this::updateState,
                                new PeriodicTrigger(
                                        SystemConfiguration.getInstances()
                                                .getMetricsSysGatherTiming()
                                                .getValue()));
                    } else {
                        removeSchedule(key);
                        log.info("Information collection for jvm is turned off（已关闭对jvm的信息收集）");
                    }
                });
    }

    public void updateState() {
        log.debug("Collecting jvm related information.");
        MetricsTotal metricsTotal = MetricsTotal.instance;
        LocalDateTime now = LocalDateTime.now();

        metricsTotal.setJvm(Jvm.of());
        metricsTotal.setCpu(Cpu.of());
        metricsTotal.setMem(Mem.of());

        MetricsVO metrics = new MetricsVO();
        metrics.setContent(JSONUtil.toJsonStr(metricsTotal));
        metrics.setHeartTime(now);
        metrics.setModel("local");
        MetricConfig.getMetricsQueue().add(metrics);

        log.debug("Collecting jvm information ends.");
    }

    private void registerMetrics(BaseMetrics baseMetrics) {
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
}
