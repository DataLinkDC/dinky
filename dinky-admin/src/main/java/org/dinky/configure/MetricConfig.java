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

import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.vo.MetricsVO;
import org.dinky.utils.PaimonUtil;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class MetricConfig {

    private static final Queue<MetricsVO> METRICS_QUEUE = new ConcurrentLinkedQueue<>();

    public int flinkMetricsRequestTimeout =
            SystemConfiguration.getInstances().getFlinkMetricsGatherTimeout().getDefaultValue();

    /** Update status per second */

    /** Entering the lake every 10 states */
    @Scheduled(fixedRate = 10000)
    @PreDestroy
    public void writeScheduled() {
        PaimonUtil.writeMetrics(new ArrayList<>(METRICS_QUEUE));
        METRICS_QUEUE.clear();
    }

    public static Queue<MetricsVO> getMetricsQueue() {
        return METRICS_QUEUE;
    }
}
