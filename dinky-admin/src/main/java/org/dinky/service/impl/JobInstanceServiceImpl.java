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

package org.dinky.service.impl;

import org.dinky.assertion.Asserts;
import org.dinky.context.TenantContextHolder;
import org.dinky.daemon.task.DaemonFactory;
import org.dinky.daemon.task.DaemonTaskConfig;
import org.dinky.data.dto.JobDataDto;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.enums.Status;
import org.dinky.data.model.Cluster;
import org.dinky.data.model.ClusterConfiguration;
import org.dinky.data.model.History;
import org.dinky.data.model.JobInfoDetail;
import org.dinky.data.model.JobInstance;
import org.dinky.data.model.JobInstanceCount;
import org.dinky.data.model.JobInstanceStatus;
import org.dinky.data.result.ProTableResult;
import org.dinky.explainer.lineage.LineageBuilder;
import org.dinky.explainer.lineage.LineageResult;
import org.dinky.job.FlinkJobTask;
import org.dinky.job.handler.JobRefeshHandler;
import org.dinky.mapper.JobInstanceMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.mybatis.util.ProTableUtil;
import org.dinky.service.ClusterConfigurationService;
import org.dinky.service.ClusterInstanceService;
import org.dinky.service.HistoryService;
import org.dinky.service.JobHistoryService;
import org.dinky.service.JobInstanceService;
import org.dinky.utils.JSONUtil;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * JobInstanceServiceImpl
 *
 * @since 2022/2/2 13:52
 */
@Service
@RequiredArgsConstructor
public class JobInstanceServiceImpl extends SuperServiceImpl<JobInstanceMapper, JobInstance>
        implements JobInstanceService {

    private final HistoryService historyService;
    private final ClusterInstanceService clusterInstanceService;
    private final ClusterConfigurationService clusterConfigurationService;
    private final JobHistoryService jobHistoryService;

    @Override
    public JobInstance getByIdWithoutTenant(Integer id) {
        return baseMapper.getByIdWithoutTenant(id);
    }

    @Override
    public JobInstanceStatus getStatusCount(boolean isHistory) {
        List<JobInstanceCount> jobInstanceCounts;
        if (isHistory) {
            jobInstanceCounts = baseMapper.countHistoryStatus();
        } else {
            jobInstanceCounts = baseMapper.countStatus();
        }
        JobInstanceStatus jobInstanceStatus = new JobInstanceStatus();
        int total = 0;
        for (JobInstanceCount item : jobInstanceCounts) {
            Integer counts = Asserts.isNull(item.getCounts()) ? 0 : item.getCounts();
            total += counts;
            switch (JobStatus.get(item.getStatus())) {
                case INITIALIZING:
                    jobInstanceStatus.setInitializing(counts);
                    break;
                case RUNNING:
                    jobInstanceStatus.setRunning(counts);
                    break;
                case FINISHED:
                    jobInstanceStatus.setFinished(counts);
                    break;
                case FAILED:
                case FAILING:
                    jobInstanceStatus.setFailed(counts);
                    break;
                case CANCELED:
                    jobInstanceStatus.setCanceled(counts);
                    break;
                case RESTARTING:
                    jobInstanceStatus.setRestarting(counts);
                    break;
                case CREATED:
                    jobInstanceStatus.setCreated(counts);
                    break;
                case CANCELLING:
                    jobInstanceStatus.setCancelling(counts);
                    break;
                case SUSPENDED:
                    jobInstanceStatus.setSuspended(counts);
                    break;
                case RECONCILING:
                    jobInstanceStatus.setReconciling(counts);
                    break;
                case UNKNOWN:
                    jobInstanceStatus.setUnknown(counts);
                    break;
                default:
            }
        }
        jobInstanceStatus.setAll(total);
        return jobInstanceStatus;
    }

    @Override
    public List<JobInstance> listJobInstanceActive() {
        return baseMapper.listJobInstanceActive();
    }

    @Override
    public JobInfoDetail getJobInfoDetail(Integer id) {
        return getJobInfoDetailInfo(getById(id));
    }

    @Override
    public JobInfoDetail getJobInfoDetailInfo(JobInstance jobInstance) {
        JobInfoDetail jobInfoDetail = new JobInfoDetail(jobInstance.getId());

        Asserts.checkNull(jobInstance, Status.JOB_INSTANCE_NOT_EXIST.getMessage());
        jobInfoDetail.setInstance(jobInstance);

        Cluster cluster = clusterInstanceService.getById(jobInstance.getClusterId());
        jobInfoDetail.setCluster(cluster);

        History history = historyService.getById(jobInstance.getHistoryId());
        history.setConfig(JSONUtil.parseObject(history.getConfigJson()));
        jobInfoDetail.setHistory(history);
        if (Asserts.isNotNull(history.getClusterConfigurationId())) {
            ClusterConfiguration clusterConfigById =
                    clusterConfigurationService.getClusterConfigById(history.getClusterConfigurationId());
            jobInfoDetail.setClusterConfiguration(clusterConfigById);
        }

        JobDataDto jobDataDto = jobHistoryService.getJobHistoryDto(jobInstance.getId());
        jobInfoDetail.setJobDataDto(jobDataDto);
        return jobInfoDetail;
    }

    @Override
    public JobInfoDetail refreshJobInfoDetail(Integer jobInstanceId) {
        JobInfoDetail jobInfoDetail = getJobInfoDetail(jobInstanceId);
        JobRefeshHandler.refeshJob(jobInfoDetail, true);
        DaemonFactory.addTask(DaemonTaskConfig.build(FlinkJobTask.TYPE, jobInstanceId));
        return jobInfoDetail;
    }

    @Override
    public LineageResult getLineage(Integer id) {
        History history = getJobInfoDetail(id).getHistory();
        return LineageBuilder.getColumnLineageByLogicalPlan(history.getStatement());
    }

    @Override
    public JobInstance getJobInstanceByTaskId(Integer id) {
        return baseMapper.getJobInstanceByTaskId(id);
    }

    @Override
    public ProTableResult<JobInstance> listJobInstances(JsonNode para) {
        int current = para.has("current") ? para.get("current").asInt() : 1;
        int pageSize = para.has("pageSize") ? para.get("pageSize").asInt() : 10;
        QueryWrapper<JobInstance> queryWrapper = new QueryWrapper<>();
        ProTableUtil.autoQueryDefalut(para, queryWrapper);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> param = mapper.convertValue(para, Map.class);
        Page<JobInstance> page = new Page<>(current, pageSize);
        List<JobInstance> list = baseMapper.selectForProTable(page, queryWrapper, param);
        return ProTableResult.<JobInstance>builder()
                .success(true)
                .data(list)
                .total(page.getTotal())
                .current(current)
                .pageSize(pageSize)
                .build();
    }

    @Override
    public void initTenantByJobInstanceId(Integer id) {
        Integer tenantId = baseMapper.getTenantByJobInstanceId(id);
        Asserts.checkNull(tenantId, Status.JOB_INSTANCE_NOT_EXIST.getMessage());
        TenantContextHolder.set(tenantId);
    }
}
