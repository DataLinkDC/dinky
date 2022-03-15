package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.JobInfoDetail;
import com.dlink.model.JobInstance;
import com.dlink.model.JobInstanceStatus;

import java.util.List;

/**
 * JobInstanceService
 *
 * @author wenmo
 * @since 2022/2/2 13:52
 */
public interface JobInstanceService extends ISuperService<JobInstance> {

    JobInstanceStatus getStatusCount(boolean isHistory);

    List<JobInstance> listJobInstanceActive();

    JobInfoDetail getJobInfoDetail(Integer id);

    JobInfoDetail getJobInfoDetailInfo(JobInstance jobInstance);

}
