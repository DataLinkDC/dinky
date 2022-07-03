package com.dlink.service;

import java.util.List;

import com.dlink.common.result.ProTableResult;
import com.dlink.db.service.ISuperService;
import com.dlink.explainer.lineage.LineageResult;
import com.dlink.model.JobInfoDetail;
import com.dlink.model.JobInstance;
import com.dlink.model.JobInstanceStatus;
import com.fasterxml.jackson.databind.JsonNode;

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

    JobInfoDetail refreshJobInfoDetailInfo(JobInstance jobInstance);

    LineageResult getLineage(Integer id);

    JobInstance getJobInstanceByTaskId(Integer id);

    ProTableResult<JobInstance> listJobInstances(JsonNode para);
}
