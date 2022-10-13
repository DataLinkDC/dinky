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

package com.dlink.service.impl;

import com.dlink.constant.PathConstant;
import com.dlink.gateway.GatewayType;
import com.dlink.job.JobConfig;
import com.dlink.job.JobManager;
import com.dlink.model.Task;
import com.dlink.service.TaskService;
import com.dlink.service.UDFService;
import com.dlink.utils.UDFUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.map.MapUtil;


/**
 * @author ZackYoung
 * @since 0.6.8
 */
@Service
public class UDFServiceImpl implements UDFService {
    /**
     * 网关类型 map
     * 快速获取 session 与 application 等类型，为了减少判断
     */
    private static final Map<String, List<GatewayType>> GATEWAY_TYPE_MAP = MapUtil
            .builder("session", Arrays.asList(GatewayType.YARN_SESSION, GatewayType.KUBERNETES_SESSION, GatewayType.STANDALONE))
            .build();

    @Resource
    TaskService taskService;

    @Override
    public void initUDF(JobConfig config, String statement) {
        Opt<String> udfJarPath = Opt.empty();

        List<String> udfClassNameList = JobManager.getUDFClassName(statement);
        List<String> codeList = CollUtil.map(udfClassNameList, x -> {
            Task task = taskService.getUDFByClassName(x);
            JobManager.initMustSuccessUDF(x, task.getStatement());
            return task.getStatement();
        }, true);
        if (codeList.size() > 0) {
            udfJarPath = Opt.ofBlankAble(UDFUtil.getUdfNameAndBuildJar(codeList));
        }

        udfJarPath.ifPresent(jarPath -> config.setJarFiles(new String[]{PathConstant.UDF_PATH + jarPath}));
    }
}
