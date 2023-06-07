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

import org.dinky.context.GitBuildContextHolder;
import org.dinky.data.model.GitProject;
import org.dinky.data.result.StepResult;
import org.dinky.process.exception.DinkyException;
import org.dinky.sse.DoneStepSse;
import org.dinky.sse.StepSse;
import org.dinky.sse.git.AnalysisUdfClassStepSse;
import org.dinky.sse.git.AnalysisUdfPythonStepSse;
import org.dinky.sse.git.GetJarsStepSse;
import org.dinky.sse.git.GitCloneStepSse;
import org.dinky.sse.git.HeadStepSse;
import org.dinky.sse.git.MavenStepSse;
import org.dinky.sse.git.PythonZipStepSse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;

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
        gitProject.setBuildStep(1);
        gitProject.setBuildState(1);
        gitProject.setLastBuild(DateUtil.date());
        gitProject.updateById();

        GitBuildContextHolder.addRun(gitProject.getId());
        sseEmitterMap.put(gitProject.getId(), emitterList);
    }

    private static StepSse getHeadStepPlan(
            Integer codeType, int sleep, List<SseEmitter> emitterList, Dict params) {
        AtomicInteger msgId = new AtomicInteger(1);
        AtomicInteger stepAtomic = new AtomicInteger(1);

        HeadStepSse headStepSse =
                new HeadStepSse(sleep, emitterList, params, msgId, stepAtomic, cachedThreadPool);
        GitCloneStepSse gitCloneStepSse =
                new GitCloneStepSse(
                        sleep, emitterList, params, msgId, stepAtomic, cachedThreadPool);

        DoneStepSse doneStepSse =
                new DoneStepSse(sleep, emitterList, params, msgId, stepAtomic, cachedThreadPool);
        headStepSse.setNexStepSse(gitCloneStepSse);
        switch (codeType) {
            case 1:
                MavenStepSse mavenStepSse =
                        new MavenStepSse(
                                sleep, emitterList, params, msgId, stepAtomic, cachedThreadPool);
                GetJarsStepSse getJarsStepSse =
                        new GetJarsStepSse(
                                sleep, emitterList, params, msgId, stepAtomic, cachedThreadPool);
                AnalysisUdfClassStepSse analysisUdfClassStepSse =
                        new AnalysisUdfClassStepSse(
                                sleep, emitterList, params, msgId, stepAtomic, cachedThreadPool);
                gitCloneStepSse.setNexStepSse(mavenStepSse);
                mavenStepSse.setNexStepSse(getJarsStepSse);
                getJarsStepSse.setNexStepSse(analysisUdfClassStepSse);
                analysisUdfClassStepSse.setNexStepSse(doneStepSse);
                return headStepSse;
            case 2:
                PythonZipStepSse pythonZipStepSse =
                        new PythonZipStepSse(
                                sleep, emitterList, params, msgId, stepAtomic, cachedThreadPool);
                AnalysisUdfPythonStepSse analysisUdfPythonStepSse =
                        new AnalysisUdfPythonStepSse(
                                sleep, emitterList, params, msgId, stepAtomic, cachedThreadPool);

                gitCloneStepSse.setNexStepSse(pythonZipStepSse);
                pythonZipStepSse.setNexStepSse(analysisUdfPythonStepSse);
                analysisUdfPythonStepSse.setNexStepSse(doneStepSse);
                return headStepSse;
            default:
                throw new DinkyException("only support Java & Python!");
        }
    }

    public static void observe(SseEmitter emitter, GitProject gitProject, Dict params) {
        List<Dict> dataList = new ArrayList<>();
        Integer state = gitProject.getExecState();
        Integer step = gitProject.getBuildStep();
        getHeadStepPlan(gitProject.getCodeType(), 0, null, params).getStatus(step, state, dataList);
        try {
            emitter.send(
                    SseEmitter.event()
                            .data(
                                    StepResult.getStepInfo(
                                            step, gitProject.getBuildState(), dataList)));

            if (gitProject.getBuildState() == 3) {
                StepResult data =
                        StepResult.getData(
                                gitProject.getCodeType().equals(1) ? 5 : 4,
                                state,
                                gitProject.getUdfClassMapList());
                emitter.send(SseEmitter.event().data(data));
            }

            File logDir = getLogDir(gitProject.getName(), gitProject.getBranch());
            IntStream.range(1, step + 1)
                    .forEach(
                            s -> {
                                String log = FileUtil.readUtf8String(new File(logDir, s + ".log"));
                                StepResult stepResult =
                                        StepResult.genLog(s, step == s ? state : 2, log, step != s);
                                try {
                                    emitter.send(SseEmitter.event().data(stepResult));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
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
