/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.data.model;

import java.util.Set;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * JobInfoDetail
 *
 * @since 2022/3/1 19:31
 */
@ApiModel(value = "JobInfoDetail", description = "Job Information Detail")
public class JobInfoDetail {

    @ApiModelProperty(value = "ID", dataType = "Integer", example = "1", notes = "Unique identifier for the job")
    private Integer id;

    @ApiModelProperty(value = "Job Instance", notes = "Details about the job instance")
    private JobInstance instance;

    @ApiModelProperty(value = "Cluster", notes = "Details about the cluster")
    private Cluster cluster;

    @ApiModelProperty(value = "Cluster Configuration", notes = "Details about the cluster configuration")
    private ClusterConfiguration clusterConfiguration;

    @ApiModelProperty(value = "History", notes = "Details about the history")
    private History history;

    @ApiModelProperty(value = "Job History", notes = "Details about the job history")
    private JobHistory jobHistory;

    @ApiModelProperty(value = "Job Manager Configuration", notes = "Details about the job manager configuration")
    private JobManagerConfiguration jobManagerConfiguration;

    @ApiModelProperty(value = "Task Manager Configurations", notes = "Set of task manager configurations")
    private Set<TaskManagerConfiguration> taskManagerConfiguration;

    @ApiModelProperty(
            value = "Refresh Count",
            dataType = "Integer",
            example = "5",
            notes = "Count of refresh operations")
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
