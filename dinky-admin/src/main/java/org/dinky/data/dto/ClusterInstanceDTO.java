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

package org.dinky.data.dto;

import org.dinky.data.model.ClusterInstance;
import org.dinky.mybatis.annotation.Save;

import javax.validation.constraints.NotNull;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClusterInstanceDTO
 *
 * @since 2023/10/23 10:18
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "ClusterInstanceDTO", description = "API Cluster Instance Data Transfer Object")
@Builder
public class ClusterInstanceDTO {

    @ApiModelProperty(value = "id", required = true, dataType = "Integer", example = "id")
    private Integer id;

    @ApiModelProperty(value = "Name", required = true, dataType = "String", example = "Name")
    private String name;

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

    @NotNull(
            message = "Enabled cannot be null",
            groups = {Save.class})
    @ApiModelProperty(value = "Enabled", required = true, dataType = "Boolean", example = "true")
    private Boolean enabled;

    public ClusterInstance toBean() {
        ClusterInstance clusterInstance = new ClusterInstance();
        BeanUtil.copyProperties(this, clusterInstance);
        return clusterInstance;
    }
}
