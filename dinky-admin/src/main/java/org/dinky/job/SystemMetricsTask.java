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

package org.dinky.job;

import org.dinky.daemon.task.DaemonTask;
import org.dinky.daemon.task.DaemonTaskConfig;
import org.dinky.data.annotations.GaugeM;
import org.dinky.data.metrics.BaseMetrics;
import org.dinky.data.metrics.MetricsTotal;
import org.dinky.job.handler.SystemMetricsHandler;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.springframework.context.annotation.DependsOn;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.ReflectUtil;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@DependsOn("springContextUtils")
@Slf4j
@Data
public class SystemMetricsTask implements DaemonTask {
    private final MeterRegistry registry = new CompositeMeterRegistry();

    private DaemonTaskConfig config;
    public static final String TYPE = SystemMetricsTask.class.toString();

    @Override
    public DaemonTask setConfig(DaemonTaskConfig config) {
        this.config = config;
        MetricsTotal metricsTotal = MetricsTotal.instance;
        registerMetrics(metricsTotal.getJvm());
        registerMetrics(metricsTotal.getCpu());
        registerMetrics(metricsTotal.getMem());

        return this;
    }

    @Override
    public boolean dealTask() {
        SystemMetricsHandler.refresh();
        return false;
    }

    private void registerMetrics(BaseMetrics baseMetrics) {
        Field[] baseFields = ReflectUtil.getFields(this.getClass());
        Field baseField = Arrays.stream(baseFields)
                .filter(field -> field.getType().equals(baseMetrics.getClass()))
                .findFirst()
                .orElse(null);
        if (baseField == null) {
            return;
        }
        Field[] fields = ReflectUtil.getFields(baseMetrics.getClass());
        for (Field field : fields) {
            GaugeM gaugeM = AnnotationUtil.getAnnotation(field, GaugeM.class);
            Opt.ofNullable(gaugeM).ifPresent(g -> Gauge.builder(gaugeM.name(), () ->
                            (Number) ReflectUtil.getFieldValue(ReflectUtil.getFieldValue(this, baseField), field))
                    .baseUnit(g.baseUnit())
                    .tags(g.tags())
                    .description(gaugeM.description())
                    .register(registry));
        }
    }

    @Override
    public DaemonTaskConfig getConfig() {
        return config;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
