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

import org.dinky.Dinky;
import org.dinky.data.dto.CatalogueTaskDTO;
import org.dinky.data.dto.CreatingCatalogueTaskDTO;
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.enums.JobLifeCycle;
import org.dinky.data.exception.SqlExplainExcepition;
import org.dinky.data.model.Catalogue;
import org.dinky.scheduler.model.DinkyTaskRequest;
import org.dinky.service.CatalogueService;
import org.dinky.service.SchedulerService;
import org.dinky.service.TaskService;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Dinky.class)
public class TestServiceImplTest {
    @Autowired
    private CatalogueService catalogueService;
    @Autowired
    private TaskService taskService;
    @Autowired
    SchedulerService schedulerService;

    @Test
    @Ignore
    public void testCreateCatalogueAndTask() throws SqlExplainExcepition {
        CreatingCatalogueTaskDTO dto = new CreatingCatalogueTaskDTO();
        List<String> catalogueNames = List.of("DDP", "test1", "test3");
        dto.setCatalogueNames(catalogueNames);
        int parentId = 0;
        for (String catalogueName : catalogueNames) {
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
        CatalogueTaskDTO catalogueTaskDTO = getCatalogueTaskDTO(parentId);
        // 新建任务
        Catalogue catalogue = catalogueService.saveOrUpdateCatalogueAndTask(catalogueTaskDTO);

        // 发布任务
        taskService.changeTaskLifeRecyle(catalogue.getTaskId(), JobLifeCycle.PUBLISH);
        DinkyTaskRequest dinkyTaskRequest = new DinkyTaskRequest();
        dinkyTaskRequest.setTaskId(catalogue.getTaskId() + "");
        dinkyTaskRequest.setDelayTime(0);
        dinkyTaskRequest.setFailRetryTimes(3);
        dinkyTaskRequest.setFailRetryInterval(2);
        dinkyTaskRequest.setFlag("YES");
        dinkyTaskRequest.setTaskPriority("MEDIUM");
        // 推送任务
        schedulerService.pushAddTask(dinkyTaskRequest);
    }

    private static @NotNull CatalogueTaskDTO getCatalogueTaskDTO(int parentId) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setSavePointStrategy(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        taskDTO.setName("Flink 测试任务" + sdf.format(new Date()));
        taskDTO.setNote("备注信息");
        taskDTO.setStatement("select now()");
        taskDTO.setParallelism(5);
        taskDTO.setEnvId(-1);
        taskDTO.setStep(1);
        taskDTO.setAlertGroupId(-1);
        taskDTO.setType("kubernetes-session");
        taskDTO.setClusterId(36);
        CatalogueTaskDTO catalogueTaskDTO = new CatalogueTaskDTO();
        catalogueTaskDTO.setLeaf(false);
        catalogueTaskDTO.setName(taskDTO.getName());
        catalogueTaskDTO.setNote(taskDTO.getNote());
        catalogueTaskDTO.setParentId(parentId);
        catalogueTaskDTO.setType("FlinkSql");
        catalogueTaskDTO.setTask(taskDTO);

        return catalogueTaskDTO;
    }
}
