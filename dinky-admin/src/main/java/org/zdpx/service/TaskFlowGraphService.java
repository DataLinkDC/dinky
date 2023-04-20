package org.zdpx.service;


import org.dinky.db.service.ISuperService;
import org.zdpx.model.FlowGraph;
import org.dinky.model.Task;

/**
 *
 */
public interface TaskFlowGraphService extends ISuperService<FlowGraph> {

    boolean insert(FlowGraph statement);

    boolean saveOrUpdateTask(Task task);

}
