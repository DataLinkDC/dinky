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

import org.dinky.context.TenantContextHolder;
import org.dinky.data.annotation.GaugeM;
import org.dinky.data.metrics.BaseMetrics;
import org.dinky.data.metrics.Cpu;
import org.dinky.data.metrics.Jvm;
import org.dinky.data.metrics.Mem;
import org.dinky.data.metrics.MetricsTotal;
import org.dinky.data.model.History;
import org.dinky.data.model.JobInstance;
import org.dinky.data.model.Metrics;
import org.dinky.data.vo.MetricsVO;
import org.dinky.service.HistoryService;
import org.dinky.service.JobInstanceService;
import org.dinky.service.MonitorService;
import org.dinky.utils.HttpUtils;
import org.dinky.utils.PaimonUtil;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.thread.AsyncUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class MetricConfig {

    private final MeterRegistry registry;
    private final HistoryService historyService;
    private final JobInstanceService jobInstanceService;
    private final MonitorService monitorService;
    private final Executor scheduleRefreshMonitorDataExecutor;
    private static final Queue<MetricsVO> metricsQueue = new ConcurrentLinkedQueue<>();
    /** task可用的url */
    private static final Map<Integer, FlinkMetrics> TASK_FLINK_METRICS_MAP =
            new ConcurrentHashMap<>();

    private static final Map<LocalDateTime, List<FlinkMetrics>> FLINK_METRICS_DATA_MAP =
            new ConcurrentHashMap<>();
    public static final int SCHEDULED_RATE = 1000;
    public static final int REQUEST_FLINK_TIMEOUT = 100;

    /** Update status per second */
    @Scheduled(fixedRate = SCHEDULED_RATE)
    public void scheduled() {
        updateState();
        LocalDateTime now = LocalDateTime.now();
        TimeInterval timer = DateUtil.timer();
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
        metricsQueue.add(metricsVO);
        FLINK_METRICS_DATA_MAP.remove(now);
    }

    /** Entering the lake every 10 states */
    @Scheduled(fixedRate = 10000)
    @PreDestroy
    public void writeScheduled() {
        PaimonUtil.writeMetrics(new ArrayList<>(metricsQueue));
        metricsQueue.clear();
    }

    @PostConstruct
    public void init() {
        MetricsTotal metricsTotal = MetricsTotal.instance;
        registerMetrics(metricsTotal.getJvm());
        registerMetrics(metricsTotal.getCpu());
        registerMetrics(metricsTotal.getMem());

        getAndCheckFlinkUrlAvailable();
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
                                Map<String, List<String>> verticesAndMetricsMap =
                                        flinkMetrics.getVerticesAndMetricsMap();
                                verticesAndMetricsMap.putIfAbsent(
                                        m.getVertices(), new ArrayList<>());
                                verticesAndMetricsMap.get(m.getVertices()).add(m.getMetrics());
                            });
            for (History jobHistory : historyList) {
                if (jobInstance.getHistoryId().equals(jobHistory.getId())) {
                    String hosts = jobHistory.getJobManagerAddress();
                    List<String> hostList = StrUtil.split(hosts, ",");
                    for (String host : hostList) {
                        HttpUtil.createGet(host + "/config")
                                .timeout(REQUEST_FLINK_TIMEOUT)
                                .then(
                                        resp -> {
                                            TASK_FLINK_METRICS_MAP.get(taskId).getUrls().add(host);
                                        });
                    }
                    break;
                }
            }
        }
    }

    public void addFlinkMetrics(FlinkMetrics flinkMetrics, LocalDateTime now) {
        Integer taskId = flinkMetrics.getTaskId();
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
                            for (String metrics : m) {
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
                                        REQUEST_FLINK_TIMEOUT,
                                        x -> {
                                            JSONArray array = JSONUtil.parseArray(x.body());
                                            String key = v + metrics;
                                            String value = array.getJSONObject(0).getStr("value");
                                            flinkMetrics.getValueMap().put(key, value);
                                        });
                            }
                        });
        FLINK_METRICS_DATA_MAP.get(now).add(flinkMetrics);
    }

    public static Queue<MetricsVO> getMetricsQueue() {
        return metricsQueue;
    }

    @Setter
    @Getter
    static class FlinkMetrics {
        private String jobId;
        private Integer taskId;
        private List<String> urls = new CopyOnWriteArrayList<>();
        private Map<String, List<String>> verticesAndMetricsMap = new HashMap<>();
        /** key为 vertices+metrics */
        private Map<String, String> valueMap = new ConcurrentHashMap<>();
    }
}
