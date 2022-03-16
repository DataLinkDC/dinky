package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkRestResultConstant;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.explainer.ca.CABuilder;
import com.dlink.explainer.ca.TableCANode;
import com.dlink.explainer.lineage.LineageBuilder;
import com.dlink.explainer.lineage.LineageResult;
import com.dlink.mapper.JobInstanceMapper;
import com.dlink.model.Cluster;
import com.dlink.model.History;
import com.dlink.model.JobHistory;
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
import com.dlink.service.TaskService;
import com.dlink.utils.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * JobInstanceServiceImpl
 *
 * @author wenmo
 * @since 2022/2/2 13:52
 */
@Service
public class JobInstanceServiceImpl extends SuperServiceImpl<JobInstanceMapper, JobInstance> implements JobInstanceService {

    @Autowired
    private HistoryService historyService;
    @Autowired
    private ClusterService clusterService;
    @Autowired
    private ClusterConfigurationService clusterConfigurationService;
    @Autowired
    private JobHistoryService jobHistoryService;

    @Override
    public JobInstanceStatus getStatusCount(boolean isHistory) {
        List<JobInstanceCount> jobInstanceCounts = null;
        if(isHistory){
            jobInstanceCounts = baseMapper.countHistoryStatus();
        }else{
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
        JobInfoDetail jobInfoDetail = new JobInfoDetail(jobInstance.getId());
        jobInfoDetail.setInstance(jobInstance);
        jobInfoDetail.setCluster(clusterService.getById(jobInstance.getClusterId()));
        jobInfoDetail.setJobHistory(jobHistoryService.getJobHistory(jobInstance.getId()));
        History history = historyService.getById(jobInstance.getHistoryId());
        history.setConfig(JSONUtil.parseObject(history.getConfigJson()));
        jobInfoDetail.setHistory(history);
        if (Asserts.isNotNull(history) && Asserts.isNotNull(history.getClusterConfigurationId())) {
            jobInfoDetail.setClusterConfiguration(clusterConfigurationService.getClusterConfigById(history.getClusterConfigurationId()));
        }
        return jobInfoDetail;
    }

    @Override
    public LineageResult getLineage(Integer id) {
        return LineageBuilder.getLineage(getJobInfoDetail(id).getHistory().getStatement());
    }

}
