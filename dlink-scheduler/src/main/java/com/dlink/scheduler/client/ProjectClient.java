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

import com.dlink.scheduler.config.DolphinSchedulerProperties;
import com.dlink.scheduler.constant.Constants;
import com.dlink.scheduler.model.Project;
import com.dlink.scheduler.result.Result;
import com.dlink.scheduler.utils.MyJSONUtil;
import com.dlink.scheduler.utils.ParamUtil;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.http.HttpRequest;

/**
 * 项目
 *
 * @author 郑文豪
 */
@Component
public class ProjectClient {

    private static final Logger logger = LoggerFactory.getLogger(TaskClient.class);

    @Autowired
    private DolphinSchedulerProperties dolphinSchedulerProperties;

    /**
     * 创建项目
     *
     * @return {@link Project}
     * @author 郑文豪
     * @date 2022/9/7 16:57
     */
    public Project createDinkyProject() {
        Map<String, Object> map = new HashMap<>();
        map.put("projectName", dolphinSchedulerProperties.getProjectName());
        map.put("description", "自动创建");

        String content = HttpRequest.post(dolphinSchedulerProperties.getUrl() + "/projects")
            .header(Constants.TOKEN, dolphinSchedulerProperties.getToken())
            .form(map)
            .timeout(5000)
            .execute().body();
        return MyJSONUtil.verifyResult(MyJSONUtil.toBean(content, new TypeReference<Result<Project>>() {
        }));
    }

    /**
     * 查询项目
     *
     * @return {@link Project}
     * @author 郑文豪
     * @date 2022/9/7 16:57
     */
    public Project getDinkyProject() {

        String content = HttpRequest.get(dolphinSchedulerProperties.getUrl() + "/projects")
            .header(Constants.TOKEN, dolphinSchedulerProperties.getToken())
            .form(ParamUtil.getPageParams(dolphinSchedulerProperties.getProjectName()))
            .timeout(5000)
            .execute().body();

        return MyJSONUtil.toPageBeanAndFindByName(content, dolphinSchedulerProperties.getProjectName(), Project.class);
    }
}
