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

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;

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

    List<Task> getAllUDF();

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

    String exportJsonByTaskId(Integer taskId);

    String exportJsonByTaskIds(JsonNode para);

    Result uploadTaskJson(MultipartFile file) throws Exception;

    void handleJobDone(JobInstance jobInstance);

    Result queryAllCatalogue();

    Result<List<Task>> queryOnLineTaskByDoneStatus(List<JobLifeCycle> jobLifeCycle
        , List<JobStatus> jobStatuses, boolean includeNull, Integer catalogueId);

    void selectSavepointOnLineTask(TaskOperatingResult taskOperatingResult);

    void selectSavepointOffLineTask(TaskOperatingResult taskOperatingResult);

}
