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

package org.dinky.sse.git;

import org.dinky.data.dto.GitAnalysisJarDTO;
import org.dinky.data.model.GitProject;
import org.dinky.function.util.UDFUtil;
import org.dinky.process.exception.DinkyException;
import org.dinky.sse.StepSse;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
public class AnalysisUdfClassStepSse extends StepSse {

    public AnalysisUdfClassStepSse(
            int sleep,
            List<SseEmitter> emitterList,
            Dict params,
            AtomicInteger msgId,
            AtomicInteger stepAtomic,
            ExecutorService cachedThreadPool) {
        super(
                "analysis udf class",
                sleep,
                emitterList,
                params,
                msgId,
                stepAtomic,
                cachedThreadPool);
    }

    @Override
    public void exec() {
        List<String> pathList = (List<String>) params.get("jarPath");

        List<GitAnalysisJarDTO> dataList = new ArrayList<>();
        Map<String, List<Class<?>>> udfMap = new TreeMap<>();
        try {
            Thread.currentThread()
                    .getContextClassLoader()
                    .loadClass("org.apache.flink.table.api.ValidationException");
        } catch (ClassNotFoundException e) {
            throw new DinkyException("flink dependency not found");
        }
        pathList.parallelStream()
                .forEach(
                        jar -> {
                            List<Class<?>> udfClassByJar = UDFUtil.getUdfClassByJar(new File(jar));
                            udfMap.put(jar, udfClassByJar);
                            sendMsg(Dict.create().set(jar, udfClassByJar));
                        });

        AtomicInteger index = new AtomicInteger(1);
        udfMap.forEach(
                (k, v) -> {
                    GitAnalysisJarDTO gitAnalysisJarDTO = new GitAnalysisJarDTO();
                    gitAnalysisJarDTO.setJarPath(k);
                    gitAnalysisJarDTO.setClassList(
                            v.stream().map(Class::getName).collect(Collectors.toList()));
                    gitAnalysisJarDTO.setOrderLine(index.get());
                    index.getAndIncrement();
                    dataList.add(gitAnalysisJarDTO);
                });

        dataList.sort(Comparator.comparing(GitAnalysisJarDTO::getOrderLine));
        String data = JSONUtil.toJsonStr(dataList);

        sendMsg(getList(null).set("data", data));

        FileUtil.appendString(data, getLogFile(), StandardCharsets.UTF_8);

        // write result
        GitProject gitProject = (GitProject) params.get("gitProject");
        gitProject.setUdfClassMapList(data);
        gitProject.updateById();
    }
}
