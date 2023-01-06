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

package com.dlink.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * History
 *
 * @author wenmo
 * @since 2021/6/26 22:48
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_history")
public class History implements Serializable {

    private static final long serialVersionUID = 4058280957630503072L;

    private Integer id;

    private Integer tenantId;

    private Integer clusterId;

    private Integer clusterConfigurationId;

    private String session;

    private String jobId;

    private String jobName;

    private String jobManagerAddress;

    private Integer status;

    private String statement;

    private String type;

    private String error;

    private String result;

    @TableField(exist = false)
    private ObjectNode config;

    private String configJson;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer taskId;

    @TableField(exist = false)
    private String statusText;

    @TableField(exist = false)
    private String clusterAlias;

    @TableField(exist = false)
    private String taskAlias;

    public JobInstance buildJobInstance() {
        JobInstance jobInstance = new JobInstance();
        jobInstance.setHistoryId(id);
        jobInstance.setClusterId(clusterId);
        jobInstance.setTaskId(taskId);
        jobInstance.setName(jobName);
        return jobInstance;
    }
}
