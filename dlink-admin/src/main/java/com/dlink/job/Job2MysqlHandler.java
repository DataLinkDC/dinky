package com.dlink.job;

import com.dlink.assertion.Asserts;
import com.dlink.context.SpringContextUtils;
import com.dlink.daemon.task.DaemonFactory;
import com.dlink.daemon.task.DaemonTaskConfig;
import com.dlink.model.*;
import com.dlink.service.*;
import com.dlink.utils.JSONUtil;
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
    private static ClusterConfigurationService clusterConfigurationService;
    private static JarService jarService;
    private static JobInstanceService jobInstanceService;
    private static JobHistoryService jobHistoryService;
    private static TaskService taskService;

    static {
        historyService = SpringContextUtils.getBean("historyServiceImpl", HistoryService.class);
        clusterService = SpringContextUtils.getBean("clusterServiceImpl", ClusterService.class);
        clusterConfigurationService = SpringContextUtils.getBean("clusterConfigurationServiceImpl", ClusterConfigurationService.class);
        jarService = SpringContextUtils.getBean("jarServiceImpl", JarService.class);
        jobInstanceService = SpringContextUtils.getBean("jobInstanceServiceImpl", JobInstanceService.class);
        jobHistoryService = SpringContextUtils.getBean("jobHistoryServiceImpl", JobHistoryService.class);
        taskService = SpringContextUtils.getBean("taskServiceImpl", TaskService.class);
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
        history.setConfigJson(JSONUtil.toJsonString(job.getJobConfig()));
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
        Cluster cluster = null;
        if (job.isUseGateway()) {
            cluster = clusterService.registersCluster(Cluster.autoRegistersCluster(job.getJobManagerAddress(),
                    job.getJobId(), job.getJobConfig().getJobName() + LocalDateTime.now(), job.getType().getLongValue(),
                    job.getJobConfig().getClusterConfigurationId(), job.getJobConfig().getTaskId()));
            if (Asserts.isNotNull(cluster)) {
                clusterId = cluster.getId();
            }
        } else {
            cluster = clusterService.getById(clusterId);
        }
        history.setClusterId(clusterId);
        historyService.updateById(history);
        ClusterConfiguration clusterConfiguration = null;
        if (Asserts.isNotNull(job.getJobConfig().getClusterConfigurationId())) {
            clusterConfiguration = clusterConfigurationService.getClusterConfigById(job.getJobConfig().getClusterConfigurationId());
        }
        Jar jar = null;
        if (Asserts.isNotNull(job.getJobConfig().getJarId())) {
            jar = jarService.getById(job.getJobConfig().getJarId());
        }
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
                Task task = new Task();
                task.setId(jobInstance.getTaskId());
                task.setJobInstanceId(jobInstance.getId());
                taskService.updateById(task);
                JobHistory jobHistory = new JobHistory();
                jobHistory.setId(jobInstance.getId());
                jobHistory.setJarJson(JSONUtil.toJsonString(jar));
                jobHistory.setClusterJson(JSONUtil.toJsonString(cluster));
                jobHistory.setClusterConfigurationJson(JSONUtil.toJsonString(clusterConfiguration));
                jobHistoryService.save(jobHistory);
                DaemonFactory.addTask(DaemonTaskConfig.build(FlinkJobTask.TYPE, jobInstance.getId()));
                break;
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
