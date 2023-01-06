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

import com.dlink.assertion.Asserts;
import com.dlink.assertion.Tips;
import com.dlink.common.result.ProTableResult;
import com.dlink.context.TenantContextHolder;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.db.util.ProTableUtil;
import com.dlink.explainer.lineage.LineageBuilder;
import com.dlink.explainer.lineage.LineageResult;
import com.dlink.job.FlinkJobTaskPool;
import com.dlink.mapper.JobInstanceMapper;
import com.dlink.model.History;
import com.dlink.model.JobInfoDetail;
import com.dlink.model.JobInstance;
import com.dlink.model.JobInstanceCount;
import com.dlink.model.JobInstanceStatus;
import com.dlink.model.JobStatus;
import com.dlink.service.ClusterConfigurationService;
import com.dlink.service.ClusterService;
import com.dlink.service.HistoryService;
import com.dlink.service.JobHistoryService;
import com.dlink.service.JobInstanceService;
import com.dlink.utils.JSONUtil;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JobInstanceServiceImpl
 *
 * @author wenmo
 * @since 2022/2/2 13:52
 */
@Service
public class JobInstanceServiceImpl extends SuperServiceImpl<JobInstanceMapper, JobInstance>
        implements
            JobInstanceService {

    @Autowired
    private HistoryService historyService;
    @Autowired
    private ClusterService clusterService;
    @Autowired
    private ClusterConfigurationService clusterConfigurationService;
    @Autowired
    private JobHistoryService jobHistoryService;

    @Override
    public JobInstance getByIdWithoutTenant(Integer id) {
        return baseMapper.getByIdWithoutTenant(id);
    }

    @Override
    public JobInstanceStatus getStatusCount(boolean isHistory) {
        List<JobInstanceCount> jobInstanceCounts = null;
        if (isHistory) {
            jobInstanceCounts = baseMapper.countHistoryStatus();
        } else {
            jobInstanceCounts = baseMapper.countStatus();
        }
        JobInstanceStatus jobInstanceStatus = new JobInstanceStatus();
        Integer total = 0;
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
                case FAILING:
                    jobInstanceStatus.setFailed(counts);
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
        Asserts.checkNull(jobInstance, "该任务实例不存在");
        String key = jobInstance.getId().toString();
        FlinkJobTaskPool pool = FlinkJobTaskPool.getInstance();
        if (pool.exist(key)) {
            return pool.get(key);
        } else {
            JobInfoDetail jobInfoDetail = new JobInfoDetail(jobInstance.getId());
            jobInfoDetail.setInstance(jobInstance);
            jobInfoDetail.setCluster(clusterService.getById(jobInstance.getClusterId()));
            jobInfoDetail.setJobHistory(jobHistoryService.getJobHistory(jobInstance.getId()));
            History history = historyService.getById(jobInstance.getHistoryId());
            history.setConfig(JSONUtil.parseObject(history.getConfigJson()));
            jobInfoDetail.setHistory(history);
            if (Asserts.isNotNull(history.getClusterConfigurationId())) {
                jobInfoDetail.setClusterConfiguration(
                        clusterConfigurationService.getClusterConfigById(history.getClusterConfigurationId()));
            }
            return jobInfoDetail;
        }
    }

    @Override
    public JobInfoDetail refreshJobInfoDetailInfo(JobInstance jobInstance) {
        Asserts.checkNull(jobInstance, "该任务实例不存在");
        JobInfoDetail jobInfoDetail;
        FlinkJobTaskPool pool = FlinkJobTaskPool.getInstance();
        String key = jobInstance.getId().toString();

        jobInfoDetail = new JobInfoDetail(jobInstance.getId());
        jobInfoDetail.setInstance(jobInstance);
        jobInfoDetail.setCluster(clusterService.getById(jobInstance.getClusterId()));
        jobInfoDetail.setJobHistory(jobHistoryService.getJobHistory(jobInstance.getId()));
        History history = historyService.getById(jobInstance.getHistoryId());
        history.setConfig(JSONUtil.parseObject(history.getConfigJson()));
        jobInfoDetail.setHistory(history);
        if (Asserts.isNotNull(history) && Asserts.isNotNull(history.getClusterConfigurationId())) {
            jobInfoDetail.setClusterConfiguration(
                    clusterConfigurationService.getClusterConfigById(history.getClusterConfigurationId()));
        }
        if (pool.exist(key)) {
            pool.refresh(jobInfoDetail);
        } else {
            pool.push(key, jobInfoDetail);
        }
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
        Integer current = para.has("current") ? para.get("current").asInt() : 1;
        Integer pageSize = para.has("pageSize") ? para.get("pageSize").asInt() : 10;
        QueryWrapper<JobInstance> queryWrapper = new QueryWrapper<>();
        ProTableUtil.autoQueryDefalut(para, queryWrapper);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> param = mapper.convertValue(para, Map.class);
        Page<JobInstance> page = new Page<>(current, pageSize);
        List<JobInstance> list = baseMapper.selectForProTable(page, queryWrapper, param);
        FlinkJobTaskPool pool = FlinkJobTaskPool.getInstance();
        for (int i = 0; i < list.size(); i++) {
            if (pool.exist(list.get(i).getId().toString())) {
                list.get(i).setStatus(pool.get(list.get(i).getId().toString()).getInstance().getStatus());
                list.get(i).setUpdateTime(pool.get(list.get(i).getId().toString()).getInstance().getUpdateTime());
                list.get(i).setFinishTime(pool.get(list.get(i).getId().toString()).getInstance().getFinishTime());
                list.get(i).setError(pool.get(list.get(i).getId().toString()).getInstance().getError());
                list.get(i).setDuration(pool.get(list.get(i).getId().toString()).getInstance().getDuration());
            }
        }
        return ProTableResult.<JobInstance>builder().success(true).data(list).total(page.getTotal()).current(current)
                .pageSize(pageSize).build();
    }

    @Override
    public void initTenantByJobInstanceId(Integer id) {
        Integer tenantId = baseMapper.getTenantByJobInstanceId(id);
        Asserts.checkNull(tenantId, Tips.JOB_INSTANCE_NOT_EXIST);
        TenantContextHolder.set(tenantId);
    }

}
