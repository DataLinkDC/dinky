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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.flink.table.catalog.FunctionLanguage;
import org.springframework.stereotype.Service;

import com.dlink.constant.PathConstant;
import com.dlink.exception.BusException;
import com.dlink.gateway.GatewayType;
import com.dlink.model.Task;
import com.dlink.model.UDFPath;
import com.dlink.process.context.ProcessContextHolder;
import com.dlink.process.model.ProcessEntity;
import com.dlink.service.TaskService;
import com.dlink.service.UDFService;
import com.dlink.udf.UDF;
import com.dlink.utils.UDFUtil;

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
        .builder("session",
            Arrays.asList(GatewayType.YARN_SESSION, GatewayType.KUBERNETES_SESSION, GatewayType.STANDALONE))
        .build();

    @Resource
    TaskService taskService;

    @Override
    public UDFPath initUDF(String statement, GatewayType gatewayType) {
        if (gatewayType == GatewayType.KUBERNETES_APPLICATION) {
            throw new BusException("udf 暂不支持k8s application");
        }

        ProcessEntity process = ProcessContextHolder.getProcess();
        process.info("Initializing Flink UDF...Start");

        List<UDF> udfList = UDFUtil.getUDF(statement);
        List<UDF> javaUdf = new ArrayList<>();
        List<UDF> pythonUdf = new ArrayList<>();
        udfList.forEach(udf -> {
            Task task = taskService.getUDFByClassName(udf.getClassName());
            udf.setCode(task.getStatement());
            if (udf.getFunctionLanguage() == FunctionLanguage.PYTHON) {
                pythonUdf.add(udf);
            } else {
                javaUdf.add(udf);
            }
        });
        String[] javaUDFPath = initJavaUDF(javaUdf);
        String[] pythonUDFPath = initPythonUDF(pythonUdf);

        process.info("Initializing Flink UDF...Finish");
        return UDFPath.builder().jarPaths(javaUDFPath).pyPaths(pythonUDFPath).build();
    }

    private static String[] initPythonUDF(List<UDF> udfList) {
        if (udfList.isEmpty()) {
            return new String[0];
        }

        return new String[] {UDFUtil.buildPy(udfList)};
    }

    private static String[] initJavaUDF(List<UDF> udfList) {
        Opt<String> udfJarPath = Opt.empty();
        if (!udfList.isEmpty()) {
            List<String> codeList = udfList.stream().map(UDF::getCode).collect(Collectors.toList());
            udfJarPath = Opt.ofBlankAble(UDFUtil.getUdfFileAndBuildJar(codeList));
        }

        if (udfJarPath.isPresent()) {
            return new String[] {PathConstant.UDF_PATH + udfJarPath.get()};
        } else {
            return new String[0];
        }
    }
}
