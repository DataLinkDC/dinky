package com.dlink.service.impl;

import com.dlink.assertion.Asserts;
import com.dlink.db.service.impl.SuperServiceImpl;
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
import com.dlink.service.JobInstanceService;
import com.dlink.service.TaskService;
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
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ClusterService clusterService;
    @Autowired
    private ClusterConfigurationService clusterConfigurationService;

    @Override
    public JobInstanceStatus getStatusCount() {
        List<JobInstanceCount> jobInstanceCounts = baseMapper.countStatus();
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
            }
        }
        jobInstanceStatus.setAll(total);
        return jobInstanceStatus;
    }

    @Override
    public JobInfoDetail getJobInfoDetail(Integer id) {
        JobInfoDetail jobInfoDetail = new JobInfoDetail(id);
        JobInstance jobInstance = getById(id);
        Asserts.checkNull(jobInstance, "该任务实例不存在");
        jobInfoDetail.setInstance(jobInstance);
        jobInfoDetail.setTask(taskService.getTaskInfoById(jobInstance.getTaskId()));
        jobInfoDetail.setCluster(clusterService.getById(jobInstance.getClusterId()));
        History history = historyService.getById(jobInstance.getHistoryId());
        jobInfoDetail.setHistory(history);
        if (Asserts.isNotNull(history) && Asserts.isNotNull(history.getClusterConfigurationId())) {
            jobInfoDetail.setClusterConfiguration(clusterConfigurationService.getClusterConfigById(history.getClusterConfigurationId()));
        }
        return jobInfoDetail;
    }
}
