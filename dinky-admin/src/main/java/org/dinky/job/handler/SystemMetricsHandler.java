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

package org.dinky.job.handler;

import org.dinky.context.MetricsContextHolder;
import org.dinky.data.enums.MetricsType;
import org.dinky.data.metrics.Cpu;
import org.dinky.data.metrics.Jvm;
import org.dinky.data.metrics.Mem;
import org.dinky.data.metrics.MetricsTotal;
import org.dinky.data.vo.MetricsVO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SystemMetricsHandler {

    public static void refresh() {
        log.debug("Collecting jvm related information.");
        MetricsTotal metricsTotal = MetricsTotal.instance;
        LocalDateTime now = LocalDateTime.now();

        metricsTotal.setJvm(Jvm.of());
        metricsTotal.setCpu(Cpu.of());
        metricsTotal.setMem(Mem.of());

        MetricsVO metrics = new MetricsVO();
        metrics.setContent(metricsTotal);
        metrics.setHeartTime(now);
        metrics.setModel(MetricsType.LOCAL.getType());
        metrics.setDate(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        MetricsContextHolder.getInstances().sendAsync(metrics.getModel(), metrics);

        log.debug("Collecting jvm information ends.");
    }
}
