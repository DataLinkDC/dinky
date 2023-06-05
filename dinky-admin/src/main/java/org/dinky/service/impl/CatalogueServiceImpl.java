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

import static org.dinky.assertion.Asserts.isNotNull;
import static org.dinky.assertion.Asserts.isNull;

import org.dinky.assertion.Asserts;
import org.dinky.data.dto.CatalogueTaskDTO;
import org.dinky.data.enums.JobLifeCycle;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.model.Catalogue;
import org.dinky.data.model.History;
import org.dinky.data.model.JobInstance;
import org.dinky.data.model.Statement;
import org.dinky.data.model.Task;
import org.dinky.mapper.CatalogueMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.CatalogueService;
import org.dinky.service.HistoryService;
import org.dinky.service.JobInstanceService;
import org.dinky.service.StatementService;
import org.dinky.service.TaskService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;

/**
 * CatalogueServiceImpl
 *
 * @since 2021/5/28 14:02
 */
@Service
@RequiredArgsConstructor
public class CatalogueServiceImpl extends SuperServiceImpl<CatalogueMapper, Catalogue>
        implements CatalogueService {

    private final TaskService taskService;
    private final JobInstanceService jobInstanceService;

    private final HistoryService historyService;

    private final StatementService statementService;

    @Override
    public List<Catalogue> getAllData() {
        return this.list();
    }

    @Override
    public Catalogue findByParentIdAndName(Integer parentId, String name) {
        return baseMapper.selectOne(
                Wrappers.<Catalogue>query().eq("parent_id", parentId).eq("name", name));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Catalogue saveOrUpdateCatalogueAndTask(CatalogueTaskDTO catalogueTaskDTO) {
        Task task = null;
        Catalogue catalogue = null;
        if (catalogueTaskDTO.getId() == null) {
            task = new Task();
            catalogue = new Catalogue();
        } else {
            catalogue = baseMapper.selectById(catalogueTaskDTO.getId());
            task = taskService.getById(catalogue.getTaskId());
        }
        task.setName(catalogueTaskDTO.getName());
        task.setDialect(catalogueTaskDTO.getDialect());
        task.setConfig(Collections.singletonList(catalogueTaskDTO.getConfig()));
        taskService.saveOrUpdateTask(task);

        catalogue.setTenantId(catalogueTaskDTO.getTenantId());
        catalogue.setName(catalogueTaskDTO.getName());
        catalogue.setIsLeaf(true);
        catalogue.setTaskId(task.getId());
        catalogue.setType(catalogueTaskDTO.getDialect());
        catalogue.setParentId(catalogueTaskDTO.getParentId());
        this.saveOrUpdate(catalogue);
        return catalogue;
    }

    @Override
    public Catalogue createCatalogAndFileTask(CatalogueTaskDTO catalogueTaskDTO, String ment) {
        Task task = new Task();
        task.setName(catalogueTaskDTO.getName());
        task.setDialect(catalogueTaskDTO.getDialect());
        task.setStatement(ment);
        task.setEnabled(true);
        taskService.saveOrUpdateTask(task);
        Catalogue catalogue = new Catalogue();
        catalogue.setName(catalogueTaskDTO.getName());
        catalogue.setIsLeaf(true);
        catalogue.setTaskId(task.getId());
        catalogue.setType(catalogueTaskDTO.getDialect());
        catalogue.setParentId(catalogueTaskDTO.getParentId());
        this.save(catalogue);
        return catalogue;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean toRename(Catalogue catalogue) {
        Catalogue oldCatalogue = this.getById(catalogue.getId());
        if (isNull(oldCatalogue)) {
            return false;
        } else {
            Task task = new Task();
            task.setId(oldCatalogue.getTaskId());
            task.setName(catalogue.getName());
            taskService.updateById(task);
            this.updateById(catalogue);
            return true;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<String> removeCatalogueAndTaskById(Integer id) {
        List<String> errors = new ArrayList<>();
        Catalogue catalogue = this.getById(id);
        if (isNull(catalogue)) {
            errors.add(id + "不存在！");
        } else {
            if (isNotNull(catalogue.getTaskId())) {
                Integer taskId = catalogue.getTaskId();
                JobInstance job = jobInstanceService.getJobInstanceByTaskId(taskId);
                if (job == null) {
                    taskService.removeById(taskId);
                    statementService.removeById(taskId);
                    this.removeById(id);
                } else if (job != null && !JobStatus.RUNNING.getValue().equals(job.getStatus())) {
                    historyService.remove(new QueryWrapper<History>().eq("task_id", taskId));
                    jobInstanceService.remove(
                            new QueryWrapper<JobInstance>().eq("task_id", taskId));
                    taskService.removeById(taskId);
                    statementService.removeById(taskId);
                    this.removeById(id);
                } else {
                    errors.add(job.getName());
                }
            } else {
                List<Catalogue> all = this.getAllData();
                Set<Catalogue> del = new HashSet<>();
                this.findAllCatalogueInDir(id, all, del);
                List<String> actives = this.analysisActiveCatalogues(del);
                if (actives.isEmpty()) {
                    for (Catalogue c : del) {
                        taskService.removeById(c.getTaskId());
                        statementService.removeById(c.getTaskId());
                        this.removeById(c.getId());
                    }
                } else {
                    errors.addAll(actives);
                }
            }
        }

        return errors;
    }

    private void findAllCatalogueInDir(Integer id, List<Catalogue> all, Set<Catalogue> del) {
        List<Catalogue> relatedList =
                all.stream()
                        .filter(
                                catalogue ->
                                        id.equals(catalogue.getId())
                                                || id.equals(catalogue.getParentId()))
                        .collect(Collectors.toList());
        List<Catalogue> subDirCatalogue =
                relatedList.stream()
                        .filter(catalogue -> catalogue.getType() == null)
                        .collect(Collectors.toList());
        subDirCatalogue.forEach(
                catalogue -> {
                    if (!id.equals(catalogue.getId())) {
                        findAllCatalogueInDir(catalogue.getId(), all, del);
                    }
                });
        del.addAll(relatedList);
    }

    private List<String> analysisActiveCatalogues(Set<Catalogue> del) {
        List<Integer> actives =
                jobInstanceService.listJobInstanceActive().stream()
                        .map(JobInstance::getTaskId)
                        .collect(Collectors.toList());
        List<Catalogue> activeCatalogue =
                del.stream()
                        .filter(
                                catalogue ->
                                        catalogue.getTaskId() != null
                                                && actives.contains(catalogue.getTaskId()))
                        .collect(Collectors.toList());
        return activeCatalogue.stream()
                .map(catalogue -> taskService.getById(catalogue.getTaskId()).getName())
                .collect(Collectors.toList());
    }

    @Override
    public boolean moveCatalogue(Integer id, Integer parentId) {
        Catalogue catalogue = this.getById(id);
        if (isNull(catalogue)) {
            return false;
        } else {
            catalogue.setParentId(parentId);
            return updateById(catalogue);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean copyTask(Catalogue catalogue) {

        if (ObjectUtil.isNull(catalogue.getTaskId())) {
            return false;
        }

        Task oldTask = taskService.getById(catalogue.getTaskId());

        if (ObjectUtil.isNull(oldTask)) {
            return false;
        }
        // 查询作业名称
        int size = taskService.queryAllSizeByName(oldTask.getName());

        Task newTask = new Task();
        BeanUtil.copyProperties(oldTask, newTask);
        newTask.setId(null);
        // 设置复制后的作业名称为：原名称+自增序列
        size = size + 1;
        newTask.setName(oldTask.getName() + "_" + size);
        newTask.setStep(JobLifeCycle.DEVELOP.getValue());
        taskService.save(newTask);

        Statement statementServiceById = statementService.getById(catalogue.getTaskId());
        // 新建作业的sql语句
        Statement statement = new Statement();
        statement.setStatement(statementServiceById.getStatement());
        statement.setId(newTask.getId());
        statementService.save(statement);

        Catalogue one =
                this.getOne(
                        new LambdaQueryWrapper<Catalogue>()
                                .eq(Catalogue::getTaskId, catalogue.getTaskId()));

        catalogue.setName(newTask.getName());
        catalogue.setIsLeaf(one.getIsLeaf());
        catalogue.setTaskId(newTask.getId());
        catalogue.setType(one.getType());
        catalogue.setParentId(one.getParentId());

        return this.save(catalogue);
    }

    @Override
    public Integer addDependCatalogue(String[] catalogueNames) {
        Integer parentId = 0;
        for (int i = 0; i < catalogueNames.length - 1; i++) {
            String catalogueName = catalogueNames[i];
            Catalogue catalogue =
                    getOne(
                            new QueryWrapper<Catalogue>()
                                    .eq("name", catalogueName)
                                    .eq("parent_id", parentId)
                                    .last(" limit 1"));
            if (Asserts.isNotNull(catalogue)) {
                parentId = catalogue.getId();
                continue;
            }
            catalogue = new Catalogue();
            catalogue.setName(catalogueName);
            catalogue.setParentId(parentId);
            catalogue.setIsLeaf(false);
            this.save(catalogue);
            parentId = catalogue.getId();
        }
        return parentId;
    }
}
