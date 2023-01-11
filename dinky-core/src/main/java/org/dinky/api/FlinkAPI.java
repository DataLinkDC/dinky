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

import com.google.common.collect.Lists;
import org.dinky.assertion.Asserts;
import org.dinky.constant.FlinkRestAPIConstant;
import org.dinky.constant.NetConstant;
import org.dinky.gateway.GatewayType;
import org.dinky.gateway.config.SavePointType;
import org.dinky.gateway.model.JobInfo;
import org.dinky.gateway.result.SavePointResult;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;

/**
 * FlinkAPI
 *
 * @author wenmo
 * @since 2021/6/24 13:56
 **/
public class FlinkAPI {

    private static final Logger logger = LoggerFactory.getLogger(FlinkAPI.class);

    public static final String REST_TARGET_DIRECTORY = "rest.target-directory";
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

    public FlinkAPI(String address) {
        this.address = address;
    }

    public static FlinkAPI build(String address) {
        return new FlinkAPI(address);
    }

    private static ObjectMapper mapper = new ObjectMapper();

    private JsonNode parse(String res) {
        try {
            return mapper.readTree(res);
        } catch (JsonProcessingException e) {
            logger.error("json parser error: ", e);
            return null;
        }
    }

    private JsonNode get(String route) {
        try {
            String res = getResult(route);
            return parse(res);
        } catch (Exception e) {
            logger.error("Unable to connect to Flink JobManager: {}", NetConstant.HTTP + address);
            return null;
        }
    }

    /**
     * get请求获取jobManger/TaskManager的日志 (结果为字符串并不是json格式)
     *
     * @param route
     * @return
     */
    private String getResult(String route) {
        return HttpUtil.get(NetConstant.HTTP + address + NetConstant.SLASH + route,
                NetConstant.SERVER_TIME_OUT_ACTIVE);
    }

    private JsonNode post(String route, String body) {
        String res = HttpUtil.post(NetConstant.HTTP + address + NetConstant.SLASH + route, body,
                NetConstant.SERVER_TIME_OUT_ACTIVE);
        return parse(res);
    }

    private JsonNode patch(String route, String body) {
        String res = HttpUtil.createRequest(Method.PATCH, NetConstant.HTTP + address + NetConstant.SLASH + route)
                .timeout(NetConstant.SERVER_TIME_OUT_ACTIVE).body(body).execute().body();
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

    public SavePointResult savepoints(String jobId, String savePointType, Map<String, String> taskConfig) {
        SavePointType type = SavePointType.get(savePointType);
        JobInfo jobInfo = new JobInfo(jobId);
        Map<String, Object> paramMap = new HashMap<>();
        String paramType = null;
        switch (type) {
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
                // paramMap.put("target-directory","hdfs:///flink13/ss1");
                paramType = FlinkRestAPIConstant.SAVEPOINTS;
                jobInfo.setStatus(JobInfo.JobStatus.RUN);
                break;
            default:
        }

        if (Asserts.isNotNull(taskConfig) && taskConfig.containsKey(REST_TARGET_DIRECTORY)) {
            paramMap.put(REST_TARGET_DIRECTORY.substring(REST_TARGET_DIRECTORY.indexOf(".") + 1),
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
            logger.error("savePoints error: ",e);
            return null;
        }

        if (json == null) {
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
                node = get(FlinkRestAPIConstant.JOBS + jobId + FlinkRestAPIConstant.SAVEPOINTS
                        + NetConstant.SLASH + json.get(REQUEST_ID).asText());
                String status = node.get(STATUS).get(ID).asText();
                if (!Asserts.isEquals(status, "IN_PROGRESS")) {
                    break;
                }
            } catch (Exception e) {
                logger.error("", e);
                result.fail(e.getMessage());
                return result;
            }
        }

        if (node == null) {
            return null;
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

    public JsonNode getException(String jobId) {
        return get(FlinkRestAPIConstant.JOBS + jobId + FlinkRestAPIConstant.EXCEPTIONS);
    }

    public JsonNode getCheckPoints(String jobId) {
        return get(FlinkRestAPIConstant.JOBS + jobId + FlinkRestAPIConstant.CHECKPOINTS);
    }

    public JsonNode getCheckPointsConfig(String jobId) {
        return get(FlinkRestAPIConstant.JOBS + jobId + FlinkRestAPIConstant.CHECKPOINTS_CONFIG);
    }

    public JsonNode getJobsConfig(String jobId) {
        return get(FlinkRestAPIConstant.JOBS + jobId + FlinkRestAPIConstant.CONFIG);
    }

    /**
     * @return JsonNode
     * @Author: zhumingye
     * @date: 2022/6/24
     * @Description: getJobManagerMetrics 获取jobManager的监控信息
     */
    public JsonNode getJobManagerMetrics() {
        return get(FlinkRestAPIConstant.JOB_MANAGER + FlinkRestAPIConstant.METRICS + FlinkRestAPIConstant.GET
                + buildMetricsParams(FlinkRestAPIConstant.JOB_MANAGER));
    }

    /**
     * @return JsonNode
     * @Author: zhumingye
     * @date: 2022/6/24
     * @Description: getJobManagerConfig 获取jobManager的配置信息
     */
    public JsonNode getJobManagerConfig() {
        return get(FlinkRestAPIConstant.JOB_MANAGER + FlinkRestAPIConstant.CONFIG);
    }

    /**
     * @return JsonNode
     * @Author: zhumingye
     * @date: 2022/6/24
     * @Description: getJobManagerLog 获取jobManager的日志信息
     */
    public String getJobManagerLog() {
        return getResult(FlinkRestAPIConstant.JOB_MANAGER + FlinkRestAPIConstant.LOG);
    }

    /**
     * @return String
     * @Author: zhumingye
     * @date: 2022/6/24
     * @Description: getJobManagerStdOut 获取jobManager的控制台输出日志
     */
    public String getJobManagerStdOut() {
        return getResult(FlinkRestAPIConstant.JOB_MANAGER + FlinkRestAPIConstant.STDOUT);
    }

    /**
     * @return JsonNode
     * @Author: zhumingye
     * @date: 2022/6/24
     * @Description: getJobManagerLogList 获取jobManager的日志列表
     */
    public JsonNode getJobManagerLogList() {
        return get(FlinkRestAPIConstant.JOB_MANAGER + FlinkRestAPIConstant.LOGS);
    }

    /**
     * @param logName 日志文件名
     * @return String
     * @Author: zhumingye
     * @date: 2022/6/24
     * @Description: getJobManagerLogFileDetail 获取jobManager的日志文件的具体信息
     */
    public String getJobManagerLogFileDetail(String logName) {
        return getResult(FlinkRestAPIConstant.JOB_MANAGER + FlinkRestAPIConstant.LOGS + logName);
    }

    /**
     * @return JsonNode
     * @Author: zhumingye
     * @date: 2022/6/24
     * @Description: getTaskManagers 获取taskManager的列表
     */
    public JsonNode getTaskManagers() {
        return get(FlinkRestAPIConstant.TASK_MANAGER);
    }

    /**
     * @return String
     * @Author: zhumingye
     * @date: 2022/6/24
     * @Description: buildMetricsParms 构建metrics参数
     * @Params: type:  入参类型 可选值：task-manager, job-manager
     */
    public String buildMetricsParams(String type) {
        JsonNode jsonNode = get(type + FlinkRestAPIConstant.METRICS);
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

    /**
     * @return JsonNode
     * @Author: zhumingye
     * @date: 2022/6/24
     * @Description: getJobManagerLog 获取jobManager的日志信息
     */
    public JsonNode getTaskManagerMetrics(String containerId) {
        return get(FlinkRestAPIConstant.TASK_MANAGER + containerId + FlinkRestAPIConstant.METRICS
                + FlinkRestAPIConstant.GET + buildMetricsParams(FlinkRestAPIConstant.JOB_MANAGER));
    }

    /**
     * @param containerId 容器id
     * @return String
     * @Author: zhumingye
     * @date: 2022/6/24
     * @Description: getTaskManagerLog 获取taskManager的日志信息
     */
    public String getTaskManagerLog(String containerId) {
        return getResult(FlinkRestAPIConstant.TASK_MANAGER + containerId + FlinkRestAPIConstant.LOG);
    }

    /**
     * @param containerId 容器id
     * @return JsonNode
     * @Author: zhumingye
     * @date: 2022/6/24
     * @Description: getTaskManagerStdOut 获取taskManager的StdOut日志信息
     */
    public String getTaskManagerStdOut(String containerId) {
        return getResult(FlinkRestAPIConstant.TASK_MANAGER + containerId + FlinkRestAPIConstant.STDOUT);
    }

    /**
     * @param containerId 容器id
     * @return JsonNode
     * @Author: zhumingye
     * @date: 2022/6/24
     * @Description: getTaskManagerLogList 获取taskManager的日志列表
     */
    public JsonNode getTaskManagerLogList(String containerId) {
        return get(FlinkRestAPIConstant.TASK_MANAGER + containerId + FlinkRestAPIConstant.LOGS);
    }

    /**
     * @param logName 日志名称
     * @return String
     * @Author: zhumingye
     * @date: 2022/6/24
     * @Description: getTaskManagerLogFileDeatil 获取具体日志的详细信息
     */
    public String getTaskManagerLogFileDeatil(String containerId, String logName) {
        return getResult(FlinkRestAPIConstant.TASK_MANAGER + containerId + FlinkRestAPIConstant.LOGS + logName);
    }

    /**
     * @return JsonNode
     * @Author: zhumingye
     * @date: 2022/6/24
     * @Description: getTaskManagerThreadDump 获取taskManager的线程信息
     */
    public JsonNode getTaskManagerThreadDump(String containerId) {
        return get(FlinkRestAPIConstant.TASK_MANAGER + containerId + FlinkRestAPIConstant.THREAD_DUMP);
    }
}
