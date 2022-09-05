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

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * JobHistory
 *
 * @author wenmo
 * @since 2022/3/2 19:48
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_job_history")
public class JobHistory implements Serializable {

    private static final long serialVersionUID = 4984787372340047250L;

    private Integer id;

    private Integer tenantId;

    @TableField(exist = false)
    private ObjectNode job;

    private String jobJson;

    @TableField(exist = false)
    private ObjectNode exceptions;

    private String exceptionsJson;

    @TableField(exist = false)
    private ObjectNode checkpoints;

    private String checkpointsJson;

    @TableField(exist = false)
    private ObjectNode checkpointsConfig;

    private String checkpointsConfigJson;

    @TableField(exist = false)
    private ObjectNode config;

    private String configJson;

    @TableField(exist = false)
    private ObjectNode jar;

    private String jarJson;

    @TableField(exist = false)
    private ObjectNode cluster;

    private String clusterJson;

    @TableField(exist = false)
    private ObjectNode clusterConfiguration;

    private String clusterConfigurationJson;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private boolean error;
}
