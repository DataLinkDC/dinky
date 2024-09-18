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

package org.dinky.service.catalogue.factory;

import org.dinky.config.Dialect;
import org.dinky.data.bo.catalogue.export.ExportCatalogueBO;
import org.dinky.data.bo.catalogue.export.ExportTaskBO;
import org.dinky.data.constant.CommonConstant;
import org.dinky.data.enums.GatewayType;
import org.dinky.data.enums.JobLifeCycle;
import org.dinky.data.model.Catalogue;
import org.dinky.data.model.Task;

import java.util.Objects;

import org.springframework.stereotype.Component;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * CatalogueFactory
 *
 * @since 2024/5/8 19:12
 */
@Slf4j
@Component
public class CatalogueFactory {

    public Task getNewTask(Task oldTask, String newTaskName) {
        if (Objects.isNull(oldTask)) {
            return null;
        }
        Task newTask = new Task();
        BeanUtil.copyProperties(oldTask, newTask);
        newTask.setId(null);
        newTask.setJobInstanceId(null);
        newTask.setName(newTaskName);
        // set tasks to be in development status by default
        newTask.setStep(JobLifeCycle.DEVELOP.getValue());
        newTask.setVersionId(null);

        // mybatis auto fill
        newTask.setCreateTime(null);
        newTask.setUpdateTime(null);
        newTask.setCreator(null);
        newTask.setUpdater(null);
        newTask.setOperator(null);
        newTask.setTenantId(null);
        return newTask;
    }

    public Task getTask(ExportTaskBO exportTaskBO, Integer firstLevelOwner) {
        Task task = new Task();
        task.setName(exportTaskBO.getName());
        task.setDialect(exportTaskBO.getDialect());
        task.setType(exportTaskBO.getType());
        task.setCheckPoint(exportTaskBO.getCheckPoint());
        task.setSavePointStrategy(exportTaskBO.getSavePointStrategy());
        task.setParallelism(exportTaskBO.getParallelism());
        task.setFragment(exportTaskBO.getFragment());
        task.setStatementSet(exportTaskBO.getStatementSet());
        task.setBatchModel(exportTaskBO.getBatchModel());
        task.setEnvId(exportTaskBO.getEnvId());
        task.setAlertGroupId(exportTaskBO.getAlertGroupId());
        task.setConfigJson(exportTaskBO.getConfigJson());
        task.setNote(exportTaskBO.getNote());
        task.setStep(exportTaskBO.getStep());
        task.setEnabled(exportTaskBO.getEnabled());
        task.setStatement(exportTaskBO.getStatement());
        task.setFirstLevelOwner(firstLevelOwner);
        return task;
    }

    public Catalogue getNewCatalogue(Catalogue paramCatalogue, Catalogue oldCatalogue, Task newTask) {
        Catalogue newCatalogue = new Catalogue();
        BeanUtil.copyProperties(paramCatalogue, newCatalogue);
        newCatalogue.setName(newTask.getName());
        newCatalogue.setIsLeaf(oldCatalogue.getIsLeaf());
        newCatalogue.setTaskId(newTask.getId());
        newCatalogue.setType(oldCatalogue.getType());
        newCatalogue.setParentId(oldCatalogue.getParentId());
        newCatalogue.setId(null);

        // mybatis auto fill
        newCatalogue.setCreateTime(null);
        newCatalogue.setUpdateTime(null);
        newCatalogue.setCreator(null);
        newCatalogue.setUpdater(null);
        newCatalogue.setTenantId(null);
        return newCatalogue;
    }

    public Catalogue getCatalogue(ExportCatalogueBO exportCatalogueBO, Integer parentId, Integer taskId) {
        Catalogue catalogue = new Catalogue();
        catalogue.setParentId(parentId);
        catalogue.setTaskId(taskId);
        catalogue.setName(exportCatalogueBO.getName());
        catalogue.setType(exportCatalogueBO.getType());
        catalogue.setEnabled(exportCatalogueBO.getEnabled());
        catalogue.setIsLeaf(exportCatalogueBO.getIsLeaf());
        return catalogue;
    }

    /**
     * Reset Task value
     *
     * @param task Task
     */
    public void resetTask(Task task, String dialect) {
        task.setStep(JobLifeCycle.DEVELOP.getValue());
        task.setEnabled(Boolean.TRUE);
        task.setVersionId(null);
        task.setJobInstanceId(null);
        if (Dialect.isFlinkSql(dialect, false)) {
            task.setType(GatewayType.LOCAL.getLongValue());
            task.setParallelism(1);
            task.setSavePointStrategy(CommonConstant.SAVE_POINT_STRATEGY_DISABLE);
            task.setEnvId(CommonConstant.ENV_DISABLE);
            task.setAlertGroupId(CommonConstant.ALERT_GROUP_DISABLE);
            task.setFragment(Boolean.FALSE);
        }
    }

    public ExportCatalogueBO getExportCatalogueBo(Catalogue catalogue, Task task) {
        return ExportCatalogueBO.builder()
                .name(catalogue.getName())
                .enabled(catalogue.getEnabled())
                .isLeaf(catalogue.getIsLeaf())
                .type(catalogue.getType())
                .task(getExportTaskBo(task))
                .build();
    }

    private ExportTaskBO getExportTaskBo(Task task) {
        if (Objects.isNull(task)) {
            return null;
        }
        // Reset task
        resetTask(task, task.getDialect());
        return ExportTaskBO.builder()
                .name(task.getName())
                .dialect(task.getDialect())
                .type(task.getType())
                .checkPoint(task.getCheckPoint())
                .savePointStrategy(task.getSavePointStrategy())
                .parallelism(task.getParallelism())
                .fragment(task.getFragment())
                .statementSet(task.getStatementSet())
                .batchModel(task.getBatchModel())
                .envId(task.getEnvId())
                .alertGroupId(task.getAlertGroupId())
                .configJson(task.getConfigJson())
                .note(task.getNote())
                .step(task.getStep())
                .enabled(task.getEnabled())
                .statement(task.getStatement())
                .build();
    }
}
