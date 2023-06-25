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
import org.dinky.context.TenantContextHolder;
import org.dinky.data.model.Configuration;
import org.dinky.data.model.History;
import org.dinky.data.model.JobInstance;
import org.dinky.data.model.Metrics;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.vo.MetricsVO;
import org.dinky.service.HistoryService;
import org.dinky.service.JobInstanceService;
import org.dinky.service.MonitorService;
import org.dinky.utils.HttpUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.AsyncUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FlinkMetricsIndicator extends BaseSchedule {
    private final HistoryService historyService;
    private final JobInstanceService jobInstanceService;
    private final MonitorService monitorService;
    public AtomicReference<Integer> flinkMetricsRequestTimeout =
            new AtomicReference<>(
                    SystemConfiguration.getInstances()
                            .getFlinkMetricsGatherTimeout()
                            .getDefaultValue());

    /** task可用的url */
    private static final Map<Integer, FlinkMetrics> TASK_FLINK_METRICS_MAP =
            new ConcurrentHashMap<>();

    private static final Map<LocalDateTime, List<FlinkMetrics>> FLINK_METRICS_DATA_MAP =
            new ConcurrentHashMap<>();

    public void writeFlinkMetrics() {
        LocalDateTime now = LocalDateTime.now();
        FLINK_METRICS_DATA_MAP.put(now, new CopyOnWriteArrayList<>());
        CompletableFuture<?>[] array =
                TASK_FLINK_METRICS_MAP.values().stream()
                        .map((f) -> CompletableFuture.runAsync(() -> addFlinkMetrics(f, now)))
                        .toArray(CompletableFuture[]::new);
        AsyncUtil.waitAll(array);
        MetricsVO metricsVO = new MetricsVO();
        metricsVO.setModel("flink");
        metricsVO.setHeartTime(now);
        metricsVO.setContent(JSONUtil.toJsonStr(FLINK_METRICS_DATA_MAP.get(now)));
        MetricConfig.getMetricsQueue().add(metricsVO);
        FLINK_METRICS_DATA_MAP.remove(now);
    }

    @PostConstruct
    public void init() {
        getAndCheckFlinkUrlAvailable();

        Configuration<Integer> flinkMetricsGatherTimeout =
                SystemConfiguration.getInstances().getFlinkMetricsGatherTimeout();
        flinkMetricsGatherTimeout.addChangeEvent(flinkMetricsRequestTimeout::set);

        Configuration<Integer> flinkMetricsGatherTiming =
                SystemConfiguration.getInstances().getFlinkMetricsGatherTiming();
        final String key = flinkMetricsGatherTiming.getKey();
        flinkMetricsGatherTiming.addChangeEvent(
                time -> {
                    removeSchedule(key);
                    addSchedule(key, this::writeFlinkMetrics, new PeriodicTrigger(time));
                });
    }

    public void getAndCheckFlinkUrlAvailable() {
        TenantContextHolder.set(1);
        List<JobInstance> jobInstances = jobInstanceService.listJobInstanceActive();
        if (CollUtil.isEmpty(jobInstances)) {
            return;
        }
        List<History> historyList =
                historyService.listByIds(
                        jobInstances.stream()
                                .map(JobInstance::getHistoryId)
                                .collect(Collectors.toList()));
        List<Metrics> metricsList = monitorService.list();
        Set<Integer> taskIdSet =
                metricsList.stream().map(Metrics::getTaskId).collect(Collectors.toSet());
        for (JobInstance jobInstance : jobInstances) {
            Integer taskId = jobInstance.getTaskId();
            if (!taskIdSet.contains(taskId)) {
                continue;
            }
            FlinkMetrics flinkMetrics = new FlinkMetrics();
            flinkMetrics.setTaskId(taskId);
            flinkMetrics.setJobId(jobInstance.getJid());
            TASK_FLINK_METRICS_MAP.put(taskId, flinkMetrics);
            metricsList.stream()
                    .filter(x -> x.getTaskId().equals(taskId))
                    .forEach(
                            m -> {
                                Map<String, Map<String, String>> verticesAndMetricsMap =
                                        flinkMetrics.getVerticesAndMetricsMap();
                                verticesAndMetricsMap.putIfAbsent(
                                        m.getVertices(), new ConcurrentHashMap<>());
                                verticesAndMetricsMap.get(m.getVertices()).put(m.getMetrics(), "");
                            });
            for (History jobHistory : historyList) {
                if (jobInstance.getHistoryId().equals(jobHistory.getId())) {
                    String hosts = jobHistory.getJobManagerAddress();
                    List<String> hostList = StrUtil.split(hosts, ",");
                    for (String host : hostList) {
                        try {
                            HttpUtil.createGet(host + "/config")
                                    .timeout(flinkMetricsRequestTimeout.get())
                                    .then(
                                            resp ->
                                                    TASK_FLINK_METRICS_MAP
                                                            .get(taskId)
                                                            .getUrls()
                                                            .add(host));
                        } catch (Exception e) {
                            log.warn("host read Timeout:{}", host);
                        }
                    }
                    break;
                }
            }
        }
    }

    public void addFlinkMetrics(FlinkMetrics flinkMetrics, LocalDateTime now) {
        List<String> urlList = flinkMetrics.getUrls();
        if (CollUtil.isEmpty(urlList)) {
            // todo url都挂掉，需要做异常通知处理，和计数淘汰此flink metrics
            log.error("");
            return;
        }

        // http://10.8.16.125:8282/jobs/06ccde3ff6e53bafe729e0e50fca72fd/vertices/cbc357ccb763df2852fee8c4fc7d55f2/metrics?get=0.buffers.inputExclusiveBuffersUsage
        flinkMetrics
                .getVerticesAndMetricsMap()
                .forEach(
                        (v, m) -> {
                            for (String metrics : m.keySet()) {
                                if (CollUtil.isEmpty(urlList)) {
                                    return;
                                }
                                HttpUtils.asyncRequest(
                                        flinkMetrics.getUrls(),
                                        "/jobs/"
                                                + flinkMetrics.getJobId()
                                                + "/vertices/"
                                                + v
                                                + "/metrics?get="
                                                + metrics,
                                        flinkMetricsRequestTimeout.get(),
                                        x -> {
                                            JSONArray array = JSONUtil.parseArray(x.body());
                                            String value = array.getJSONObject(0).getStr("value");
                                            m.put(metrics, value);
                                        });
                            }
                        });
        FLINK_METRICS_DATA_MAP.get(now).add(flinkMetrics);
    }

    @Setter
    @Getter
    static class FlinkMetrics {
        private String jobId;
        private Integer taskId;
        private List<String> urls = new CopyOnWriteArrayList<>();
        /** jobId -> metricsId -> metricsValue */
        private Map<String, Map<String, String>> verticesAndMetricsMap = new HashMap<>();
    }
}
