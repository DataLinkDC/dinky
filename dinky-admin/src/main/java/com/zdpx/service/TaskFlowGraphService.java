package com.zdpx.service;


import com.zdpx.model.FlowGraph;
import org.dinky.db.service.ISuperService;
import org.dinky.model.Task;

/**
 *
 */
public interface TaskFlowGraphService extends ISuperService<FlowGraph> {

    boolean insert(FlowGraph statement);

    boolean saveOrUpdateTask(Task task);

}
