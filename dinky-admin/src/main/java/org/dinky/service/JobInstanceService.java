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

import org.dinky.data.model.ext.JobInfoDetail;
import org.dinky.data.model.home.JobInstanceStatus;
import org.dinky.data.model.job.JobInstance;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.vo.task.JobInstanceVo;
import org.dinky.explainer.lineage.LineageResult;
import org.dinky.mybatis.service.ISuperService;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * JobInstanceService
 *
 * @since 2022/2/2 13:52
 */
public interface JobInstanceService extends ISuperService<JobInstance> {

    /**
     * Get the job instance with the given ID without a tenant.
     *
     * @param id The ID of the job instance to get.
     * @return A {@link JobInstance} object representing the found job instance.
     */
    @Deprecated
    JobInstance getByIdWithoutTenant(Integer id);

    /**
     * Get the count of job instance statuses.
     *
     * @return An integer representing the count of job instance statuses.
     */
    JobInstanceStatus getStatusCount();

    /**
     * Get a list of active job instances.
     *
     * @return A list of {@link JobInstance} objects representing the active job instances.
     */
    List<JobInstance> listJobInstanceActive();

    /**
     * Get the job information detail for the given ID.
     *
     * @param id The ID of the job information detail to get.
     * @return A {@link JobInfoDetail} object representing the found job information detail.
     */
    JobInfoDetail getJobInfoDetail(Integer id);

    /**
     * Get the job information detail for the given job instance.
     *
     * @param jobInstance The job instance to get the job information detail for.
     * @return A {@link JobInfoDetail} object representing the found job information detail.
     */
    JobInfoDetail getJobInfoDetailInfo(JobInstance jobInstance);

    /**
     * Refresh the job information detail for the given job instance ID.
     *
     * @param jobInstanceId The ID of the job instance to refresh the job information detail for.
     * @return A {@link JobInfoDetail} object representing the refreshed job information detail.
     */
    JobInfoDetail refreshJobInfoDetail(Integer jobInstanceId, boolean isForce);

    /**
     * Hook the job done for the given job ID and task ID.
     *
     * @param jobId The ID of the job to hook the job done for.
     * @param taskId The ID of the task to hook the job done for.
     * @return A boolean indicating whether the hooking was successful or not.
     */
    boolean hookJobDone(String jobId, Integer taskId);

    /**
     * Refresh the job instances for the given task IDs.
     *
     * @param taskIds The IDs of the tasks to refresh the job instances for.
     */
    void refreshJobByTaskIds(Integer... taskIds);

    /**
     * Get the lineage of a job instance with the given ID.
     *
     * @param id The ID of the job instance to get the lineage for.
     * @return A {@link LineageResult} object representing the lineage of the job instance.
     */
    LineageResult getLineage(Integer id);

    /**
     * Get the job instance with the given task ID.
     *
     * @param id The ID of the task to get the job instance for.
     * @return A {@link JobInstance} object representing the found job instance.
     */
    JobInstance getJobInstanceByTaskId(Integer id);

    /**
     * List all job instances in the system.
     *
     * @param para A {@link JsonNode} object representing the filter conditions for listing job instances.
     * @return A {@link ProTableResult<JobInstance>} object representing the list of job instances.
     */
    ProTableResult<JobInstanceVo> listJobInstances(JsonNode para);

    /**
     * Initialize the tenant by the given job instance ID.
     *
     * @param id The ID of the job instance to initialize the tenant for.
     */
    void initTenantByJobInstanceId(Integer id);
}
