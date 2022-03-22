package com.dlink.service;


import com.dlink.common.result.Result;
import com.dlink.db.service.ISuperService;
import com.dlink.job.JobResult;
import com.dlink.model.JobInfoDetail;
import com.dlink.model.JobInstance;
import com.dlink.model.Task;
import com.dlink.result.SqlExplainResult;

import java.util.List;

/**
 * 作业 服务类
 *
 * @author wenmo
 * @since 2021-05-28
 */
public interface TaskService extends ISuperService<Task> {

    JobResult submitTask(Integer id);

    JobResult submitTaskToOnline(Integer id);

    JobResult restartTask(Integer id);

    List<SqlExplainResult> explainTask(Integer id);

    Task getTaskInfoById(Integer id);

    boolean saveOrUpdateTask(Task task);

    List<Task> listFlinkSQLEnv();

    String exportSql(Integer id);

    Task getUDFByClassName(String className);

    Result releaseTask(Integer id);

    boolean developTask(Integer id);

    Result onLineTask(Integer id);

    Result reOnLineTask(Integer id);

    Result offLineTask(Integer id, String type);

    Result cancelTask(Integer id);

    boolean recoveryTask(Integer id);

    boolean savepointTask(Integer taskId, String savePointType);

    JobInstance refreshJobInstance(Integer id, boolean isCoercive);

    JobInfoDetail refreshJobInfoDetail(Integer id);
}
