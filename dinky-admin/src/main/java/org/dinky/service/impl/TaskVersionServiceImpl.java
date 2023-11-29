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

import org.dinky.assertion.Asserts;
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.dto.TaskVersionConfigureDTO;
import org.dinky.data.model.TaskVersion;
import org.dinky.mapper.TaskVersionMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.TaskVersionService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.hutool.core.bean.BeanUtil;

@Service
public class TaskVersionServiceImpl extends SuperServiceImpl<TaskVersionMapper, TaskVersion>
        implements TaskVersionService {

    @Override
    public List<TaskVersion> getTaskVersionByTaskId(Integer taskId) {

        return baseMapper.selectList(new LambdaQueryWrapper<TaskVersion>()
                .eq(TaskVersion::getTaskId, taskId)
                .orderByDesc(true, TaskVersion::getVersionId));
    }

    @Override
    public Integer createTaskVersionSnapshot(TaskDTO task) {
        List<TaskVersion> taskVersions = getTaskVersionByTaskId(task.getId());
        List<Integer> versionIds =
                taskVersions.stream().map(TaskVersion::getVersionId).collect(Collectors.toList());
        Map<Integer, TaskVersion> versionMap =
                taskVersions.stream().collect(Collectors.toMap(TaskVersion::getVersionId, t -> t));

        TaskVersion taskVersion = new TaskVersion();
        BeanUtil.copyProperties(task, taskVersion);

        TaskVersionConfigureDTO taskVersionConfigureDTO = new TaskVersionConfigureDTO();
        BeanUtil.copyProperties(task, taskVersionConfigureDTO);

        taskVersion.setTaskConfigure(taskVersionConfigureDTO);
        taskVersion.setTaskId(taskVersion.getId());
        taskVersion.setId(null);

        if (Asserts.isNull(task.getVersionId()) || !versionIds.contains(task.getVersionId())) {
            // FIRST RELEASE, ADD NEW VERSION
            taskVersion.setVersionId(1);
            taskVersion.setCreateTime(LocalDateTime.now());
            save(taskVersion);
        } else {
            // Explain that there is a version, you need to determine whether it is an old version after fallback
            TaskVersion version = versionMap.getOrDefault(task.getVersionId(), new TaskVersion());
            // IDs are not involved in the comparison
            if (!taskVersion.equals(version)) {
                int newVersionId = versionIds.isEmpty() ? 1 : Collections.max(versionIds) + 1;
                taskVersion.setVersionId(newVersionId);
                taskVersion.setCreateTime(LocalDateTime.now());
                save(taskVersion);
            }
        }
        return taskVersion.getVersionId();
    }
}
