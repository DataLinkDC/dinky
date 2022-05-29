package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.JobHistory;

/**
 * JobHistoryService
 *
 * @author wenmo
 * @since 2022/3/2 19:55
 **/
public interface JobHistoryService extends ISuperService<JobHistory> {

    JobHistory getJobHistory(Integer id);

    JobHistory getJobHistoryInfo(JobHistory jobHistory);

    JobHistory refreshJobHistory(Integer id, String jobManagerHost, String jobId, boolean needSave);
}
