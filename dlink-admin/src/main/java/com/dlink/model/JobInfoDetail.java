package com.dlink.model;

import java.util.Set;

/**
 * JobInfoDetail
 *
 * @author wenmo
 * @since 2022/3/1 19:31
 **/
public class JobInfoDetail {

    private Integer id;
    private JobInstance instance;
    private Cluster cluster;
    private ClusterConfiguration clusterConfiguration;
    private History history;
    private JobHistory jobHistory;
    private JobManagerConfiguration jobManagerConfiguration;
    private Set<TaskManagerConfiguration> taskManagerConfiguration;
    private Integer refreshCount;

    public JobInfoDetail(Integer id) {
        this.id = id;
        this.refreshCount = 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JobInstance getInstance() {
        return instance;
    }

    public void setInstance(JobInstance instance) {
        this.instance = instance;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public ClusterConfiguration getClusterConfiguration() {
        return clusterConfiguration;
    }

    public void setClusterConfiguration(ClusterConfiguration clusterConfiguration) {
        this.clusterConfiguration = clusterConfiguration;
    }

    public void setJobManagerConfiguration(JobManagerConfiguration jobMangerConfiguration) {
        this.jobManagerConfiguration = jobMangerConfiguration;
    }
    public JobManagerConfiguration getJobManagerConfiguration() {
        return jobManagerConfiguration;
    }


    public void setTaskManagerConfiguration(Set<TaskManagerConfiguration> taskManagerConfiguration) {
        this.taskManagerConfiguration = taskManagerConfiguration;
    }
    public Set<TaskManagerConfiguration> getTaskManagerConfiguration() {
        return taskManagerConfiguration;
    }


    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public JobHistory getJobHistory() {
        return jobHistory;
    }

    public void setJobHistory(JobHistory jobHistory) {
        this.jobHistory = jobHistory;
    }

    public void refresh() {
        refreshCount = refreshCount + 1;
        if (isNeedSave()) {
            refreshCount = 0;
        }
    }

    public boolean isNeedSave() {
        return refreshCount % 60 == 0;
    }
}
