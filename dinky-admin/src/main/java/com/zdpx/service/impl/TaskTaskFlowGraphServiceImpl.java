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

package com.zdpx.service.impl;

import org.dinky.data.model.Task;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.TaskService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.zdpx.coder.SceneCodeBuilder;
import com.zdpx.coder.graph.Scene;
import com.zdpx.coder.json.ToInternalConvert;
import com.zdpx.coder.json.x6.X6ToInternalConvert;
import com.zdpx.mapper.FlowGraphScriptMapper;
import com.zdpx.model.FlowGraph;
import com.zdpx.service.TaskFlowGraphService;

import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
@Service
public class TaskTaskFlowGraphServiceImpl extends SuperServiceImpl<FlowGraphScriptMapper, FlowGraph>
        implements TaskFlowGraphService {

    private final TaskService taskService;

    public TaskTaskFlowGraphServiceImpl(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public boolean insert(FlowGraph statement) {
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateTask(Task task) {
        String sql = convertConfigToSource(task);
        task.setStatement(sql);

        return taskService.saveOrUpdateTask(task);
    }

    @Override
    public List<JsonNode> getOperatorConfigurations() {
        return Scene.getOperatorConfigurations();
    }

    @Override
    public String testGraphStatement(String graph) {
        return convertToSql(graph);
    }

    private String convertConfigToSource(Task task) {
        String flowGraphScript = task.getStatement();
        String sql = convertToSql(flowGraphScript);

        FlowGraph flowGraph = new FlowGraph();
        flowGraph.setTaskId(task.getId());
        flowGraph.setScript(flowGraphScript);
        this.saveOrUpdate(flowGraph);
        return sql;
    }

    public String convertToSql(String flowGraphScript) {
        List<Task> tasks = taskService.list(new QueryWrapper<Task>().eq("dialect", "Java"));
        Map<String, String> udfDatabase =
                tasks.stream()
                        .collect(
                                Collectors.toMap(
                                        Task::getName,
                                        Task::getSavePointPath,
                                        (existing, replacement) -> replacement));
        Map<String, String> udfAll = new HashMap<>();
        udfAll.putAll(Scene.USER_DEFINED_FUNCTION);
        udfAll.putAll(udfDatabase);

        ToInternalConvert toic = new X6ToInternalConvert();
        Scene sceneInternal = toic.convert(flowGraphScript);
        SceneCodeBuilder su = new SceneCodeBuilder(sceneInternal);
        su.setUdfFunctionMap(udfAll);
        return su.build();
    }
}
