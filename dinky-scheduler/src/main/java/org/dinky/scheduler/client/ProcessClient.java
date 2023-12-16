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
import org.dinky.scheduler.model.DagNodeLocation;
import org.dinky.scheduler.model.ProcessDefinition;
import org.dinky.scheduler.result.PageInfo;
import org.dinky.scheduler.result.Result;
import org.dinky.scheduler.utils.MyJSONUtil;
import org.dinky.scheduler.utils.ParamUtil;
import org.dinky.scheduler.utils.ReadFileUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;

/**
 * 工作流定义
 */
@Component
public class ProcessClient {

    private static final Logger logger = LoggerFactory.getLogger(TaskClient.class);

    /**
     * Get a list of process definitions for a specified project code and process name.
     *
     * @param projectCode The ID of the project to get the process definitions for.
     * @param processName The name of the process to get the process definitions for.
     * @return A list of {@link ProcessDefinition} objects representing the process definitions for the specified project code and process name.
     */
    public List<ProcessDefinition> getProcessDefinition(Long projectCode, String processName) {
        String format = StrUtil.format(
                SystemConfiguration.getInstances().getDolphinschedulerUrl().getValue()
                        + "/projects/{projectCode}/process-definition",
                Collections.singletonMap("projectCode", projectCode));

        String content = HttpRequest.get(format)
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
     * Get information about a specified process definition.
     *
     * @param projectCode The ID of the project to get the process definition information for.
     * @param processName The name of the process definition to get information for.
     * @return A {@link ProcessDefinition} object representing the information for the specified process definition.
     */
    public ProcessDefinition getProcessDefinitionInfo(Long projectCode, String processName) {
        List<ProcessDefinition> lists = getProcessDefinition(projectCode, processName);
        Optional<ProcessDefinition> processDefinition = lists.stream()
                .filter(list -> list.getName().equalsIgnoreCase(processName))
                .findFirst();
        return processDefinition.orElse(null);
    }

    /**
     * Get information about a specified process definition.
     *
     * @param projectCode The ID of the project to get the process definition information for.
     * @param processCode The ID of the process definition to get information for.
     * @return A {@link DagData} object representing the information for the specified process definition.
     */
    public DagData getProcessDefinitionInfo(Long projectCode, Long processCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectCode", projectCode);
        map.put("code", processCode);
        String format = StrUtil.format(
                SystemConfiguration.getInstances().getDolphinschedulerUrl().getValue()
                        + "/projects/{projectCode}/process-definition/{code}",
                map);

        String content = HttpRequest.get(format)
                .header(
                        Constants.TOKEN,
                        SystemConfiguration.getInstances()
                                .getDolphinschedulerToken()
                                .getValue())
                .timeout(5000)
                .execute()
                .body();

        return MyJSONUtil.verifyResult(MyJSONUtil.toBean(content, new TypeReference<Result<DagData>>() {}));
    }

    /**
     * Create a new process definition.
     *
     * @param projectCode        The ID of the project to create the process definition for.
     * @param processName        The name of the process definition to create.
     * @param taskCode           The ID of the task to associate with the process definition.
     * @param taskDefinitionJson A JSON string representing the task definition to associate with the process definition.
     * @return A {@link ProcessDefinition} object representing the newly created process definition.
     */
    public ProcessDefinition createOrUpdateProcessDefinition(
            Long projectCode,
            Long processCode,
            String processName,
            Long taskCode,
            String taskDefinitionJson,
            List<DagNodeLocation> locations,
            boolean isModify) {
        String format = StrUtil.format(
                SystemConfiguration.getInstances().getDolphinschedulerUrl().getValue()
                        + "/projects/{projectCode}/process-definition",
                Collections.singletonMap("projectCode", projectCode));

        Map<String, Object> params = new HashMap<>();
        params.put("name", processName);
        params.put("description", "系统添加");
        params.put("tenantCode", "default");
        params.put("locations", locations);
        params.put("taskRelationJson", ReadFileUtil.taskRelation(Collections.singletonMap("code", taskCode)));
        params.put("taskDefinitionJson", taskDefinitionJson);
        params.put("executionType", "PARALLEL");

        HttpRequest httpRequest;
        if (!isModify) {
            httpRequest = HttpRequest.post(format);
        } else {
            httpRequest = HttpRequest.put(format + "/" + processCode);
        }
        HttpResponse httpResponse = httpRequest
                .header(
                        Constants.TOKEN,
                        SystemConfiguration.getInstances()
                                .getDolphinschedulerToken()
                                .getValue())
                .form(params)
                .timeout(5000)
                .execute();
        String content = httpResponse.body();
        return MyJSONUtil.verifyResult(MyJSONUtil.toBean(content, new TypeReference<Result<ProcessDefinition>>() {}));
    }
}
