package com.dlink.service;


import com.dlink.common.result.Result;
import com.dlink.db.service.ISuperService;
import com.dlink.dto.TaskRollbackVersionDTO;
import com.dlink.job.JobResult;
import com.dlink.model.JobInfoDetail;
import com.dlink.model.JobInstance;
import com.dlink.model.JobLifeCycle;
import com.dlink.model.JobStatus;
import com.dlink.model.Task;
import com.dlink.result.SqlExplainResult;
import com.dlink.result.TaskOperatingResult;

import java.util.List;

/**
 * 作业 服务类
 *
 * @author wenmo
 * @since 2021-05-28
 */
public interface TaskService extends ISuperService<Task> {

    JobResult submitTask(Integer id);

    JobResult submitTaskToOnline(Task dtoTask, Integer id);

    JobResult restartTask(Integer id, String savePointPath);

    List<SqlExplainResult> explainTask(Integer id);

    Task getTaskInfoById(Integer id);

    boolean saveOrUpdateTask(Task task);

    List<Task> listFlinkSQLEnv();

    Task initDefaultFlinkSQLEnv();

    String exportSql(Integer id);

    Task getUDFByClassName(String className);

    Result releaseTask(Integer id);

    boolean developTask(Integer id);

    Result onLineTask(Integer id);

    Result reOnLineTask(Integer id, String savePointPath);

    Result offLineTask(Integer id, String type);

    Result cancelTask(Integer id);

    boolean recoveryTask(Integer id);

    boolean savepointTask(Integer taskId, String savePointType);

    JobInstance refreshJobInstance(Integer id, boolean isCoercive);

    JobInfoDetail refreshJobInfoDetail(Integer id);

    String getTaskAPIAddress();

    Result rollbackTask(TaskRollbackVersionDTO dto);

    Integer queryAllSizeByName(String name);

    Result queryAllCatalogue();

    Result<List<Task>> queryOnLineTaskByDoneStatus(List<JobLifeCycle> jobLifeCycle
            , List<JobStatus> jobStatuses, boolean includeNull, Integer catalogueId);

    void selectSavepointOnLineTask(TaskOperatingResult taskOperatingResult);

    void selectSavepointOffLineTask(TaskOperatingResult taskOperatingResult);
}
