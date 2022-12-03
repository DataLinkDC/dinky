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

package com.dlink.service.impl;

import com.dlink.api.FlinkAPI;
import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkRestResultConstant;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.JobHistoryMapper;
import com.dlink.model.JobHistory;
import com.dlink.service.JobHistoryService;
import com.dlink.utils.JSONUtil;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * JobHistoryServiceImpl
 *
 * @author wenmo
 * @since 2022/3/2 20:00
 **/
@Service
public class JobHistoryServiceImpl extends SuperServiceImpl<JobHistoryMapper, JobHistory> implements JobHistoryService {

    private static final Logger log = LoggerFactory.getLogger(JobHistoryServiceImpl.class);

    @Override
    public JobHistory getByIdWithoutTenant(Integer id) {
        return baseMapper.getByIdWithoutTenant(id);
    }

    @Override
    public JobHistory getJobHistory(Integer id) {
        return getJobHistoryInfo(baseMapper.getByIdWithoutTenant(id));
    }

    @Override
    public JobHistory getJobHistoryInfo(JobHistory jobHistory) {
        if (Asserts.isNotNull(jobHistory)) {
            if (Asserts.isNotNullString(jobHistory.getJobJson())) {
                jobHistory.setJob(JSONUtil.parseObject(jobHistory.getJobJson()));
                jobHistory.setJobJson(null);
            }
            if (Asserts.isNotNullString(jobHistory.getExceptionsJson())) {
                jobHistory.setExceptions(JSONUtil.parseObject(jobHistory.getExceptionsJson()));
                jobHistory.setExceptionsJson(null);
            }
            if (Asserts.isNotNullString(jobHistory.getCheckpointsJson())) {
                jobHistory.setCheckpoints(JSONUtil.parseObject(jobHistory.getCheckpointsJson()));
                jobHistory.setCheckpointsJson(null);
            }
            if (Asserts.isNotNullString(jobHistory.getCheckpointsConfigJson())) {
                jobHistory.setCheckpointsConfig(JSONUtil.parseObject(jobHistory.getCheckpointsConfigJson()));
                jobHistory.setCheckpointsConfigJson(null);
            }
            if (Asserts.isNotNullString(jobHistory.getConfigJson())) {
                jobHistory.setConfig(JSONUtil.parseObject(jobHistory.getConfigJson()));
                jobHistory.setConfigJson(null);
            }
            if (Asserts.isNotNullString(jobHistory.getJarJson())) {
                jobHistory.setJar(JSONUtil.parseObject(jobHistory.getJarJson()));
                jobHistory.setJarJson(null);
            }
            if (Asserts.isNotNullString(jobHistory.getClusterJson())) {
                jobHistory.setCluster(JSONUtil.parseObject(jobHistory.getClusterJson()));
                jobHistory.setClusterJson(null);
            }
            if (Asserts.isNotNullString(jobHistory.getClusterConfigurationJson())) {
                jobHistory.setClusterConfiguration(JSONUtil.parseObject(jobHistory.getClusterConfigurationJson()));
                jobHistory.setClusterConfigurationJson(null);
            }
        }
        return jobHistory;
    }

    @Override
    public JobHistory refreshJobHistory(Integer id, String jobManagerHost, String jobId, boolean needSave) {
        JobHistory jobHistory = new JobHistory();
        jobHistory.setId(id);
        try {
            JsonNode jobInfo = FlinkAPI.build(jobManagerHost).getJobInfo(jobId);
            if (Asserts.isNull(jobInfo) || jobInfo.has(FlinkRestResultConstant.ERRORS)) {
                final JobHistory dbHistory = getById(id);
                if (Objects.nonNull(dbHistory)) {
                    jobHistory = dbHistory;
                }
                jobHistory.setError(true);
                return jobHistory;
            }
            JsonNode exception = FlinkAPI.build(jobManagerHost).getException(jobId);
            JsonNode checkPoints = FlinkAPI.build(jobManagerHost).getCheckPoints(jobId);
            JsonNode checkPointsConfig = FlinkAPI.build(jobManagerHost).getCheckPointsConfig(jobId);
            JsonNode jobsConfig = FlinkAPI.build(jobManagerHost).getJobsConfig(jobId);
            jobHistory.setJobJson(JSONUtil.toJsonString(jobInfo));
            jobHistory.setExceptionsJson(JSONUtil.toJsonString(exception));
            jobHistory.setCheckpointsJson(JSONUtil.toJsonString(checkPoints));
            jobHistory.setCheckpointsConfigJson(JSONUtil.toJsonString(checkPointsConfig));
            jobHistory.setConfigJson(JSONUtil.toJsonString(jobsConfig));
            if (needSave) {
                updateById(jobHistory);
                /*
                 * if (Asserts.isNotNull(getById(id))) { updateById(jobHistory); } else { save(jobHistory); }
                 */
            }
        } catch (Exception e) {
            log.error("Get flink job info failed !! historyId is {}, jobManagerHost is :{}, jobId is :{}",
                    id, jobManagerHost, jobId);
            e.printStackTrace();
        }
        return jobHistory;
    }
}
