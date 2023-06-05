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
import org.dinky.scheduler.model.DagData;
import org.dinky.scheduler.model.ProcessDefinition;
import org.dinky.scheduler.result.PageInfo;
import org.dinky.scheduler.result.Result;
import org.dinky.scheduler.utils.MyJSONUtil;
import org.dinky.scheduler.utils.ParamUtil;
import org.dinky.scheduler.utils.ReadFileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;

/** 工作流定义 */
@Component
public class ProcessClient {

    private static final Logger logger = LoggerFactory.getLogger(TaskClient.class);

    /**
     * 查询工作流定义
     *
     * @param projectCode 项目编号
     * @param processName 工作流定义名
     * @return {@link List<ProcessDefinition>}
     */
    public List<ProcessDefinition> getProcessDefinition(Long projectCode, String processName) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectCode", projectCode);

        String format =
                StrUtil.format(
                        SystemConfiguration.getInstances().getDolphinschedulerUrl().getValue()
                                + "/projects/{projectCode}/process-definition",
                        map);

        String content =
                HttpRequest.get(format)
                        .header(
                                Constants.TOKEN,
                                SystemConfiguration.getInstances()
                                        .getDolphinschedulerToken()
                                        .getValue())
                        .form(ParamUtil.getPageParams(processName))
                        .timeout(5000)
                        .execute()
                        .body();
        PageInfo<JSONObject> data = MyJSONUtil.toPageBean(content);
        List<ProcessDefinition> lists = new ArrayList<>();
        if (data == null || data.getTotalList() == null) {
            return lists;
        }

        for (JSONObject jsonObject : data.getTotalList()) {
            lists.add(MyJSONUtil.toBean(jsonObject, ProcessDefinition.class));
        }
        return lists;
    }

    /**
     * 查询工作流定义
     *
     * @param projectCode 项目编号
     * @param processName 工作流定义名
     * @return {@link ProcessDefinition}
     */
    public ProcessDefinition getProcessDefinitionInfo(Long projectCode, String processName) {

        List<ProcessDefinition> lists = getProcessDefinition(projectCode, processName);
        for (ProcessDefinition list : lists) {
            if (list.getName().equalsIgnoreCase(processName)) {
                return list;
            }
        }
        return null;
    }

    /**
     * 根据编号获取
     *
     * @param projectCode 项目编号
     * @param processCode 任务编号
     * @return {@link DagData}
     */
    public DagData getProcessDefinitionInfo(Long projectCode, Long processCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectCode", projectCode);
        map.put("code", processCode);
        String format =
                StrUtil.format(
                        SystemConfiguration.getInstances().getDolphinschedulerUrl().getValue()
                                + "/projects/{projectCode}/process-definition/{code}",
                        map);

        String content =
                HttpRequest.get(format)
                        .header(
                                Constants.TOKEN,
                                SystemConfiguration.getInstances()
                                        .getDolphinschedulerToken()
                                        .getValue())
                        .timeout(5000)
                        .execute()
                        .body();

        return MyJSONUtil.verifyResult(
                MyJSONUtil.toBean(content, new TypeReference<Result<DagData>>() {}));
    }

    /**
     * 创建工作流定义
     *
     * @param projectCode 项目编号
     * @param processName 工作流定义名称
     * @return {@link ProcessDefinition}
     */
    public ProcessDefinition createProcessDefinition(
            Long projectCode, String processName, Long taskCode, String taskDefinitionJson) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectCode", projectCode);
        String format =
                StrUtil.format(
                        SystemConfiguration.getInstances().getDolphinschedulerUrl().getValue()
                                + "/projects/{projectCode}/process-definition",
                        map);

        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("code", taskCode);

        String taskRelationJson = ReadFileUtil.taskRelation(taskMap);

        Map<String, Object> params = new HashMap<>();
        params.put("name", processName);
        params.put("description", "系统添加");
        params.put("tenantCode", "default");
        params.put("taskRelationJson", taskRelationJson);
        params.put("taskDefinitionJson", taskDefinitionJson);
        params.put("executionType", "PARALLEL");

        String content =
                HttpRequest.post(format)
                        .header(
                                Constants.TOKEN,
                                SystemConfiguration.getInstances()
                                        .getDolphinschedulerToken()
                                        .getValue())
                        .form(params)
                        .timeout(5000)
                        .execute()
                        .body();

        return MyJSONUtil.verifyResult(
                MyJSONUtil.toBean(content, new TypeReference<Result<ProcessDefinition>>() {}));
    }
}
