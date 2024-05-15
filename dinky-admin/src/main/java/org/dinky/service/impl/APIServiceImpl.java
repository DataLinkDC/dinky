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

package org.dinky.service.impl;

import cn.hutool.json.JSONUtil;
import org.dinky.data.dto.CatalogueTaskDTO;
import org.dinky.data.dto.CreatingCatalogueTaskDTO;
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.enums.JobLifeCycle;
import org.dinky.data.enums.Status;
import org.dinky.data.exception.SqlExplainExcepition;
import org.dinky.data.model.Catalogue;
import org.dinky.scheduler.model.DinkyTaskRequest;
import org.dinky.service.APIService;

import org.dinky.service.CatalogueService;
import org.dinky.service.SchedulerService;
import org.dinky.service.TaskService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * APIServiceImpl
 *
 * @since 2021/12/11 21:46
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class APIServiceImpl implements APIService {
    @Autowired
    private TaskService taskService;
    @Autowired
    private SchedulerService schedulerService;
    @Autowired
    private CatalogueService catalogueService;

    /**
     * 创建目录、任务并推送到 DolphinScheduler
     * @param dto CreateCatalogueTaskDTO
     * @throws SqlExplainExcepition
     */
    @Override
    public void createTaskAndSend2Ds(CreatingCatalogueTaskDTO dto) throws SqlExplainExcepition {
        int parentId = 0;
        for (String catalogueName : dto.getCatalogueNames()) {
            Catalogue catalogue = catalogueService.findByParentIdAndName(parentId, catalogueName);
            // 目录不存在则创建
            if (catalogue == null) {
                catalogue = new Catalogue();
                catalogue.setName(catalogueName);
                catalogue.setIsLeaf(false);
                catalogue.setParentId(parentId);
                catalogueService.save(catalogue);
            }
            parentId = catalogue.getId();
        }
        TaskDTO taskDTO = JSONUtil.toBean(dto.getTaskJson(), TaskDTO.class);
        CatalogueTaskDTO catalogueTaskDTO = new CatalogueTaskDTO();
        catalogueTaskDTO.setLeaf(false);
        catalogueTaskDTO.setName(taskDTO.getName());
        catalogueTaskDTO.setNote(taskDTO.getNote());
        catalogueTaskDTO.setParentId(parentId);
        catalogueTaskDTO.setType(dto.getType());
        catalogueTaskDTO.setTask(taskDTO);
        // 保存任务
        Catalogue catalogue = catalogueService.saveOrUpdateCatalogueAndTask(catalogueTaskDTO);

        // 发布任务
        taskService.changeTaskLifeRecyle(catalogue.getTaskId(), JobLifeCycle.PUBLISH);
        // 推送任务
        DinkyTaskRequest dinkyTaskRequest = JSONUtil.toBean(dto.getJobConfigJson(), DinkyTaskRequest.class);
        dinkyTaskRequest.setTaskId(catalogue.getTaskId() + "");
        schedulerService.pushAddTask(dinkyTaskRequest);
    }
}
