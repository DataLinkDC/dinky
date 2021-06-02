package com.dlink.service;


import com.dlink.db.service.ISuperService;
import com.dlink.model.Task;
import com.dlink.result.SubmitResult;

/**
 * 作业 服务类
 *
 * @author wenmo
 * @since 2021-05-28
 */
public interface TaskService extends ISuperService<Task> {

    SubmitResult submitByTaskId(Integer id);

    Task getTaskInfoById(Integer id);

    boolean saveOrUpdateTask(Task task);
}
