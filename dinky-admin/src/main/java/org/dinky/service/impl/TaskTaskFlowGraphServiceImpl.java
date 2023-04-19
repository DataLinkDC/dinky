package org.dinky.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import groovy.util.logging.Slf4j;
import org.dinky.db.service.impl.SuperServiceImpl;
import org.dinky.mapper.FlowGraphScriptMapper;
import org.dinky.model.FlowGraph;
import org.dinky.model.Task;
import org.dinky.service.TaskFlowGraphService;
import org.dinky.service.TaskService;
import org.dinky.zdpx.coder.SceneCodeBuilder;
import org.dinky.zdpx.coder.graph.Scene;
import org.dinky.zdpx.coder.json.SceneNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 *
 */
@Slf4j
@Service
public class TaskTaskFlowGraphServiceImpl extends SuperServiceImpl<FlowGraphScriptMapper, FlowGraph> implements TaskFlowGraphService {

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

    private String convertConfigToSource(Task task) {
        String flowGraphScript = task.getStatement();
        List<Task> tasks = taskService.list(new QueryWrapper<Task>().eq("dialect", "Java"));
        Map<String, String> udfDatabase = tasks.stream().collect(Collectors.toMap(Task::getName, Task::getSavePointPath,
                (existing, replacement) -> replacement));
        SceneNode scene = SceneCodeBuilder.readScene(flowGraphScript);
        if (scene == null) {
            log.warn("save graph generate sql.");
            return flowGraphScript;
        }
        Scene sceneInternal = SceneCodeBuilder.convertToInternal(scene);
        Map<String, String> udfAll = new HashMap<>();
        udfAll.putAll(Scene.USER_DEFINED_FUNCTION);
        udfAll.putAll(udfDatabase);
        SceneCodeBuilder su = new SceneCodeBuilder(sceneInternal);
        su.setUdfFunctionMap(udfAll);

        FlowGraph flowGraph = new FlowGraph();
        flowGraph.setTaskId(task.getId());
        flowGraph.setScript(flowGraphScript);
        this.saveOrUpdate(flowGraph);
        return su.build();
    }

}
