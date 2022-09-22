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

package com.dlink.scheduler.client;

import com.dlink.scheduler.constant.Constants;
import com.dlink.scheduler.exception.SchedulerException;
import com.dlink.scheduler.model.TaskDefinition;
import com.dlink.scheduler.model.TaskDefinitionLog;
import com.dlink.scheduler.model.TaskMainInfo;
import com.dlink.scheduler.result.PageInfo;
import com.dlink.scheduler.result.Result;
import com.dlink.scheduler.utils.MyJSONUtil;
import com.dlink.scheduler.utils.ParamUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;

/**
 * 任务定义
 *
 * @author 郑文豪
 */
@Component
public class TaskClient {

    private static final Logger logger = LoggerFactory.getLogger(TaskClient.class);

    @Value("${dinky.dolphinscheduler.url}")
    private String url;
    @Value("${dinky.dolphinscheduler.token}")
    private String tokenKey;

    /**
     * 查询任务定义
     *
     * @param projectCode 项目编号
     * @param processName 工作流定义名称
     * @param taskName    任务定义名称
     * @return {@link TaskMainInfo}
     * @author 郑文豪
     * @date 2022/9/7 17:16
     */
    public TaskMainInfo getTaskMainInfo(Long projectCode, String processName, String taskName) {
        List<TaskMainInfo> lists = getTaskMainInfos(projectCode, processName, taskName);
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
     * @author 郑文豪
     * @date 2022/9/7 17:16
     */
    public List<TaskMainInfo> getTaskMainInfos(Long projectCode, String processName, String taskName) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectCode", projectCode);
        String format = StrUtil.format(url + "/projects/{projectCode}/task-definition", map);

        Map<String, Object> pageParams = ParamUtil.getPageParams();
        pageParams.put("searchTaskName", taskName);
        pageParams.put("searchWorkflowName", processName);
        pageParams.put("taskType", "DINKY");

        String content = HttpRequest.get(format)
            .header(Constants.TOKEN, tokenKey)
            .form(pageParams)
            .timeout(5000)
            .execute().body();

        PageInfo<JSONObject> data = MyJSONUtil.toPageBean(content);
        List<TaskMainInfo> lists = new ArrayList<>();
        if (data == null || data.getTotalList() == null) {
            return lists;
        }

        for (JSONObject jsonObject : data.getTotalList()) {
            if (processName.equalsIgnoreCase(jsonObject.getStr("processDefinitionName"))) {
                lists.add(MyJSONUtil.toBean(jsonObject, TaskMainInfo.class));
            }
        }
        return lists;
    }

    /**
     * 根据编号查询
     *
     * @param projectCode 项目编号
     * @param taskCode    任务编号
     * @return {@link TaskDefinition}
     * @author 郑文豪
     * @date 2022/9/13 10:52
     */
    public TaskDefinition getTaskDefinition(Long projectCode, Long taskCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectCode", projectCode);
        map.put("code", taskCode);
        String format = StrUtil.format(url + "/projects/{projectCode}/task-definition/{code}", map);

        String content = HttpRequest.get(format)
            .header(Constants.TOKEN, tokenKey)
            .timeout(5000)
            .execute().body();

        return MyJSONUtil.verifyResult(MyJSONUtil.toBean(content, new TypeReference<Result<TaskDefinition>>() {
        }));
    }

    /**
     * 创建任务定义
     *
     * @param projectCode 项目编号
     * @param processCode 工作流定义编号
     * @return {@link TaskDefinitionLog}
     * @author 郑文豪
     * @date 2022/9/7 17:05
     */
    public TaskDefinitionLog createTaskDefinition(Long projectCode, Long processCode, String upstreamCodes, String taskDefinitionJsonObj) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectCode", projectCode);
        String format = StrUtil.format(url + "/projects/{projectCode}/task-definition/save-single", map);

        Map<String, Object> pageParams = new HashMap<>();
        pageParams.put("processDefinitionCode", processCode);
        if (StringUtils.isNotBlank(upstreamCodes)) {
            pageParams.put("upstreamCodes", upstreamCodes);
        }

        pageParams.put("taskDefinitionJsonObj", taskDefinitionJsonObj);

        String content = HttpRequest.post(format)
            .header(Constants.TOKEN, tokenKey)
            .form(pageParams)
            .timeout(5000)
            .execute().body();

        return MyJSONUtil.verifyResult(MyJSONUtil.toBean(content, new TypeReference<Result<TaskDefinitionLog>>() {
        }));
    }

    /**
     * 修改任务定义
     *
     * @param projectCode           项目编号
     * @param taskCode              任务定义编号
     * @param taskDefinitionJsonObj 修改参数
     * @return {@link Long}
     * @author 郑文豪
     * @date 2022/9/13 8:59
     */
    public Long updateTaskDefinition(long projectCode, long taskCode, String upstreamCodes, String taskDefinitionJsonObj) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectCode", projectCode);
        map.put("code", taskCode);
        String format = StrUtil.format(url + "/projects/{projectCode}/task-definition/{code}/with-upstream", map);

        Map<String, Object> params = new HashMap<>();
        params.put("upstreamCodes", upstreamCodes);
        params.put("taskDefinitionJsonObj", taskDefinitionJsonObj);

        String content = HttpRequest.put(format)
            .header(Constants.TOKEN, tokenKey)
            .form(params)
            .timeout(5000)
            .execute().body();
        return MyJSONUtil.verifyResult(MyJSONUtil.toBean(content, new TypeReference<Result<Long>>() {
        }));
    }

    /**
     * 生成任务定义编号
     *
     * @param projectCode 项目编号
     * @param genNum      生成个数
     * @return {@link List}
     * @author 郑文豪
     * @date 2022/9/8 18:00
     */
    public List<Long> genTaskCodes(Long projectCode, int genNum) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectCode", projectCode);
        String format = StrUtil.format(url + "/projects/{projectCode}/task-definition/gen-task-codes", map);
        Map<String, Object> params = new HashMap<>();
        params.put("genNum", genNum);
        String content = HttpRequest.get(format)
            .header(Constants.TOKEN, tokenKey)
            .form(params)
            .timeout(5000)
            .execute().body();

        return MyJSONUtil.verifyResult(MyJSONUtil.toBean(content, new TypeReference<Result<List<Long>>>() {
        }));
    }

    /**
     * 生成一个任务定义编号
     *
     * @param projectCode 项目编号
     * @return {@link Long}
     * @author 郑文豪
     * @date 2022/9/8 18:02
     */
    public Long genTaskCode(Long projectCode) {
        List<Long> codes = genTaskCodes(projectCode, 1);
        if (codes == null || codes.isEmpty()) {
            throw new SchedulerException("生成任务定义编号失败");
        }
        return codes.get(0);
    }

}
