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

package org.dinky.controller;

import org.dinky.common.result.ProTableResult;
import org.dinky.common.result.Result;
import org.dinky.dto.GitProjectDTO;
import org.dinky.model.GitProject;
import org.dinky.service.GitProjectService;
import org.dinky.utils.GitProjectStepSseFactory;
import org.dinky.utils.GitRepository;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.JsonNode;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import lombok.AllArgsConstructor;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
@RestController
@RequestMapping("/api/git")
@AllArgsConstructor
public class GitController {
    final GitProjectService gitProjectService;

    @PostMapping("/saveOrUpdate")
    public Result<Void> saveOrUpdate(@Validated @RequestBody GitProjectDTO gitProjectDTO) {
        gitProjectService.saveOrUpdate(gitProjectDTO);
        GitRepository gitRepository = new GitRepository(gitProjectDTO);
        gitRepository.cloneAndPull(gitProjectDTO.getName(), gitProjectDTO.getBranches());
        return Result.succeed();
    }

    @GetMapping("/getBranchList")
    public Result<List<String>> getBranchList(GitProjectDTO gitProjectDTO) {
        GitRepository gitRepository = new GitRepository(gitProjectDTO);
        return Result.succeed(gitRepository.getBranchList());
    }

    @DeleteMapping("/deleteProject")
    public Result<Void> deleteProject(Long id) {
        gitProjectService.removeById(id);
        return Result.succeed();
    }

    @PostMapping("/updateEnable")
    public Result<Void> updateEnable(Long id, boolean enable) {
        gitProjectService.updateState(id, enable);
        return Result.succeed();
    }

    @PostMapping("/getProjectList")
    public Result<ProTableResult<GitProject>> getAllProject(@RequestBody JsonNode params) {
        return Result.succeed(gitProjectService.selectForProTable(params));
    }

    @PostMapping("/getOneDetails")
    public Result<GitProject> getOneDetails(Long id) {
        return Result.succeed(gitProjectService.getById(id));
    }

    @GetMapping(path = "/build")
    public Result<Void> build(Long id) {

        GitProject gitProject = gitProjectService.getById(id);
        if (gitProject.getBuildState().equals(1)) {
            return Result.failed("此任务正在构建");
        }

        Dict params = new Dict();
        File logDir =
                FileUtil.file(
                        GitRepository.getProjectDir(gitProject.getName()),
                        gitProject.getBranch() + "_log");
        params.set("gitProject", gitProject).set("logDir", logDir);
        GitProjectStepSseFactory.build(gitProject, params);

        return Result.succeed();
    }

    @GetMapping(path = "/build-step-logs", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter buildStepLogs(Long id) {
        SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(30));
        GitProject gitProject = gitProjectService.getById(id);
        Dict params = new Dict();
        File logDir =
                FileUtil.file(
                        GitRepository.getProjectDir(gitProject.getName()),
                        gitProject.getBranch() + "_log");
        params.set("gitProject", gitProject).set("logDir", logDir);

        GitProjectStepSseFactory.observe(emitter, gitProject, params);

        return emitter;
    }
}
