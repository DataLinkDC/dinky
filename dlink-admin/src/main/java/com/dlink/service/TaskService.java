package com.dlink.service;


import com.dlink.db.service.ISuperService;
import com.dlink.job.JobResult;
import com.dlink.model.Task;

/**
 * 作业 服务类
 *
 * @author wenmo
 * @since 2021-05-28
 */
public interface TaskService extends ISuperService<Task> {

    JobResult submitByTaskId(Integer id);

    Task getTaskInfoById(Integer id);

    boolean saveOrUpdateTask(Task task);
}
