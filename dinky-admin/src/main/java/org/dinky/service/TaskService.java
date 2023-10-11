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

import org.dinky.data.dto.AbstractStatementDTO;
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.dto.TaskRollbackVersionDTO;
import org.dinky.data.enums.JobLifeCycle;
import org.dinky.data.exception.NotSupportExplainExcepition;
import org.dinky.data.model.JobModelOverview;
import org.dinky.data.model.JobTypeOverView;
import org.dinky.data.model.Task;
import org.dinky.data.result.Result;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.job.JobResult;
import org.dinky.mybatis.service.ISuperService;
import org.dinky.process.exception.ExcuteException;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.lang.tree.Tree;

/**
 * 作业 服务类
 *
 * @since 2021-05-28
 */
public interface TaskService extends ISuperService<Task> {

    String buildEnvSql(AbstractStatementDTO task);

    JobResult submitTask(Integer id, String savePointPath) throws ExcuteException;

    JobResult restartTask(Integer id, String savePointPath) throws ExcuteException;

    SavePointResult savepointTaskJob(TaskDTO task, String savePointType);

    List<SqlExplainResult> explainTask(TaskDTO task) throws NotSupportExplainExcepition;

    boolean cancelTaskJob(TaskDTO task);

    ObjectNode getStreamGraph(TaskDTO taskDTO);

    String exportSql(Integer id);

    ObjectNode getJobPlan(TaskDTO task);

    TaskDTO getTaskInfoById(Integer id);

    void initTenantByTaskId(Integer id);

    boolean changeTaskLifeRecyle(Integer taskId, JobLifeCycle lifeCycle);

    boolean saveOrUpdateTask(Task task);

    List<Task> listFlinkSQLEnv();

    Task initDefaultFlinkSQLEnv(Integer tenantId);

    List<Task> getAllUDF();

    String getTaskAPIAddress();

    Result<Void> rollbackTask(TaskRollbackVersionDTO dto);

    Integer queryAllSizeByName(String name);

    String exportJsonByTaskId(Integer taskId);

    String exportJsonByTaskIds(JsonNode para);

    Result<Void> uploadTaskJson(MultipartFile file) throws Exception;

    Result<Tree<Integer>> queryAllCatalogue();

    Task getTaskByNameAndTenantId(String name, Integer tenantId);

    List<JobTypeOverView> getTaskOnlineRate();

    JobModelOverview getJobStreamingOrBatchModelOverview();
}
