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

import com.dlink.common.result.ProTableResult;
import com.dlink.db.service.ISuperService;
import com.dlink.explainer.lineage.LineageResult;
import com.dlink.model.JobInfoDetail;
import com.dlink.model.JobInstance;
import com.dlink.model.JobInstanceStatus;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * JobInstanceService
 *
 * @author wenmo
 * @since 2022/2/2 13:52
 */
public interface JobInstanceService extends ISuperService<JobInstance> {

    JobInstance getByIdWithoutTenant(Integer id);

    JobInstanceStatus getStatusCount(boolean isHistory);

    List<JobInstance> listJobInstanceActive();

    JobInfoDetail getJobInfoDetail(Integer id);

    JobInfoDetail getJobInfoDetailInfo(JobInstance jobInstance);

    JobInfoDetail refreshJobInfoDetailInfo(JobInstance jobInstance);

    LineageResult getLineage(Integer id);

    JobInstance getJobInstanceByTaskId(Integer id);

    ProTableResult<JobInstance> listJobInstances(JsonNode para);

    void initTenantByJobInstanceId(Integer id);
}
