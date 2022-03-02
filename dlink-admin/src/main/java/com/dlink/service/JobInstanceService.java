package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.JobInfoDetail;
import com.dlink.model.JobInstance;
import com.dlink.model.JobInstanceStatus;

/**
 * JobInstanceService
 *
 * @author wenmo
 * @since 2022/2/2 13:52
 */
public interface JobInstanceService extends ISuperService<JobInstance> {

    JobInstanceStatus getStatusCount();

    JobInfoDetail getJobInfoDetail(Integer id);

    JobInfoDetail getJobInfoDetailInfo(JobInstance jobInstance);

    JobInstance refreshJobInstance(Integer id);

    JobInfoDetail refreshJobInfoDetail(Integer id);
}
