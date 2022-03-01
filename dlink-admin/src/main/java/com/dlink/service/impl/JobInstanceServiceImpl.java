package com.dlink.service.impl;

import com.dlink.assertion.Asserts;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.JobInstanceMapper;
import com.dlink.model.JobInstance;
import com.dlink.model.JobInstanceCount;
import com.dlink.model.JobInstanceStatus;
import com.dlink.model.JobStatus;
import com.dlink.service.JobInstanceService;
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
    @Override
    public JobInstanceStatus getStatusCount() {
        List<JobInstanceCount> jobInstanceCounts = baseMapper.countStatus();
        JobInstanceStatus jobInstanceStatus = new JobInstanceStatus();
        Integer total = 0;
        for (JobInstanceCount item : jobInstanceCounts) {
            Integer counts = Asserts.isNull(item.getCounts())?0:item.getCounts();
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
}
