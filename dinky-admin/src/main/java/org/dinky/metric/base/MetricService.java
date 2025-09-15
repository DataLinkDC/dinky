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

package org.dinky.metric.base;

import org.dinky.api.FlinkAPI;
import org.dinky.daemon.pool.ScheduleThreadPool;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.model.Task;
import org.dinky.data.model.ext.JobInfoDetail;
import org.dinky.data.model.job.JobInstance;
import org.dinky.service.JobInstanceService;
import org.dinky.service.TaskService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.PeriodicTrigger;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class MetricService<T> {

    @Autowired
    private JobInstanceService jobInstanceService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ScheduleThreadPool schedule;

    private HashMap<MetricType, T> metricCaches = new HashMap<>();

    private boolean isScheduleStart = false;

    protected abstract T formatFlinkJobMetrics(List<HashMap<String, Object>> metrics);

    protected abstract T mergeFlinkJobMetrics(List<T> metricGroups);

    public T retrieveAllFlinkJobMetrics(List<MetricType> types) {
        if (!this.isScheduleStart) {
            synchronized (this) {
                if (!this.isScheduleStart) {
                    this.isScheduleStart = true;
                    schedule.addSchedule(
                            "retrieve.all.flink.status.metrics",
                            () -> this.runRetrieveAllFlinkJobMetrics(MetricType.STATUS),
                            new PeriodicTrigger(1, TimeUnit.MINUTES));
                    schedule.addSchedule(
                            "retrieve.all.flink.job.metrics",
                            () -> this.runRetrieveAllFlinkJobMetrics(MetricType.JOBMANAGER),
                            new PeriodicTrigger(1, TimeUnit.MINUTES));
                    schedule.addSchedule(
                            "retrieve.all.flink.task.metrics",
                            () -> this.runRetrieveAllFlinkJobMetrics(MetricType.TASKMANAGER),
                            new PeriodicTrigger(1, TimeUnit.MINUTES));
                    schedule.addSchedule(
                            "retrieve.all.flink.vertices.metrics",
                            () -> this.runRetrieveAllFlinkJobMetrics(MetricType.VERTICES),
                            new PeriodicTrigger(2, TimeUnit.MINUTES));
                }
            }
        }
        List<MetricType> _types = types == null || types.isEmpty()
                ? Arrays.asList(MetricType.STATUS, MetricType.JOBMANAGER, MetricType.TASKMANAGER, MetricType.VERTICES)
                : types;
        List<T> metricGroups = this.metricCaches.entrySet().stream()
                .filter(e -> _types.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        return mergeFlinkJobMetrics(metricGroups);
    }

    private void runRetrieveAllFlinkJobMetrics(MetricType type) {
        List<JobInstance> jobInstances = jobInstanceService.listAllJobInstances();
        log.info("jobInstance count: {}", jobInstances.size());
        T allMetrics = this.runRetrieveAllFlinkJobMetricsWithJobList(jobInstances, type);
        this.metricCaches.put(type, allMetrics);
    }

    private T runRetrieveAllFlinkJobMetricsWithJobList(List<JobInstance> jobInstances, MetricType type) {
        List<HashMap<String, Object>> metrics = new ArrayList<>();
        for (JobInstance jobInstance : jobInstances) {
            try {
                JobInfoDetail jobInfoDetail = jobInstanceService.getJobInfoDetail(jobInstance.getId());
                if (type == MetricType.STATUS) {
                    metrics.add(this.retrieveFlinkJobStatusMetricByJobInfoDetail(jobInfoDetail));
                } else if (jobInstance.getStatus().equals(JobStatus.RUNNING.toString())) {
                    switch (type) {
                        case JOBMANAGER:
                            metrics.addAll(this.retrieveTaskManagerMetricsByJobInfoDetail(jobInfoDetail));
                            break;
                        case TASKMANAGER:
                            metrics.addAll(this.retrieveJobManagerMetricsByJobInfoDetail(jobInfoDetail));
                            break;
                        case VERTICES:
                            metrics.addAll(this.retrieveJobVerticesMetricsByJobInfoDetail(jobInfoDetail));
                            break;
                    }
                }
            } catch (Exception e) {
                log.error("JobInstance {} failed: {}", jobInstance.getId(), e.getMessage(), e);
            }
        }
        return this.formatFlinkJobMetrics(metrics);
    }

    private HashMap<String, Object> retrieveFlinkJobStatusMetricByJobInfoDetail(JobInfoDetail jobInfoDetail) {
        HashMap<String, Object> metric = this.retrieveJobInfoMetrics(jobInfoDetail);
        metric.put("name", MetricNames.DINKY_FLINK_TASK_IS_RUNNING);
        metric.put("value", jobInfoDetail.getInstance().getStatus().equals(JobStatus.RUNNING.toString()) ? 1 : 0);
        return metric;
    }

    private List<HashMap<String, Object>> retrieveTaskManagerMetricsByJobInfoDetail(JobInfoDetail jobInfoDetail) {

        FlinkAPI flinkAPI = FlinkAPI.build(jobInfoDetail.getClusterInstance().getJobManagerHost());
        List<JsonNode> taskManagers =
                Lists.newArrayList(flinkAPI.getTaskManagers().get("taskmanagers"));
        return taskManagers.parallelStream()
                .flatMap(taskManager -> {
                    List<HashMap<String, Object>> metricList = new ArrayList();
                    String taskManagerId = taskManager.get("id").asText();
                    JsonNode taskManagerMetrics = flinkAPI.getTaskManagerMetrics(taskManagerId);
                    for (JsonNode metric : Lists.newArrayList(taskManagerMetrics)) {
                        HashMap<String, String> other = new HashMap<>(Map.of(
                                MetricKeys.DINKY_FLINK_TASK_MANAGER_ID,
                                taskManagerId.substring(
                                        jobInfoDetail.getInstance().getName().length() + 1)));
                        metricList.add(buildMetric(jobInfoDetail, metric, other));
                    }
                    return metricList.stream();
                })
                .collect(Collectors.toList());
    }

    private List<HashMap<String, Object>> retrieveJobManagerMetricsByJobInfoDetail(JobInfoDetail jobInfoDetail) {
        List<HashMap<String, Object>> metricList = new ArrayList();
        FlinkAPI flinkAPI = FlinkAPI.build(jobInfoDetail.getClusterInstance().getJobManagerHost());
        JsonNode jobManagerMetrics = flinkAPI.getJobManagerMetrics();
        for (JsonNode metric : Lists.newArrayList(jobManagerMetrics)) {
            metricList.add(buildMetric(jobInfoDetail, metric));
        }
        return metricList;
    }

    private List<HashMap<String, Object>> retrieveJobVerticesMetricsByJobInfoDetail(JobInfoDetail jobInfoDetail) {
        FlinkAPI flinkAPI = FlinkAPI.build(jobInfoDetail.getClusterInstance().getJobManagerHost());
        String jid = jobInfoDetail.getInstance().getJid();
        return flinkAPI.getVertices(jid).parallelStream()
                .flatMap(vertice -> {
                    List<HashMap<String, Object>> metricList = new ArrayList<>();
                    HashMap<String, List<String>> whitelist =
                            this.getWhitelistMetricsFromJobVertices(jobInfoDetail, vertice);
                    if (!whitelist.isEmpty()) {
                        HashMap<String, String> other =
                                new HashMap<>(Map.of(MetricKeys.DINKY_FLINK_TASK_VERTICE_ID, vertice));
                        for (String name : whitelist.keySet()) {
                            List<JsonNode> verticeMetrics = new ArrayList<>();
                            for (List<String> metricGroup : Lists.partition(whitelist.get(name), 15)) {
                                JsonNode groupMetrics =
                                        flinkAPI.getJobMetricsData(jid, vertice, String.join(",", metricGroup));
                                verticeMetrics.addAll(Lists.newArrayList(groupMetrics));
                            }
                            long value = this.getVerticeMetricValueByMetricName(name, verticeMetrics);
                            metricList.add(buildMetric(jobInfoDetail, name, value, other));
                        }
                    }
                    metricList.addAll(
                            this.retrieveJobVerticeBackPressureMetricsByJobInfoDetail(jobInfoDetail, vertice));
                    return metricList.stream();
                })
                .collect(Collectors.toList());
    }

    private long getVerticeMetricValueByMetricName(String dinkyMetricName, List<JsonNode> verticeMetrics) {
        List<Long> values = new ArrayList<>();
        for (JsonNode metric : verticeMetrics) {
            if (metric.has("value") && !metric.get("value").isNull()) {
                values.add(metric.get("value").asLong());
            }
        }
        if (values.isEmpty()) {
            return 0L;
        }
        switch (dinkyMetricName) {
            case MetricNames.DINKY_FLINK_TASK_RECORDS_CONSUMED_RATE:
            case MetricNames.DINKY_FLINK_TASK_BYTES_CONSUMED_RATE:
                return (long) values.stream().mapToLong(Long::longValue).sum();

            case MetricNames.DINKY_FLINK_TASK_RECORDS_LAG_MAX:
                return values.stream().mapToLong(Long::longValue).max().orElse(0);

            case MetricNames.DINKY_FLINK_TASK_CURRENT_OFFSET:
            case MetricNames.DINKY_FLINK_TASK_COMMITTED_OFFSET:
                return values.stream().mapToLong(Long::longValue).max().orElse(0);

            case MetricNames.DINKY_FLINK_TASK_LAST_CHECKPOINT_DURATION:
            case MetricNames.DINKY_FLINK_TASK_LAST_CHECKPOINT_SIZE:
                return values.stream().mapToLong(Long::longValue).max().orElse(0);

            default:
                return (long) values.stream().mapToLong(Long::longValue).sum();
        }
    }

    private HashMap<String, List<String>> getWhitelistMetricsFromJobVertices(
            JobInfoDetail jobInfoDetail, String vertice) {
        FlinkAPI flinkAPI = FlinkAPI.build(jobInfoDetail.getClusterInstance().getJobManagerHost());
        JsonNode metrics =
                flinkAPI.getJobMetricsItems(jobInfoDetail.getInstance().getJid(), vertice);

        HashMap<String, List<String>> whitelist = new HashMap<>();
        for (JsonNode metric : metrics) {
            String id = metric.get("id").asText();
            MetricNames.FLINK_VERTICE_MERTICS_MAP.keySet().stream().forEach(m -> {
                if (id.contains(m)) {
                    String name = MetricNames.FLINK_VERTICE_MERTICS_MAP.get(m);
                    if (MetricNames.HUDI_METRICS_LIST.stream().anyMatch(name::equals) && id.contains("hoodie")) {
                        name = name + "_HUDI";
                    }
                    whitelist.computeIfAbsent(name, k -> new ArrayList<>()).add(id);
                }
            });
        }
        return whitelist;
    }

    private HashMap<String, Object> buildMetric(
            JobInfoDetail jobInfoDetail, String name, long value, HashMap<String, String> other) {
        HashMap<String, Object> base = this.retrieveJobInfoMetrics(jobInfoDetail);
        HashMap<String, Object> m = new HashMap<>(base);
        m.put("name", name);
        m.put("value", value);
        if (other != null) {
            m.putAll(other);
        }
        return m;
    }

    private HashMap<String, Object> buildMetric(
            JobInfoDetail jobInfoDetail, JsonNode metric, HashMap<String, String> other) {
        HashMap<String, Object> base = this.retrieveJobInfoMetrics(jobInfoDetail);
        HashMap<String, Object> m = new HashMap<>(base);
        String name =
                "DINKY_FLINK_JOB_" + metric.get("id").asText().replace(".", "_").toUpperCase();
        long value = metric.get("value").asLong();
        m.put("name", name);
        m.put("value", value);
        if (other != null) {
            m.putAll(other);
        }
        return m;
    }

    private HashMap<String, Object> buildMetric(JobInfoDetail jobInfoDetail, JsonNode metric) {
        return this.buildMetric(jobInfoDetail, metric, null);
    }

    private List<HashMap<String, Object>> retrieveJobVerticeBackPressureMetricsByJobInfoDetail(
            JobInfoDetail jobInfoDetail, String vertice) {
        FlinkAPI flinkAPI = FlinkAPI.build(jobInfoDetail.getClusterInstance().getJobManagerHost());
        JsonNode backPressure =
                flinkAPI.getBackPressureJson(jobInfoDetail.getInstance().getJid(), vertice);

        int backpressureLevel = mapJobVerticeBackPressureLevel(
                backPressure.get("backpressureLevel").asText());

        List<Double> ratios = StreamSupport.stream(backPressure.get("subtasks").spliterator(), false)
                .map(subtask -> subtask.get("ratio").asDouble())
                .collect(Collectors.toList());

        double backpressureRateMin =
                ratios.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
        double backpressureRateMax =
                ratios.stream().mapToDouble(Double::doubleValue).max().orElse(1.0);

        HashMap<String, Object> baseMetrics = this.retrieveJobInfoMetrics(jobInfoDetail);

        List<HashMap<String, Object>> metricList = new ArrayList<>();
        Map<String, Object> metricsMap = Map.of(
                MetricNames.DINKY_FLINK_TASK_BACKPRESSURE_LEVEL, backpressureLevel,
                MetricNames.DINKY_FLINK_TASK_BACKPRESSURE_RATE_MAX, backpressureRateMax,
                MetricNames.DINKY_FLINK_TASK_BACKPRESSURE_RATE_MIN, backpressureRateMin);

        metricsMap.forEach((name, value) -> {
            HashMap<String, Object> metric = new HashMap<>(baseMetrics);
            metric.put("name", name);
            metric.put("value", value);
            metric.put(MetricKeys.DINKY_FLINK_TASK_VERTICE_ID, vertice);
            metricList.add(metric);
        });

        return metricList;
    }

    private int mapJobVerticeBackPressureLevel(String backpressureLevel) {
        switch (backpressureLevel.toLowerCase()) {
            case "ok":
                return 1;
            case "low":
                return 2;
            case "medium":
                return 3;
            case "high":
                return 4;
            default:
                return 0;
        }
    }

    public HashMap<String, Object> retrieveJobInfoMetrics(JobInfoDetail jobInfoDetail) {
        HashMap<String, Object> metrics = new HashMap();

        if (taskService != null) {
            Task task = taskService.getById(jobInfoDetail.getInstance().getTaskId());

            if (task != null) {
                metrics.put(
                        MetricKeys.DINKY_FLINK_TASK_STATUS,
                        jobInfoDetail.getInstance().getStatus());
                metrics.put(MetricKeys.DINKY_FLINK_TASK_DEPLOY_STATUS, task.getStep());
                metrics.put(MetricKeys.DINKY_FLINK_TASK_NAME, task.getName());
            }
        }

        if (!metrics.containsKey(MetricKeys.DINKY_FLINK_TASK_NAME)) {
            metrics.put(
                    MetricKeys.DINKY_FLINK_TASK_NAME,
                    jobInfoDetail.getInstance().getName());
        }

        metrics.put(MetricKeys.DINKY_FLINK_TASK_ID, jobInfoDetail.getInstance().getTaskId());

        if (jobInfoDetail.getClusterConfiguration() != null) {
            metrics.put(
                    MetricKeys.DINKY_FLINK_CLUSTER_NAME,
                    jobInfoDetail.getClusterConfiguration().getName());
        }
        return metrics;
    }
}
