package com.dlink.service;


import com.dlink.db.service.ISuperService;
import com.dlink.job.JobResult;
import com.dlink.model.Task;

import java.util.List;

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

    List<Task> listFlinkSQLEnv();

    String exportSql(Integer id);

    Task getUDFByClassName(String className);
}
