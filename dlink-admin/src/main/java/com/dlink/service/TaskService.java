package com.dlink.service;


import com.dlink.common.result.Result;
import com.dlink.db.service.ISuperService;
import com.dlink.job.JobResult;
import com.dlink.model.Task;
import com.dlink.result.SubmitResult;

/**
 * 作业 服务类
 *
 * @author wenmo
 * @since 2021-05-28
 */
public interface TaskService extends ISuperService<Task> {

    JobResult submitByTaskId(Integer id);

    Result submitApplicationByTaskId(Integer id);

    Task getTaskInfoById(Integer id);

    boolean saveOrUpdateTask(Task task);
}
