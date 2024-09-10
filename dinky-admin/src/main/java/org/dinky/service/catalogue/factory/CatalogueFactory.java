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
}
