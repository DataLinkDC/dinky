package com.dlink.service.impl;

import com.dlink.api.FlinkAPI;
import com.dlink.assertion.Asserts;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.JobHistoryMapper;
import com.dlink.model.JobHistory;
import com.dlink.service.JobHistoryService;
import com.dlink.utils.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

/**
 * JobHistoryServiceImpl
 *
 * @author wenmo
 * @since 2022/3/2 20:00
 **/
@Service
public class JobHistoryServiceImpl extends SuperServiceImpl<JobHistoryMapper, JobHistory> implements JobHistoryService {

    @Override
    public JobHistory getJobHistory(Integer id) {
        return getJobHistoryInfo(getById(id));
    }

    @Override
    public JobHistory getJobHistoryInfo(JobHistory jobHistory) {
        if(Asserts.isNotNull(jobHistory)){
            if(Asserts.isNotNullString(jobHistory.getJobJson())){
                jobHistory.setJob(JSONUtil.parseObject(jobHistory.getJobJson()));
                jobHistory.setJobJson(null);
            }
            if(Asserts.isNotNullString(jobHistory.getExceptionsJson())){
                jobHistory.setExceptions(JSONUtil.parseObject(jobHistory.getExceptionsJson()));
                jobHistory.setExceptionsJson(null);
            }
            if(Asserts.isNotNullString(jobHistory.getCheckpointsJson())){
                jobHistory.setCheckpoints(JSONUtil.parseObject(jobHistory.getCheckpointsJson()));
                jobHistory.setCheckpointsJson(null);
            }
            if(Asserts.isNotNullString(jobHistory.getCheckpointsConfigJson())){
                jobHistory.setCheckpointsConfig(JSONUtil.parseObject(jobHistory.getCheckpointsConfigJson()));
                jobHistory.setCheckpointsConfigJson(null);
            }
            if(Asserts.isNotNullString(jobHistory.getConfigJson())){
                jobHistory.setConfig(JSONUtil.parseObject(jobHistory.getConfigJson()));
                jobHistory.setConfigJson(null);
            }
            if(Asserts.isNotNullString(jobHistory.getJarJson())){
                jobHistory.setJar(JSONUtil.parseObject(jobHistory.getJarJson()));
                jobHistory.setJarJson(null);
            }
            if(Asserts.isNotNullString(jobHistory.getClusterJson())){
                jobHistory.setCluster(JSONUtil.parseObject(jobHistory.getClusterJson()));
                jobHistory.setClusterJson(null);
            }
            if(Asserts.isNotNullString(jobHistory.getClusterConfigurationJson())){
                jobHistory.setClusterConfiguration(JSONUtil.parseObject(jobHistory.getClusterConfigurationJson()));
                jobHistory.setClusterConfigurationJson(null);
            }
        }
        return jobHistory;
    }

    @Override
    public JobHistory refreshJobHistory(Integer id, String jobManagerHost, String jobId) {
        JobHistory jobHistory = new JobHistory();
        jobHistory.setId(id);
        try {
            JsonNode jobInfo = FlinkAPI.build(jobManagerHost).getJobInfo(jobId);
            JsonNode exception = FlinkAPI.build(jobManagerHost).getException(jobId);
            JsonNode checkPoints = FlinkAPI.build(jobManagerHost).getCheckPoints(jobId);
            JsonNode checkPointsConfig = FlinkAPI.build(jobManagerHost).getCheckPointsConfig(jobId);
            JsonNode jobsConfig = FlinkAPI.build(jobManagerHost).getJobsConfig(jobId);
            jobHistory.setJobJson(JSONUtil.toJsonString(jobInfo));
            jobHistory.setExceptionsJson(JSONUtil.toJsonString(exception));
            jobHistory.setCheckpointsJson(JSONUtil.toJsonString(checkPoints));
            jobHistory.setCheckpointsConfigJson(JSONUtil.toJsonString(checkPointsConfig));
            jobHistory.setConfigJson(JSONUtil.toJsonString(jobsConfig));
            if (Asserts.isNotNull(getById(id))) {
                updateById(jobHistory);
            } else {
                save(jobHistory);
            }
        }catch (Exception e){
        }finally {
            return jobHistory;
        }
    }
}
