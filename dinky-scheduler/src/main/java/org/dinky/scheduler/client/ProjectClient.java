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
import org.dinky.scheduler.model.Project;
import org.dinky.scheduler.result.Result;
import org.dinky.scheduler.utils.MyJSONUtil;
import org.dinky.scheduler.utils.ParamUtil;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

/**
 * 项目
 */
@Component
public class ProjectClient {

    private static final Logger logger = LoggerFactory.getLogger(TaskClient.class);

    /**
     * 创建项目
     *
     * @return {@link Project}
     */
    public Project createDinkyProject() {
        Map<String, Object> map = new HashMap<>();
        map.put(
                "projectName",
                SystemConfiguration.getInstances()
                        .getDolphinschedulerProjectName()
                        .getValue());
        map.put("description", "自动创建");
        HttpResponse httpResponse = HttpRequest.post(SystemConfiguration.getInstances()
                                .getDolphinschedulerUrl()
                                .getValue()
                        + "/projects")
                .header(
                        Constants.TOKEN,
                        SystemConfiguration.getInstances()
                                .getDolphinschedulerToken()
                                .getValue())
                .form(map)
                .timeout(5000)
                .execute();
        if (httpResponse.getStatus() != 200) {
            SystemConfiguration.getInstances().getDolphinschedulerEnable().setValue(false);
            logger.error("DolphInScheduler connection failed, Reason: {}", httpResponse.getStatus());
            return null;
        }
        String content = httpResponse.body();
        return MyJSONUtil.verifyResult(MyJSONUtil.toBean(content, new TypeReference<Result<Project>>() {}));
    }

    /**
     * 查询项目
     *
     * @return {@link Project}
     */
    public Project getDinkyProject() {

        HttpResponse httpResponse = HttpRequest.get(SystemConfiguration.getInstances()
                                .getDolphinschedulerUrl()
                                .getValue()
                        + "/projects")
                .header(
                        Constants.TOKEN,
                        SystemConfiguration.getInstances()
                                .getDolphinschedulerToken()
                                .getValue())
                .form(ParamUtil.getPageParams(SystemConfiguration.getInstances()
                        .getDolphinschedulerProjectName()
                        .getValue()))
                .timeout(5000)
                .execute();

        if (httpResponse.getStatus() != 200) {
            SystemConfiguration.getInstances().getDolphinschedulerEnable().setValue(false);
            logger.error("DolphInScheduler connection failed, Reason: {}", httpResponse.getStatus());
            return null;
        }
        String content = httpResponse.body();
        try {
            return MyJSONUtil.toPageBeanAndFindByName(
                    content,
                    SystemConfiguration.getInstances()
                            .getDolphinschedulerProjectName()
                            .getValue(),
                    Project.class);
        } catch (Exception e) {
            SystemConfiguration.getInstances().getDolphinschedulerEnable().setValue(false);
            throw new RuntimeException(content);
        }
    }
}
