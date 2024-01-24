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

package org.dinky.api;

import org.dinky.assertion.Asserts;
import org.dinky.data.constant.FlinkRestAPIConstant;
import org.dinky.data.constant.NetConstant;
import org.dinky.data.exception.BusException;
import org.dinky.gateway.enums.GatewayType;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.model.JobInfo;
import org.dinky.gateway.result.SavePointResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;

/**
 * FlinkAPI
 *
 * @since 2021/6/24 13:56
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class FlinkAPI {
    private static final Logger logger = LoggerFactory.getLogger(FlinkAPI.class);

    public static final String REST_TARGET_DIRECTORY = "rest.target-directory";
    public static final String ERRORS = "errors";
    public static final String CANCEL_JOB = "cancel-job";
    public static final String DRAIN = "drain";
    public static final String REQUEST_ID = "request-id";
    public static final String STATUS = "status";
    public static final String OPERATION = "operation";
    public static final String FAILURE_CAUSE = "failure-cause";
    public static final String LOCATION = "location";
    public static final String FLINK_VERSION = "flink-version";
    public static final String ID = "id";
    public static final String JOBS = "jobs";

    private final String address;
    private static final ObjectMapper mapper = new ObjectMapper();

    public FlinkAPI(String address) {
        this.address = address;
    }

    public static FlinkAPI build(String address) {
        return new FlinkAPI(address);
    }

    private JsonNode parse(String res) {
        try {
            return mapper.readTree(res);
        } catch (JsonProcessingException e) {
            logger.error("json parser error: ", e);
            return null;
        }
    }

    private JsonNode get(String route) {
        String res = getResult(route);
        return parse(res);
    }

    /**
     * get请求获取jobManager/TaskManager的日志 (结果为字符串并不是json格式)
     *
     * @param route route
     * @return {@link String}
     */
    private String getResult(String route) {
        return HttpUtil.get(NetConstant.HTTP + address + NetConstant.SLASH + route, NetConstant.SERVER_TIME_OUT_ACTIVE);
    }

    private JsonNode post(String route, String body) {
        String res = HttpUtil.post(
                NetConstant.HTTP + address + NetConstant.SLASH + route, body, NetConstant.SERVER_TIME_OUT_ACTIVE);
        return parse(res);
    }

    private JsonNode patch(String route, String body) {
        String res = HttpUtil.createRequest(Method.PATCH, NetConstant.HTTP + address + NetConstant.SLASH + route)
                .timeout(NetConstant.SERVER_TIME_OUT_ACTIVE)
                .body(body)
                .execute()
                .body();
        return parse(res);
    }

    public List<JsonNode> listJobs() {
        JsonNode result = get(FlinkRestAPIConstant.JOBSLIST);
        JsonNode jobs = result.get(JOBS);
        return jobs.isArray() ? Lists.newArrayList(jobs) : Lists.newArrayList();
    }

    public boolean stop(String jobId) {
        JsonNode result = get(FlinkRestAPIConstant.JOBS + jobId + FlinkRestAPIConstant.CANCEL);
        // TODO: 2023/1/11 return by result code
        return true;
    }

    @SuppressWarnings("checkstyle:Indentation")
    public SavePointResult savepoints(String jobId, SavePointType savePointType, Map<String, String> taskConfig) {
        JobInfo jobInfo = new JobInfo(jobId);
        Map<String, Object> paramMap = new HashMap<>(8);
        String paramType = null;
        switch (savePointType) {
            case CANCEL:
                paramMap.put(CANCEL_JOB, true);
                paramType = FlinkRestAPIConstant.SAVEPOINTS;
                jobInfo.setStatus(JobInfo.JobStatus.CANCEL);
                break;
            case STOP:
                paramMap.put(DRAIN, false);
                paramType = FlinkRestAPIConstant.STOP;
                jobInfo.setStatus(JobInfo.JobStatus.STOP);
                break;
            case TRIGGER:
                paramMap.put(CANCEL_JOB, false);
                paramType = FlinkRestAPIConstant.SAVEPOINTS;
                jobInfo.setStatus(JobInfo.JobStatus.RUN);
                break;
            default:
        }

        if (Asserts.isNotNull(taskConfig) && taskConfig.containsKey(REST_TARGET_DIRECTORY)) {
            paramMap.put(
                    REST_TARGET_DIRECTORY.substring(REST_TARGET_DIRECTORY.indexOf(".") + 1),
                    taskConfig.get(REST_TARGET_DIRECTORY));
        }

        JsonNode json = triggerSavePoint(jobId, paramMap, paramType);
        if (json == null) {
            return null;
        }

        return getSavePointResult(jobId, jobInfo, json);
    }

    private JsonNode triggerSavePoint(String jobId, Map<String, Object> paramMap, String paramType) {
        JsonNode json;
        try {
            json = post(FlinkRestAPIConstant.JOBS + jobId + paramType, mapper.writeValueAsString(paramMap));
        } catch (JsonProcessingException e) {
            logger.error("savePoints error: ", e);
            return null;
        }

        return json;
    }

    private SavePointResult getSavePointResult(String jobId, JobInfo jobInfo, JsonNode json) {
        SavePointResult result = SavePointResult.build(GatewayType.YARN_PER_JOB);
        JsonNode node;
        while (true) {
            try {
                Thread.sleep(1000);
                JsonNode errNode = json.get(ERRORS);
                if (Asserts.isNotNull(errNode)
                        && Asserts.isNotNullString(json.get(ERRORS).get(0).asText())) {
                    // 打印的可能是 堆栈 信息， 截取第一行关键信息即可
                    String errMsg = StrUtil.subBefore(json.get(ERRORS).get(0).asText(), "\n", false);
                    throw new Exception(errMsg);
                }
                node = get(FlinkRestAPIConstant.JOBS
                        + jobId
                        + FlinkRestAPIConstant.SAVEPOINTS
                        + NetConstant.SLASH
                        + json.get(REQUEST_ID).asText());
                String status = node.get(STATUS).get(ID).asText();
                if (!Asserts.isEquals(status, "IN_PROGRESS")) {
                    break;
                }
            } catch (Exception e) {
                throw new BusException(e.getMessage());
            }
        }

        if (node.get(OPERATION).has(FAILURE_CAUSE)) {
            String failureCause = node.get(OPERATION).get(FAILURE_CAUSE).toString();
            if (Asserts.isNotNullString(failureCause)) {
                result.fail(failureCause);
                return result;
            }
        }

        if (node.get(OPERATION).has(LOCATION)) {
            String location = node.get(OPERATION).get(LOCATION).asText();
            jobInfo.setSavePoint(location);
            result.setJobInfos(Lists.newArrayList(jobInfo));
            return result;
        }
        return result;
    }

    public String getVersion() {
        JsonNode result = get(FlinkRestAPIConstant.FLINK_CONFIG);
        if (Asserts.isNotNull(result) && Asserts.isNotNull(result.get(FLINK_VERSION))) {
            return result.get(FLINK_VERSION).asText();
        }
        return null;
    }

    public JsonNode getOverview() {
        return get(FlinkRestAPIConstant.OVERVIEW);
    }

    public JsonNode getJobInfo(String jobId) {
        return get(FlinkRestAPIConstant.JOBS + jobId);
    }

    public JsonNode getJobInfoSpecialItem(String jobId, String flinkRestAPIConstant) {
        return get(FlinkRestAPIConstant.JOBS + jobId + flinkRestAPIConstant);
    }

    public JsonNode getException(String jobId) {
        return getJobInfoSpecialItem(jobId, FlinkRestAPIConstant.EXCEPTIONS);
    }

    public JsonNode getCheckPoints(String jobId) {
        return getJobInfoSpecialItem(jobId, FlinkRestAPIConstant.CHECKPOINTS);
    }

    public JsonNode getCheckPointsConfig(String jobId) {
        return getJobInfoSpecialItem(jobId, FlinkRestAPIConstant.CHECKPOINTS_CONFIG);
    }

    public JsonNode getJobsConfig(String jobId) {
        return getJobInfoSpecialItem(jobId, FlinkRestAPIConstant.CONFIG);
    }

    /** @return JsonNode */
    public JsonNode getJobManagerMetrics() {
        return get(FlinkRestAPIConstant.JOB_MANAGER
                + FlinkRestAPIConstant.METRICS
                + FlinkRestAPIConstant.GET
                + buildMetricsParams(FlinkRestAPIConstant.JOB_MANAGER));
    }

    /** @return JsonNode */
    public JsonNode getJobManagerConfig() {
        return get(FlinkRestAPIConstant.JOB_MANAGER + FlinkRestAPIConstant.CONFIG);
    }

    /** @return JsonNode */
    public String getJobManagerLog() {
        return getResult(FlinkRestAPIConstant.JOB_MANAGER + FlinkRestAPIConstant.LOG);
    }
    /** @return JsonNode */
    public String getJobManagerThreadDump() {
        return getResult(FlinkRestAPIConstant.JOB_MANAGER + FlinkRestAPIConstant.THREAD_DUMP);
    }

    /** @return String */
    public String getJobManagerStdOut() {
        return getResult(FlinkRestAPIConstant.JOB_MANAGER + FlinkRestAPIConstant.STDOUT);
    }

    /** @return JsonNode */
    public JsonNode getJobManagerLogList() {
        return get(FlinkRestAPIConstant.JOB_MANAGER + FlinkRestAPIConstant.LOGS);
    }

    /**
     * @param logName 日志文件名
     * @return String
     */
    public String getJobManagerLogFileDetail(String logName) {
        return getResult(FlinkRestAPIConstant.JOB_MANAGER + FlinkRestAPIConstant.LOGS + logName);
    }

    /** @return JsonNode */
    public JsonNode getTaskManagers() {
        return get(FlinkRestAPIConstant.TASK_MANAGER);
    }

    /** @return String 可选值：task-manager, job-manager */
    public String buildMetricsParams(String type) {
        JsonNode jsonNode = get(type + FlinkRestAPIConstant.METRICS);
        if (jsonNode == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (JsonNode node : jsonNode) {
            if (Asserts.isNull(node)) {
                continue;
            }

            final JsonNode id = node.get(ID);
            if (Asserts.isNull(id)) {
                continue;
            }

            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(id.asText());
        }

        return sb.toString();
    }

    /** @return JsonNode */
    public JsonNode getTaskManagerMetrics(String containerId) {
        return get(FlinkRestAPIConstant.TASK_MANAGER
                + containerId
                + FlinkRestAPIConstant.METRICS
                + FlinkRestAPIConstant.GET
                + buildMetricsParams(FlinkRestAPIConstant.JOB_MANAGER));
    }

    /**
     * @param containerId 容器id
     * @return String
     */
    public String getTaskManagerLog(String containerId) {
        return getResult(FlinkRestAPIConstant.TASK_MANAGER + containerId + FlinkRestAPIConstant.LOG);
    }

    /**
     * @param containerId 容器id
     * @return JsonNode
     */
    public String getTaskManagerStdOut(String containerId) {
        return getResult(FlinkRestAPIConstant.TASK_MANAGER + containerId + FlinkRestAPIConstant.STDOUT);
    }

    /**
     * @param containerId 容器id
     * @return JsonNode
     */
    public JsonNode getTaskManagerLogList(String containerId) {
        return get(FlinkRestAPIConstant.TASK_MANAGER + containerId + FlinkRestAPIConstant.LOGS);
    }

    /**
     * @param logName 日志名称
     * @return String
     */
    public String getTaskManagerLogFileDetail(String containerId, String logName) {
        return getResult(FlinkRestAPIConstant.TASK_MANAGER + containerId + FlinkRestAPIConstant.LOGS + logName);
    }

    /** @return JsonNode */
    public JsonNode getTaskManagerThreadDump(String containerId) {
        return get(FlinkRestAPIConstant.TASK_MANAGER + containerId + FlinkRestAPIConstant.THREAD_DUMP);
    }

    public JsonNode getJobMetricsItems(String jobId, String verticeId) {
        return get(FlinkRestAPIConstant.JOBS
                + jobId
                + FlinkRestAPIConstant.VERTICES
                + verticeId
                + FlinkRestAPIConstant.METRICS);
    }

    public JsonNode getJobMetricsData(String jobId, String verticeId, String metrics) {
        return get(FlinkRestAPIConstant.JOBS + jobId + FlinkRestAPIConstant.VERTICES + verticeId
                + FlinkRestAPIConstant.METRICS + "?get=" + URLEncodeUtil.encode(metrics));
    }

    /**
     * GET backpressure
     */
    public String getBackPressure(String jobId, String verticeId) {
        return getResult(FlinkRestAPIConstant.JOBS
                + jobId
                + FlinkRestAPIConstant.VERTICES
                + verticeId
                + FlinkRestAPIConstant.BACKPRESSURE);
    }

    /**
     * GET watermark
     */
    public String getWatermark(String jobId, String verticeId) {
        return getResult(FlinkRestAPIConstant.JOBS
                + jobId
                + FlinkRestAPIConstant.VERTICES
                + verticeId
                + FlinkRestAPIConstant.WATERMARKS);
    }
    /**
     * get vertices
     */
    public List<String> getVertices(String jobId) {
        JsonNode jsonNode = getJobInfo(jobId);
        if (jsonNode == null) {
            return null;
        }
        List<String> arrayList = new ArrayList<>();
        jsonNode.get("vertices").forEach(node -> {
            if (Asserts.isNull(node)) {
                return;
            }
            String id = node.get("id").asText();
            arrayList.add(id);
        });
        return arrayList;
    }
}
