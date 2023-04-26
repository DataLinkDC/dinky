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

package org.dinky.utils;

import org.dinky.common.result.StepResult;
import org.dinky.model.GitProject;
import org.dinky.process.exception.DinkyException;
import org.dinky.sse.DoneStepSse;
import org.dinky.sse.StepSse;
import org.dinky.sse.git.AnalysisUdfClassStepSse;
import org.dinky.sse.git.GetJarsStepSse;
import org.dinky.sse.git.GitCloneStepSse;
import org.dinky.sse.git.HeadStepSse;
import org.dinky.sse.git.MavenStepSse;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
public final class GitProjectStepSseFactory {

    private static Map<Integer, List<SseEmitter>> sseEmitterMap = new ConcurrentHashMap<>();
    private static final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public static void build(GitProject gitProject, Dict params) {
        int sleep = 500;
        List<SseEmitter> emitterList = new ArrayList<>();

        StepSse headStepSse = getHeadStepPlan(gitProject.getCodeType(), sleep, emitterList, params);
        cachedThreadPool.execute(headStepSse::main);

        sseEmitterMap.put(gitProject.getId(), emitterList);
    }

    private static StepSse getHeadStepPlan(
            Integer codeType, int sleep, List<SseEmitter> emitterList, Dict params) {
        AtomicInteger msgId = new AtomicInteger(1);
        AtomicInteger stepAtomic = new AtomicInteger(1);

        if (codeType.equals(1)) {
            HeadStepSse headStepSse =
                    new HeadStepSse(
                            sleep, emitterList, params, msgId, stepAtomic, cachedThreadPool);
            GitCloneStepSse gitCloneStepSse =
                    new GitCloneStepSse(
                            sleep, emitterList, params, msgId, stepAtomic, cachedThreadPool);
            MavenStepSse mavenStepSse =
                    new MavenStepSse(
                            sleep, emitterList, params, msgId, stepAtomic, cachedThreadPool);
            GetJarsStepSse getJarsStepSse =
                    new GetJarsStepSse(
                            sleep, emitterList, params, msgId, stepAtomic, cachedThreadPool);
            AnalysisUdfClassStepSse analysisUdfClassStepSse =
                    new AnalysisUdfClassStepSse(
                            sleep, emitterList, params, msgId, stepAtomic, cachedThreadPool);
            DoneStepSse doneStepSse =
                    new DoneStepSse(
                            sleep, emitterList, params, msgId, stepAtomic, cachedThreadPool);

            headStepSse.setNexStepSse(gitCloneStepSse);
            gitCloneStepSse.setNexStepSse(mavenStepSse);
            mavenStepSse.setNexStepSse(getJarsStepSse);
            getJarsStepSse.setNexStepSse(analysisUdfClassStepSse);
            analysisUdfClassStepSse.setNexStepSse(doneStepSse);
            return headStepSse;
        } else {
            throw new DinkyException("only support java!");
        }
    }

    public static void observe(SseEmitter emitter, GitProject gitProject, Dict params) {

        List<Dict> dataList = new ArrayList<>();
        Integer state = gitProject.getExecState();
        Integer step = gitProject.getBuildStep();
        getHeadStepPlan(gitProject.getCodeType(), 0, null, params).getStatus(step, state, dataList);
        try {
            emitter.send(SseEmitter.event().data(StepResult.getStepInfo(step, dataList)));

            File logDir = getLogDir(gitProject.getName(), gitProject.getBranch());
            List<String> msgList =
                    FileUtil.readLines(new File(logDir, step + ".log"), StandardCharsets.UTF_8);
            StepResult stepResult =
                    StepResult.builder()
                            .currentStep(step)
                            .status(state)
                            .data(StrUtil.join("\n", msgList))
                            .type(2)
                            .build();
            emitter.send(SseEmitter.event().id("2").data(stepResult));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (state.equals(1)) {
            sseEmitterMap.get(gitProject.getId()).add(emitter);
        } else {
            emitter.complete();
        }
    }

    public static File getLogDir(String projectName, String branch) {
        return FileUtil.file(GitRepository.getProjectDir(projectName), branch + "_log");
    }
}
