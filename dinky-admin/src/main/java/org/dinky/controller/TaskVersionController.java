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

import org.dinky.data.annotation.Log;
import org.dinky.data.dto.TaskVersionHistoryDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.model.TaskVersion;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.TaskVersionService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 任务版本 Controller
 *
 * @since 2022-06-28
 */
@Slf4j
@RestController
@Api(tags = "Task Version Controller")
@RequestMapping("/api/task/version")
@RequiredArgsConstructor
public class TaskVersionController {

    private final TaskVersionService versionService;

    /**
     * query task version list
     *
     * @return {@link ProTableResult}<{@link TaskVersionHistoryDTO}>
     */
    @GetMapping
    @ApiOperation("Query Task Version list")
    @ApiImplicitParam(name = "taskId", value = "Task Id", dataType = "int", paramType = "query", required = true)
    public Result<List<TaskVersionHistoryDTO>> listTaskVersions(@RequestParam int taskId) {
        List<TaskVersion> taskVersions = versionService.getTaskVersionByTaskId(taskId);
        List<TaskVersionHistoryDTO> collect = taskVersions.stream()
                .map(t -> BeanUtil.copyProperties(t, TaskVersionHistoryDTO.class))
                .collect(Collectors.toList());
        return Result.succeed(collect);
    }

    @DeleteMapping
    @ApiOperation("Delete Task Version")
    @ApiImplicitParam(name = "id", value = "Task Version Id", dataType = "int", paramType = "query", required = true)
    @Log(title = "Delete Task Version", businessType = BusinessType.DELETE)
    public Result<Boolean> deleteVersion(@RequestParam int id) {
        boolean b = versionService.removeById(id);
        return Result.succeed(b);
    }
}
