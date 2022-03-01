package com.dlink.model;

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
    private Task task;
    private History history;

    public JobInfoDetail(Integer id) {
        this.id = id;
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

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }
}
