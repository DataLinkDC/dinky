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

package org.dinky.scheduler.client;

import org.dinky.data.model.SystemConfiguration;
import org.dinky.scheduler.constant.Constants;
import org.dinky.scheduler.exception.SchedulerException;
import org.dinky.scheduler.model.TaskDefinition;
import org.dinky.scheduler.model.TaskDefinitionLog;
import org.dinky.scheduler.model.TaskGroup;
import org.dinky.scheduler.model.TaskMainInfo;
import org.dinky.scheduler.result.PageInfo;
import org.dinky.scheduler.result.Result;
import org.dinky.scheduler.utils.MyJSONUtil;
import org.dinky.scheduler.utils.ParamUtil;
import org.dinky.utils.JsonUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;

/**
 * 任务定义
 */
@Component
public class TaskClient {

    private static final Logger logger = LoggerFactory.getLogger(TaskClient.class);

    /**
     * 查询任务定义
     *
     * @param projectCode 项目编号
     * @param processName 工作流定义名称
     * @param taskName    任务定义名称
     * @return {@link TaskMainInfo}
     */
    public TaskMainInfo getTaskMainInfo(Long projectCode, String processName, String taskName, String taskType) {
        List<TaskMainInfo> lists = getTaskMainInfos(projectCode, processName, taskName, taskType);
        for (TaskMainInfo list : lists) {
            if (list.getTaskName().equalsIgnoreCase(taskName)) {
                return list;
            }
        }
        return null;
    }

    /**
     * 查询任务定义集合
     *
     * @param projectCode 项目编号
     * @param processName 工作流定义名称
     * @param taskName    任务定义名称
     * @return {@link List<TaskMainInfo>}
     */
    public List<TaskMainInfo> getTaskMainInfos(Long projectCode, String processName, String taskName, String taskType) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectCode", projectCode);
        String format = StrUtil.format(
                SystemConfiguration.getInstances().getDolphinschedulerUrl().getValue()
                        + "/projects/{projectCode}/task-definition",
                map);

        Map<String, Object> pageParams = ParamUtil.getPageParams();
        pageParams.put("searchTaskName", taskName);
        pageParams.put("searchWorkflowName", processName);
        pageParams.put("taskType", taskType);

        try (HttpResponse httpResponse = HttpRequest.get(format)
                .header(
                        Constants.TOKEN,
                        SystemConfiguration.getInstances()
                                .getDolphinschedulerToken()
                                .getValue())
                .form(pageParams)
                .timeout(5000)
                .execute()) {
            PageInfo<JSONObject> data = MyJSONUtil.toPageBean(httpResponse.body());
            List<TaskMainInfo> lists = new ArrayList<>();
            if (data == null || data.getTotalList() == null) {
                return lists;
            }

            for (JSONObject jsonObject : data.getTotalList()) {
                lists.add(JsonUtils.toBean(jsonObject, TaskMainInfo.class));
            }
            return lists;
        }
    }

    /**
     * 根据编号查询
     *
     * @param projectCode 项目编号
     * @param taskCode    任务编号
     * @return {@link TaskDefinition}
     */
    public TaskDefinition getTaskDefinition(Long projectCode, Long taskCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectCode", projectCode);
        map.put("code", taskCode);
        String format = StrUtil.format(
                SystemConfiguration.getInstances().getDolphinschedulerUrl().getValue()
                        + "/projects/{projectCode}/task-definition/{code}",
                map);

        try (HttpResponse httpResponse = HttpRequest.get(format)
                .header(
                        Constants.TOKEN,
                        SystemConfiguration.getInstances()
                                .getDolphinschedulerToken()
                                .getValue())
                .timeout(20000)
                .execute()) {
            return MyJSONUtil.verifyResult(
                    MyJSONUtil.toBean(httpResponse.body(), new TypeReference<Result<TaskDefinition>>() {}));
        }
    }

    /**
     * 创建任务定义
     *
     * @param projectCode 项目编号
     * @param processCode 工作流定义编号
     * @return {@link TaskDefinitionLog}
     */
    public TaskDefinitionLog createTaskDefinition(
            Long projectCode, Long processCode, List<String> upstreamCodes, String taskDefinitionJsonObj) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectCode", projectCode);
        String format = StrUtil.format(
                SystemConfiguration.getInstances().getDolphinschedulerUrl().getValue()
                        + "/projects/{projectCode}/task-definition/save-single",
                map);

        Map<String, Object> pageParams = new HashMap<>();
        pageParams.put("processDefinitionCode", processCode);
        if (CollUtil.isNotEmpty(upstreamCodes)) {
            pageParams.put("upstreamCodes", StringUtils.join(upstreamCodes, ","));
        }

        pageParams.put("taskDefinitionJsonObj", taskDefinitionJsonObj);

        try (HttpResponse httpResponse = HttpRequest.post(format)
                .header(
                        Constants.TOKEN,
                        SystemConfiguration.getInstances()
                                .getDolphinschedulerToken()
                                .getValue())
                .form(pageParams)
                .timeout(5000)
                .execute()) {
            return MyJSONUtil.verifyResult(
                    MyJSONUtil.toBean(httpResponse.body(), new TypeReference<Result<TaskDefinitionLog>>() {}));
        }
    }

    /**
     * 修改任务定义
     *
     * @param projectCode           项目编号
     * @param taskCode              任务定义编号
     * @param taskDefinitionJsonObj 修改参数
     * @return {@link Long}
     */
    public Long updateTaskDefinition(
            long projectCode, long taskCode, List<String> upstreamCodes, String taskDefinitionJsonObj) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectCode", projectCode);
        map.put("code", taskCode);
        String format = StrUtil.format(
                SystemConfiguration.getInstances().getDolphinschedulerUrl().getValue()
                        + "/projects/{projectCode}/task-definition/{code}/with-upstream",
                map);

        Map<String, Object> params = new HashMap<>();
        if (CollUtil.isNotEmpty(upstreamCodes)) {
            params.put("upstreamCodes", StringUtils.join(upstreamCodes, ","));
        }
        params.put("taskDefinitionJsonObj", taskDefinitionJsonObj);

        try (HttpResponse httpResponse = HttpRequest.put(format)
                .header(
                        Constants.TOKEN,
                        SystemConfiguration.getInstances()
                                .getDolphinschedulerToken()
                                .getValue())
                .form(params)
                .timeout(5000)
                .execute()) {
            return MyJSONUtil.verifyResult(
                    MyJSONUtil.toBean(httpResponse.body(), new TypeReference<Result<Long>>() {}));
        }
    }

    /**
     * 生成任务定义编号
     *
     * @param projectCode 项目编号
     * @param genNum      生成个数
     * @return {@link List}
     */
    public List<Long> genTaskCodes(Long projectCode, int genNum) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectCode", projectCode);
        String format = StrUtil.format(
                SystemConfiguration.getInstances().getDolphinschedulerUrl().getValue()
                        + "/projects/{projectCode}/task-definition/gen-task-codes",
                map);
        Map<String, Object> params = new HashMap<>();
        params.put("genNum", genNum);

        try (HttpResponse httpResponse = HttpRequest.get(format)
                .header(
                        Constants.TOKEN,
                        SystemConfiguration.getInstances()
                                .getDolphinschedulerToken()
                                .getValue())
                .form(params)
                .timeout(5000)
                .execute()) {
            return MyJSONUtil.verifyResult(
                    MyJSONUtil.toBean(httpResponse.body(), new TypeReference<Result<List<Long>>>() {}));
        }
    }

    /**
     * 生成一个任务定义编号
     *
     * @param projectCode 项目编号
     * @return {@link Long}
     */
    public Long genTaskCode(Long projectCode) {
        List<Long> codes = genTaskCodes(projectCode, 1);
        if (codes == null || codes.isEmpty()) {
            throw new SchedulerException("Failed to generate task definition number");
        }
        return codes.get(0);
    }

    /**
     * 通过 projectCode 获得 DolphinScheduler 任务组列表
     *
     * @param projectCode projectCode
     * @return  List<TaskGroup>
     */
    public List<TaskGroup> getTaskGroupList(Long projectCode) {
        String url = SystemConfiguration.getInstances().getDolphinschedulerUrl().getValue()
                + "/task-group/query-list-by-projectCode";
        Map<String, Object> params = new HashMap<>();
        params.put("projectCode", projectCode);
        params.put("pageNo", 1);
        params.put("pageSize", 100);

        try (HttpResponse httpResponse = HttpRequest.get(url)
                .header(
                        Constants.TOKEN,
                        SystemConfiguration.getInstances()
                                .getDolphinschedulerToken()
                                .getValue())
                .form(params)
                .timeout(5000)
                .execute()) {
            PageInfo<JSONObject> pageInfo = MyJSONUtil.toPageBean(httpResponse.body());
            return MyJSONUtil.toBean(pageInfo.getTotalList().toString(), new TypeReference<List<TaskGroup>>() {});
        }
    }
}
