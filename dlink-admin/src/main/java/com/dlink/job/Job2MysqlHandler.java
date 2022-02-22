package com.dlink.job;

import cn.hutool.json.JSONUtil;
import com.dlink.assertion.Asserts;
import com.dlink.context.SpringContextUtils;
import com.dlink.model.Cluster;
import com.dlink.model.History;
import com.dlink.model.JobInstance;
import com.dlink.model.JobStatus;
import com.dlink.service.ClusterService;
import com.dlink.service.HistoryService;
import com.dlink.service.JobInstanceService;
import org.springframework.context.annotation.DependsOn;

import java.time.LocalDateTime;

/**
 * Job2MysqlHandler
 *
 * @author wenmo
 * @since 2021/6/27 0:04
 */
@DependsOn("springContextUtils")
public class Job2MysqlHandler implements JobHandler {

    private static HistoryService historyService;
    private static ClusterService clusterService;
    private static JobInstanceService jobInstanceService;

    static {
        historyService = SpringContextUtils.getBean("historyServiceImpl", HistoryService.class);
        clusterService = SpringContextUtils.getBean("clusterServiceImpl", ClusterService.class);
        jobInstanceService = SpringContextUtils.getBean("jobInstanceServiceImpl", JobInstanceService.class);
    }

    @Override
    public boolean init() {
        Job job = JobContextHolder.getJob();
        History history = new History();
        history.setType(job.getType().getLongValue());
        if (job.isUseGateway()) {
            history.setClusterConfigurationId(job.getJobConfig().getClusterConfigurationId());
        } else {
            history.setClusterId(job.getJobConfig().getClusterId());
        }
        history.setJobManagerAddress(job.getJobManagerAddress());
        history.setJobName(job.getJobConfig().getJobName());
        history.setSession(job.getJobConfig().getSession());
        history.setStatus(job.getStatus().ordinal());
        history.setStatement(job.getStatement());
        history.setStartTime(job.getStartTime());
        history.setTaskId(job.getJobConfig().getTaskId());
        history.setConfig(JSONUtil.toJsonStr(job.getJobConfig()));
        historyService.save(history);
        job.setId(history.getId());
        return true;
    }

    @Override
    public boolean ready() {
        return true;
    }

    @Override
    public boolean running() {
        return true;
    }

    @Override
    public boolean success() {
        Job job = JobContextHolder.getJob();
        History history = new History();
        history.setId(job.getId());
        if (job.isUseGateway() && Asserts.isNullString(job.getJobId())) {
            job.setJobId("unknown-" + LocalDateTime.now().toString());
        }
        history.setJobId(job.getJobId());
        history.setStatus(job.getStatus().ordinal());
        history.setEndTime(job.getEndTime());
        if (job.isUseGateway()) {
            history.setJobManagerAddress(job.getJobManagerAddress());
        }
        Integer clusterId = job.getJobConfig().getClusterId();
        if (job.isUseGateway()) {
            Cluster cluster = clusterService.registersCluster(Cluster.autoRegistersCluster(job.getJobManagerAddress(),
                    job.getJobId(), job.getJobConfig().getJobName() + LocalDateTime.now(), job.getType().getLongValue(),
                    job.getJobConfig().getClusterConfigurationId(), job.getJobConfig().getTaskId()));
            if (Asserts.isNotNull(cluster)) {
                clusterId = cluster.getId();
            }
        }
        history.setClusterId(clusterId);
        historyService.updateById(history);
        if (Asserts.isNotNullCollection(job.getJids())) {
            for (String jid : job.getJids()) {
                JobInstance jobInstance = history.buildJobInstance();
                jobInstance.setHistoryId(job.getId());
                jobInstance.setClusterId(clusterId);
                jobInstance.setTaskId(job.getJobConfig().getTaskId());
                jobInstance.setName(job.getJobConfig().getJobName());
                jobInstance.setJid(jid);
                jobInstance.setStatus(JobStatus.INITIALIZING.getValue());
                jobInstanceService.save(jobInstance);
            }
        }
        return true;
    }

    @Override
    public boolean failed() {
        Job job = JobContextHolder.getJob();
        History history = new History();
        history.setId(job.getId());
        history.setJobId(job.getJobId());
        history.setStatus(job.getStatus().ordinal());
        history.setJobManagerAddress(job.getJobManagerAddress());
        history.setEndTime(job.getEndTime());
        history.setError(job.getError());
        historyService.updateById(history);
        return true;
    }

    @Override
    public boolean callback() {
        return true;
    }

    @Override
    public boolean close() {
        return true;
    }
}
