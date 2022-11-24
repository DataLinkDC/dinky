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

package com.dlink.api;

import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkRestAPIConstant;
import com.dlink.constant.NetConstant;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.config.SavePointType;
import com.dlink.gateway.model.JobInfo;
import com.dlink.gateway.result.SavePointResult;

import java.util.ArrayList;
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

    private String address;

    public FlinkAPI(String address) {
        this.address = address;
    }

    public static FlinkAPI build(String address) {
        return new FlinkAPI(address);
    }

    private JsonNode parse(String res) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode result = null;
        try {
            result = mapper.readTree(res);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    private JsonNode get(String route) {
        try {
            String res = HttpUtil.get(NetConstant.HTTP + address + NetConstant.SLASH + route,
                    NetConstant.SERVER_TIME_OUT_ACTIVE);
            return parse(res);
        } catch (Exception e) {
            logger.info("Unable to connect to Flink JobManager: {}", NetConstant.HTTP + address);
        }
        return null;
    }

    /**
     * get请求获取jobManger/TaskManager的日志 (结果为字符串并不是json格式)
     *
     * @param route
     * @return
     */
    private String getResult(String route) {
        String res = HttpUtil.get(NetConstant.HTTP + address + NetConstant.SLASH + route,
                NetConstant.SERVER_TIME_OUT_ACTIVE);
        return res;
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
        JsonNode jobs = result.get("jobs");
        List<JsonNode> joblist = new ArrayList<>();
        if (jobs.isArray()) {
            for (final JsonNode objNode : jobs) {
                joblist.add(objNode);
            }
        }
        return joblist;
    }

    public boolean stop(String jobId) {
        get(FlinkRestAPIConstant.JOBS + jobId + FlinkRestAPIConstant.CANCEL);
        return true;
    }

    public SavePointResult savepoints(String jobId, String savePointType) {
        SavePointType type = SavePointType.get(savePointType);
        String paramType = null;
        SavePointResult result = SavePointResult.build(GatewayType.YARN_PER_JOB);
        JobInfo jobInfo = new JobInfo(jobId);
        Map<String, Object> paramMap = new HashMap<>();
        switch (type) {
            case CANCEL:
                paramMap.put("cancel-job", true);
                paramType = FlinkRestAPIConstant.SAVEPOINTS;
                jobInfo.setStatus(JobInfo.JobStatus.CANCEL);
                break;
            case STOP:
                paramMap.put("drain", false);
                paramType = FlinkRestAPIConstant.STOP;
                jobInfo.setStatus(JobInfo.JobStatus.STOP);
                break;
            case TRIGGER:
                paramMap.put("cancel-job", false);
                // paramMap.put("target-directory","hdfs:///flink13/ss1");
                paramType = FlinkRestAPIConstant.SAVEPOINTS;
                jobInfo.setStatus(JobInfo.JobStatus.RUN);
                break;
            default:
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = null;
        try {
            String s = mapper.writeValueAsString(paramMap);
            json = post(FlinkRestAPIConstant.JOBS + jobId + paramType, s);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String triggerid = json.get("request-id").asText();
        while (triggerid != null) {
            try {
                Thread.sleep(1000);
                JsonNode node = get(FlinkRestAPIConstant.JOBS + jobId + FlinkRestAPIConstant.SAVEPOINTS
                        + NetConstant.SLASH + triggerid);
                String status = node.get("status").get("id").asText();
                if (Asserts.isEquals(status, "IN_PROGRESS")) {
                    continue;
                }
                if (node.get("operation").has("failure-cause")) {
                    String failureCause = node.get("operation").get("failure-cause").toString();
                    if (Asserts.isNotNullString(failureCause)) {
                        result.fail(failureCause);
                        break;
                    }
                }
                if (node.get("operation").has("location")) {
                    String location = node.get("operation").get("location").asText();
                    List<JobInfo> jobInfos = new ArrayList<>();
                    jobInfo.setSavePoint(location);
                    jobInfos.add(jobInfo);
                    result.setJobInfos(jobInfos);
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.fail(e.getMessage());
                break;
            }
        }
        return result;
    }

    public String getVersion() {
        JsonNode result = get(FlinkRestAPIConstant.FLINK_CONFIG);
        return result.get("flink-version").asText();
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
                + buildMetricsParms(FlinkRestAPIConstant.JOB_MANAGER));
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
    public String buildMetricsParms(String type) {
        JsonNode jsonNode = get(type + FlinkRestAPIConstant.METRICS);
        StringBuilder sb = new StringBuilder();
        Iterator<JsonNode> jsonNodeIterator = jsonNode.elements();
        while (jsonNodeIterator.hasNext()) {
            JsonNode node = jsonNodeIterator.next();
            if (Asserts.isNotNull(node) && Asserts.isNotNull(node.get("id"))) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(node.get("id").asText());
            }
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
                + FlinkRestAPIConstant.GET + buildMetricsParms(FlinkRestAPIConstant.JOB_MANAGER));
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
