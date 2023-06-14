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

import org.dinky.data.annotation.PublicInterface;
import org.dinky.data.dto.GitProjectDTO;
import org.dinky.data.dto.TreeNodeDTO;
import org.dinky.data.enums.Status;
import org.dinky.data.model.GitProject;
import org.dinky.data.params.GitProjectSortJarParams;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.GitProjectService;
import org.dinky.sse.SseEmitterUTF8;
import org.dinky.utils.GitProjectStepSseFactory;
import org.dinky.utils.GitRepository;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.JsonNode;

import cn.hutool.core.bean.BeanUtil;
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

    /**
     * save or update git project
     *
     * @param gitProject {@link GitProject}
     * @return {@link Result} of {@link Void}
     */
    @PutMapping("/saveOrUpdate")
    public Result<Void> saveOrUpdate(@Validated @RequestBody GitProjectDTO gitProject) {
        gitProjectService.saveOrUpdate(gitProject);
        GitRepository gitRepository =
                new GitRepository(BeanUtil.copyProperties(gitProject, GitProjectDTO.class));
        gitRepository.cloneAndPull(gitProject.getName(), gitProject.getBranch());
        return Result.succeed();
    }

    /**
     * drag sort project level
     *
     * @param sortList after sorter data
     * @return {@link Result}<{@link Void}>
     */
    @PostMapping("/dragendSortProject")
    public Result<Void> dragendSortProject(@RequestBody Map sortList) {
        if (sortList == null) {
            return Result.failed(Status.GIT_PROJECT_NOT_FOUND);
        }
        gitProjectService.dragendSortProject(sortList);
        return Result.succeed(Status.GIT_SORT_SUCCESS);
    }

    /**
     * drag sort jar level
     *
     * @param gitProjectSortJarParams
     * @return {@link Result}<{@link Void}>
     */
    @PostMapping("/dragendSortJar")
    public Result<Void> dragendSortJar(
            @RequestBody GitProjectSortJarParams gitProjectSortJarParams) {
        GitProject gitProjectServiceById =
                gitProjectService.getById(gitProjectSortJarParams.getProjectId());
        if (gitProjectServiceById == null) {
            return Result.failed(Status.GIT_PROJECT_NOT_FOUND);
        } else {
            if (gitProjectService.dragendSortJar(gitProjectSortJarParams)) {
                return Result.succeed(Status.GIT_SORT_SUCCESS);
            } else {
                return Result.failed(Status.GIT_SORT_FAILED);
            }
        }
    }

    /**
     * get branch list
     *
     * @param gitProjectDTO {@link GitProjectDTO}
     * @return {@link Result} of {@link List}
     */
    @PostMapping("/getBranchList")
    public Result<List<String>> getBranchList(@RequestBody GitProjectDTO gitProjectDTO) {
        GitRepository gitRepository = new GitRepository(gitProjectDTO);
        return Result.succeed(gitRepository.getBranchList());
    }

    /**
     * delete project
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @DeleteMapping("/deleteProject")
    public Result<Void> deleteProject(@RequestParam("id") Integer id) {
        gitProjectService.removeProjectAndCodeCascade(id);
        return Result.succeed();
    }

    /**
     * enable or disable project
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @PutMapping("/updateEnable")
    public Result<Void> updateEnable(@RequestParam("id") Integer id) {
        gitProjectService.updateState(id);
        return Result.succeed();
    }

    /**
     * get project list
     *
     * @param params {@link JsonNode}
     * @return {@link ProTableResult} of {@link GitProject}
     */
    @PostMapping("/getProjectList")
    public ProTableResult<GitProject> getAllProject(@RequestBody JsonNode params) {
        return gitProjectService.selectForProTable(params);
    }

    /**
     * get project info by id
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link GitProject}
     */
    @PostMapping("/getOneDetails")
    public Result<GitProject> getOneDetails(@RequestParam("id") Integer id) {
        return Result.succeed(gitProjectService.getById(id));
    }

    /**
     * build project
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @PutMapping("/build")
    public Result<Void> build(@RequestParam("id") Integer id) {

        GitProject gitProject = gitProjectService.getById(id);
        if (gitProject.getBuildState().equals(1)) {
            return Result.failed(Status.GIT_BUILDING);
        }

        Dict params = new Dict();
        File logDir =
                FileUtil.file(
                        GitRepository.getProjectDir(gitProject.getName()),
                        gitProject.getBranch() + "_log");
        params.set("gitProject", gitProject).set("logDir", logDir);
        GitProjectStepSseFactory.build(gitProject, params);

        return Result.succeed(Status.GIT_BUILD_SUCCESS);
    }

    /**
     * build step
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @GetMapping(path = "/build-step-logs", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin("*")
    @PublicInterface
    public SseEmitter buildStepLogs(@RequestParam("id") Integer id) {
        SseEmitter emitter = new SseEmitterUTF8(TimeUnit.MINUTES.toMillis(30));
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

    /**
     * get project code
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @GetMapping("/getProjectCode")
    public Result<List<TreeNodeDTO>> getProjectCode(@RequestParam("id") Integer id) {

        List<TreeNodeDTO> projectCode = gitProjectService.getProjectCode(id);
        if (projectCode == null) {
            return Result.failed();
        }
        return Result.succeed(projectCode);
    }
}
