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
import org.dinky.data.dto.TaskSubmitDto;
import org.dinky.data.enums.JobLifeCycle;
import org.dinky.data.exception.ExcuteException;
import org.dinky.data.exception.NotSupportExplainExcepition;
import org.dinky.data.exception.SqlExplainExcepition;
import org.dinky.data.model.Task;
import org.dinky.data.model.home.JobModelOverview;
import org.dinky.data.model.home.JobTypeOverView;
import org.dinky.data.result.Result;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.explainer.lineage.LineageResult;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.job.JobConfig;
import org.dinky.job.JobResult;
import org.dinky.mybatis.service.ISuperService;

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

    /**
     * Build SQL statements for environment based on the given task.
     *
     * @param task The {@link AbstractStatementDTO} object representing the task to build SQL statements for.
     * @return A string containing the built SQL statements.
     */
    String buildEnvSql(AbstractStatementDTO task);

    /**
     * Submit the given task and return the job result.
     *
     * @param submitDto The param of the task to submit.
     * @return A {@link JobResult} object representing the result of the submitted task.
     * @throws ExcuteException If there is an error executing the task.
     */
    JobResult submitTask(TaskSubmitDto submitDto) throws Exception;

    /**
     * Debug the given task and return the job result.
     *
     * @param task The param of preview task.
     * @return A {@link JobResult} object representing the result of the submitted task.
     * @throws ExcuteException If there is an error debugging the task.
     */
    JobResult debugTask(TaskDTO task) throws Exception;

    /**
     * This class implements the TaskService interface and provides functionality to restart a task.
     * It checks if the task exists and if it is not a common SQL dialect. If the job instance is not done,
     * it cancels the job and triggers a savepoint if necessary.
     * It then waits for the job to complete and submits the task again with the savepoint path.
     * @param id The ID of the task to restart.
     * @param savePointPath The savepoint path to use for restarting the task.
     * @return A JobResult object containing the status of the job.
     * @throws Exception If there is an error while restarting the task.
     */
    JobResult restartTask(Integer id, String savePointPath) throws Exception;

    /**
     * Savepoint the given task job and return the savepoint result.
     *
     * @param task The {@link TaskDTO} object representing the task to savepoint.
     * @param savePointType The type of savepoint to create.
     * @return A {@link SavePointResult} object representing the savepoint result.
     */
    SavePointResult savepointTaskJob(TaskDTO task, SavePointType savePointType);

    /**
     * Explain the given task and return a list of SQL explain results.
     *
     * @param task The {@link TaskDTO} object representing the task to explain.
     * @return A list of {@link SqlExplainResult} objects representing the SQL explain results.
     * @throws NotSupportExplainExcepition If the task does not support SQL explain.
     */
    List<SqlExplainResult> explainTask(TaskDTO task) throws NotSupportExplainExcepition;

    /**
     * Cancel the execution of the given task job.
     *
     * @param task The {@link TaskDTO} object representing the task to cancel.
     * @return true if the task job is successfully cancelled, false otherwise.
     */
    boolean cancelTaskJob(TaskDTO task, boolean withSavePoint, boolean forceCancel);

    /**
     * Get the stream graph of the given task job.
     *
     * @param taskDTO The {@link TaskDTO} object representing the task to get the stream graph for.
     * @return A JSON object representing the stream graph.
     */
    ObjectNode getStreamGraph(TaskDTO taskDTO);

    /**
     * Export the SQL statements for the given task ID.
     *
     * @param id The ID of the task to export SQL statements for.
     * @return A string containing the exported SQL statements.
     */
    String exportSql(Integer id);

    /**
     * Get the job plan for the given task.
     *
     * @param task The {@link TaskDTO} object representing the task to get the job plan for.
     * @return A JSON object representing the job plan.
     */
    ObjectNode getJobPlan(TaskDTO task);

    /**
     * Get the information of the given task by its ID.
     *
     * @param id The ID of the task to get information for.
     * @return A {@link TaskDTO} object representing the task information.
     */
    TaskDTO getTaskInfoById(Integer id);

    /**
     * Initialize the tenant by task ID.
     *
     * @param id The ID of the task to initialize the tenant for.
     */
    void initTenantByTaskId(Integer id);

    /**
     * Change the life cycle of the given task.
     *
     * @param taskId The ID of the task to change the life cycle for.
     * @param lifeCycle The new life cycle of the task.
     * @return true if the life cycle is successfully changed, false otherwise.
     */
    boolean changeTaskLifeRecyle(Integer taskId, JobLifeCycle lifeCycle) throws SqlExplainExcepition;

    /**
     * Save or update the given task.
     *
     * @param task The {@link Task} object representing the task to save or update.
     * @return true if the task is successfully saved or updated, false otherwise.
     */
    boolean saveOrUpdateTask(Task task);

    /**
     * Get a list of all Flink SQL environments.
     *
     * @return A list of {@link Task} objects representing the Flink SQL environments.
     */
    List<Task> listFlinkSQLEnv();

    /**
     * Initialize the default Flink SQL environment for the given tenant ID.
     *
     * @param tenantId The ID of the tenant to initialize the default Flink SQL environment for.
     * @return A {@link Task} object representing the initialized default Flink SQL environment.
     */
    Task initDefaultFlinkSQLEnv(Integer tenantId);

    /**
     * Get a list of all user-defined functions (UDFs) in the system.
     *
     * @return A list of {@link Task} objects representing the UDFs.
     */
    List<Task> getAllUDF();

    /**
     * Get the API address of the given task.
     *
     * @return A string containing the API address of the task.
     */
    String getTaskAPIAddress();

    /**
     * Rollback the given task version.
     *
     * @param dto The {@link TaskRollbackVersionDTO} object representing the task to rollback.
     * @return A {@link Result} object indicating the result of the rollback operation.
     */
    boolean rollbackTask(TaskRollbackVersionDTO dto);

    /**
     * Get the size of all tasks with the given name in the system.
     *
     * @param name The name of the tasks to query.
     * @return An integer representing the number of tasks with the given name.
     */
    Integer queryAllSizeByName(String name);

    /**
     * Export the JSON data for the given task ID.
     *
     * @param taskId The ID of the task to export JSON data for.
     * @return A string containing the exported JSON data.
     */
    String exportJsonByTaskId(Integer taskId);

    /**
     * Export the JSON data for multiple tasks by their IDs.
     *
     * @param para A {@link JsonNode} object representing the task IDs to export JSON data for.
     * @return A string containing the exported JSON data.
     */
    String exportJsonByTaskIds(JsonNode para);

    /**
     * Upload a task JSON file to the server.
     *
     * @param file A {@link MultipartFile} object representing the task JSON file to upload.
     * @return A {@link Result} object indicating the result of the upload operation.
     * @throws Exception If an error occurs during the upload process.
     */
    Result<Void> uploadTaskJson(MultipartFile file) throws Exception;

    /**
     * Query all task catalogs in the system.
     *
     * @return A {@link Result} object containing a {@link Tree} object representing the task catalogs.
     */
    Result<Tree<Integer>> queryAllCatalogue();

    /**
     * Get the task with the given name and tenant ID.
     *
     * @param name The name of the task to get.
     * @param tenantId The ID of the tenant to get the task for.
     * @return A {@link Task} object representing the found task.
     */
    Task getTaskByNameAndTenantId(String name, Integer tenantId);

    /**
     * Get the online rate of all tasks in the system.
     *
     * @return A list of {@link JobTypeOverView} objects representing the online rates of all tasks.
     */
    List<JobTypeOverView> getTaskOnlineRate();

    /**
     * Get the streaming or batch model overview of all jobs in the system.
     *
     * @return A {@link JobModelOverview} object representing the job model overview.
     */
    JobModelOverview getJobStreamingOrBatchModelOverview();

    /**
     * Get the task with the given name and tenant ID.
     *
     * @param id The id of the task to get.
     * @return A {@link LineageResult} object representing the found task lineage.
     */
    LineageResult getTaskLineage(Integer id);

    /**
     * Build the job submit config with the given task
     * @param task
     * @return
     */
    JobConfig buildJobSubmitConfig(TaskDTO task);
}
