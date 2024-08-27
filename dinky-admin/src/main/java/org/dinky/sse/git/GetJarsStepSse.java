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

import org.dinky.data.dto.TreeNodeDTO;
import org.dinky.data.model.GitProject;
import org.dinky.service.resource.ResourcesService;
import org.dinky.sse.StepSse;
import org.dinky.utils.MavenUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.extra.spring.SpringUtil;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
public class GetJarsStepSse extends StepSse {

    public GetJarsStepSse(
            int sleep,
            List<SseEmitter> emitterList,
            Dict params,
            AtomicInteger msgId,
            AtomicInteger stepAtomic,
            ExecutorService cachedThreadPool) {
        super("get jars", sleep, emitterList, params, msgId, stepAtomic, cachedThreadPool);
    }

    @Override
    public void exec() {
        List<File> jars = MavenUtil.getJars((File) params.get("pom"));
        List<String> pathList = uploadResources(jars);
        addMsg(String.join("\n", pathList));

        params.put("jarPath", pathList);
    }

    private List<String> uploadResources(List<File> jars) {
        GitProject gitProject = (GitProject) params.get("gitProject");

        ResourcesService resourcesService = SpringUtil.getBean(ResourcesService.class);
        TreeNodeDTO gitFolder = resourcesService.createFolderOrGet(1, "git", "");
        TreeNodeDTO treeNodeDTO =
                resourcesService.createFolderOrGet(Convert.toInt(gitFolder.getId()), gitProject.getName(), "");
        return jars.stream()
                .peek(f -> resourcesService.uploadFile(Convert.toInt(treeNodeDTO.getId()), "", f))
                .map(x -> "rs:/git/" + gitProject.getName() + "/" + x.getName())
                .collect(Collectors.toList());
    }
}
