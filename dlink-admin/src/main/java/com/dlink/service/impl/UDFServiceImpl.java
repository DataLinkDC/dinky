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

import com.dlink.exception.BusException;
import com.dlink.executor.Executor;
import com.dlink.gateway.GatewayType;
import com.dlink.job.JobConfig;
import com.dlink.process.context.ProcessContextHolder;
import com.dlink.process.model.ProcessEntity;
import com.dlink.service.TaskService;
import com.dlink.service.UDFService;
import com.dlink.ud.FunctionFactory;
import com.dlink.ud.data.model.Env;
import com.dlink.ud.data.model.UDF;
import com.dlink.ud.data.model.UDFPath;
import com.dlink.utils.UDFUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;

/**
 * @author ZackYoung
 * @since 0.6.8
 */
@Service
public class UDFServiceImpl implements UDFService {
    private static final String YARN = "YARN";
    private static final String APPLICATION = "APPLICATION";

    /**
     * 网关类型 map
     * 快速获取 session 与 application 等类型，为了减少判断
     */
    private static final Map<String, List<GatewayType>> GATEWAY_TYPE_MAP = MapUtil
        .builder("session",
            Arrays.asList(GatewayType.YARN_SESSION, GatewayType.KUBERNETES_SESSION, GatewayType.STANDALONE))
        .put(YARN,
            Arrays.asList(GatewayType.YARN_APPLICATION, GatewayType.YARN_PER_JOB))
        .put(APPLICATION,
            Arrays.asList(GatewayType.YARN_APPLICATION, GatewayType.KUBERNETES_APPLICATION))
        .build();

    @Resource
    TaskService taskService;

    /**
     * @param statement   sql语句
     * @param gatewayType flink 网关提交类型
     * @param missionId   任务id
     * @param executor    flink执行器
     * @param config      job配置
     */
    @Override
    public void initUDF(String statement, GatewayType gatewayType, Integer missionId, Executor executor, JobConfig config) {
        if (gatewayType == GatewayType.KUBERNETES_APPLICATION) {
            throw new BusException("udf 暂不支持k8s application");
        }

        ProcessEntity process = ProcessContextHolder.getProcess();
        process.info("Initializing Flink UDF...Start");

        List<UDF> udf = UDFUtils.getUDF(statement);
        UDFPath udfPath = FunctionFactory.initUDF(udf, missionId, executor.getTableConfig().getConfiguration());

        executor.initUDF(udfPath.getJarPaths());
        executor.initPyUDF(Env.getPath(),udfPath.getPyPaths());

        //
        if (GATEWAY_TYPE_MAP.get(YARN).contains(gatewayType)) {
            config.getGatewayConfig().setJarPaths(ArrayUtil.append(udfPath.getJarPaths(), udfPath.getPyPaths()));
        }

        if (GATEWAY_TYPE_MAP.get(APPLICATION).contains(gatewayType)) {
            config.getGatewayConfig().setJarPaths(ArrayUtil.append(udfPath.getJarPaths(), udfPath.getPyPaths()));
        }

        process.info("Initializing Flink UDF...Finish");
    }
}
