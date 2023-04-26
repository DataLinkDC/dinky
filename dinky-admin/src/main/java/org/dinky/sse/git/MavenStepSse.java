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

import org.dinky.model.GitProject;
import org.dinky.sse.StepSse;
import org.dinky.utils.GitRepository;
import org.dinky.utils.MavenUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.StrUtil;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
public class MavenStepSse extends StepSse {

    public MavenStepSse(
            int sleep,
            List<SseEmitter> emitterList,
            Dict params,
            AtomicInteger msgId,
            AtomicInteger stepAtomic,
            ExecutorService cachedThreadPool) {
        super("maven build", sleep, emitterList, params, msgId, stepAtomic, cachedThreadPool);
    }

    @Override
    public void exec() {
        GitProject gitProject = (GitProject) params.get("gitProject");
        String pom =
                Opt.ofBlankAble(gitProject.getPom())
                        .orElse(
                                FileUtil.file(
                                                GitRepository.getProjectDir(gitProject.getName()),
                                                gitProject.getBranch())
                                        .getAbsolutePath());
        boolean state =
                MavenUtil.build(
                        "/Volumes/文档及虚拟机/company/数宜信/OpenApi/settings2.xml",
                        pom,
                        null,
                        null,
                        getLogFile().getAbsolutePath(),
                        CollUtil.newArrayList("clean", "package"),
                        StrUtil.split(gitProject.getBuildArgs(), " "),
                        this::addMsg);
        if (!state) {
            setFinish(false);
        }
    }
}
