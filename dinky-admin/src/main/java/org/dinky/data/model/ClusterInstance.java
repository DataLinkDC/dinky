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

import org.dinky.mybatis.model.SuperEntity;

import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ClusterInstance
 *
 * @since 2021/5/28 13:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_cluster")
@ApiModel(value = "ClusterInstance", description = "ClusterInstance")
public class ClusterInstance extends SuperEntity<ClusterInstance> {

    private static final long serialVersionUID = 3104721227014487321L;

    @ApiModelProperty(value = "name", required = true, dataType = "String", example = "test")
    private Integer tenantId;

    @ApiModelProperty(
            value = "alias",
            required = true,
            dataType = "String",
            example = "test",
            notes = "cluster alias, if this is auto register, it will be has value, and can not modify it")
    private String alias;

    @ApiModelProperty(
            value = "type",
            required = true,
            dataType = "String",
            example = "test",
            notes = "cluster type, such as: standalone ,yarn-session")
    private String type;

    @ApiModelProperty(value = "hosts", required = true, dataType = "String", example = "test", notes = "cluster hosts")
    private String hosts;

    @ApiModelProperty(
            value = "jobManagerHost",
            required = true,
            dataType = "String",
            example = "test",
            notes = "job manager host")
    private String jobManagerHost;

    @ApiModelProperty(
            value = "version",
            required = true,
            dataType = "String",
            example = "test",
            notes = "Flink cluster version")
    private String version;

    @ApiModelProperty(
            value = "status",
            required = true,
            dataType = "Integer",
            example = "test",
            notes = "0:unavailable, 1:available")
    private Integer status;

    @ApiModelProperty(value = "note", dataType = "String", example = "test")
    private String note;

    @ApiModelProperty(
            value = "autoRegisters",
            required = true,
            dataType = "Boolean",
            example = "test",
            notes = "is auto registers, if this record from projob/application mode , it will be true")
    private Boolean autoRegisters;

    @ApiModelProperty(
            value = "clusterConfigurationId",
            required = true,
            dataType = "Integer",
            example = "test",
            notes = "cluster configuration id")
    private Integer clusterConfigurationId;

    @ApiModelProperty(value = "taskId", required = true, dataType = "Integer", example = "test", notes = "task id")
    private Integer taskId;
}
