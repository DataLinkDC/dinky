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

package org.dinky.data.model.ext;

import org.dinky.data.dto.ClusterConfigurationDTO;
import org.dinky.data.dto.JobDataDto;
import org.dinky.data.model.ClusterInstance;
import org.dinky.data.model.job.History;
import org.dinky.data.model.job.JobInstance;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * JobInfoDetail
 *
 * @since 2022/3/1 19:31
 */
@Data
@ApiModel(value = "JobInfoDetail", description = "Job Information Detail")
public class JobInfoDetail implements Serializable {
    private static final long serialVersionUID = -5372368793146991172L;

    @ApiModelProperty(value = "ID", dataType = "Integer", example = "1", notes = "Unique identifier for the job")
    private Integer id;

    @ApiModelProperty(value = "Job Instance", notes = "Details about the job instance")
    private JobInstance instance;

    @ApiModelProperty(value = "ClusterInstance", notes = "Details about the cluster Instance")
    private ClusterInstance clusterInstance;

    @ApiModelProperty(value = "Cluster Configuration", notes = "Details about the cluster configuration")
    private ClusterConfigurationDTO clusterConfiguration;

    @ApiModelProperty(value = "History", notes = "Details about the history")
    private History history;

    @ApiModelProperty(value = "JobDataDto", notes = "Details about the job")
    private JobDataDto jobDataDto;

    public JobInfoDetail(Integer id) {
        this.id = id;
    }
}
