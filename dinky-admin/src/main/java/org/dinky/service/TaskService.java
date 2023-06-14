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

package org.dinky.service;

import org.dinky.data.dto.TaskRollbackVersionDTO;
import org.dinky.data.enums.JobLifeCycle;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.model.JobInfoDetail;
import org.dinky.data.model.JobInstance;
import org.dinky.data.model.JobModelOverview;
import org.dinky.data.model.JobTypeOverView;
import org.dinky.data.model.Task;
import org.dinky.data.result.Result;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.data.result.TaskOperatingResult;
import org.dinky.job.JobResult;
import org.dinky.mybatis.service.ISuperService;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;

import cn.hutool.core.lang.tree.Tree;

/**
 * 作业 服务类
 *
 * @since 2021-05-28
 */
public interface TaskService extends ISuperService<Task> {

    JobResult submitTask(Integer id);

    JobResult submitTaskToOnline(Task dtoTask, Integer id);

    JobResult restartTask(Integer id, String savePointPath);

    List<SqlExplainResult> explainTask(Integer id);

    Task getTaskInfoById(Integer id);

    void initTenantByTaskId(Integer id);

    boolean saveOrUpdateTask(Task task);

    List<Task> listFlinkSQLEnv();

    Task initDefaultFlinkSQLEnv(Integer tenantId);

    String exportSql(Integer id);

    Task getUDFByClassName(String className);

    List<Task> getAllUDF();

    Result<Void> releaseTask(Integer id);

    boolean developTask(Integer id);

    Result<JobResult> onLineTask(Integer id);

    Result<JobResult> reOnLineTask(Integer id, String savePointPath);

    Result<Void> offLineTask(Integer id, String type);

    Result<Void> cancelTask(Integer id);

    boolean recoveryTask(Integer id);

    boolean savepointTask(Integer taskId, String savePointType);

    JobInstance refreshJobInstance(Integer id, boolean isCoercive);

    JobInfoDetail refreshJobInfoDetail(Integer id);

    String getTaskAPIAddress();

    Result<Void> rollbackTask(TaskRollbackVersionDTO dto);

    Integer queryAllSizeByName(String name);

    String exportJsonByTaskId(Integer taskId);

    String exportJsonByTaskIds(JsonNode para);

    Result<Void> uploadTaskJson(MultipartFile file) throws Exception;

    void handleJobDone(JobInstance jobInstance);

    Result<Tree<Integer>> queryAllCatalogue();

    Result<List<Task>> queryOnLineTaskByDoneStatus(
            List<JobLifeCycle> jobLifeCycle,
            List<JobStatus> jobStatuses,
            boolean includeNull,
            Integer catalogueId);

    void selectSavepointOnLineTask(TaskOperatingResult taskOperatingResult);

    void selectSavepointOffLineTask(TaskOperatingResult taskOperatingResult);

    Task getTaskByNameAndTenantId(String name, Integer tenantId);

    JobStatus checkJobStatus(JobInfoDetail jobInfoDetail);

    List<JobTypeOverView> getTaskOnlineRate();

    JobModelOverview getJobStreamingOrBatchModelOverview();
}
