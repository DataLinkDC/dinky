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

import org.dinky.data.dto.TaskVersionHistoryDTO;
import org.dinky.data.model.TaskVersion;
import org.dinky.data.result.ProTableResult;
import org.dinky.service.TaskVersionService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 任务版本 Controller
 *
 * @since 2022-06-28
 */
@Slf4j
@RestController
@RequestMapping("/api/task/version")
@RequiredArgsConstructor
public class TaskVersionController {

    private final TaskVersionService versionService;

    /** 动态查询列表 */
    @PostMapping
    public ProTableResult<TaskVersionHistoryDTO> listTasks(@RequestBody JsonNode para) {
        ProTableResult<TaskVersionHistoryDTO> versionHistoryDTOProTableResult =
                new ProTableResult<>();

        ProTableResult<TaskVersion> versionProTableResult = versionService.selectForProTable(para);

        BeanUtil.copyProperties(versionProTableResult, versionHistoryDTOProTableResult);
        List<TaskVersionHistoryDTO> collect =
                versionProTableResult.getData().stream()
                        .map(
                                t -> {
                                    TaskVersionHistoryDTO versionHistoryDTO =
                                            new TaskVersionHistoryDTO();
                                    versionHistoryDTO.setId(t.getId());
                                    versionHistoryDTO.setTaskId(t.getTaskId());
                                    versionHistoryDTO.setName(t.getName());
                                    versionHistoryDTO.setDialect(t.getDialect());
                                    versionHistoryDTO.setType(t.getType());
                                    versionHistoryDTO.setStatement(t.getStatement());
                                    versionHistoryDTO.setVersionId(t.getVersionId());
                                    versionHistoryDTO.setCreateTime(t.getCreateTime());
                                    return versionHistoryDTO;
                                })
                        .collect(Collectors.toList());

        versionHistoryDTOProTableResult.setData(collect);

        return versionHistoryDTOProTableResult;
    }
}
