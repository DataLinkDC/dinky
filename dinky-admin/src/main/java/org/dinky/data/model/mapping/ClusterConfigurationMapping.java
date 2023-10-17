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

package org.dinky.data.model.mapping;

import org.dinky.data.model.ClusterConfiguration;

import java.time.LocalDateTime;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClusterConfigurationMapping {

    /** 主键ID */
    @ApiModelProperty(value = "ID", required = true, dataType = "Integer", example = "1", notes = "Primary Key")
    private Integer id;

    @ApiModelProperty(value = "Name", required = true, dataType = "String", example = "Name")
    private String name;

    @ApiModelProperty(value = "Enabled", required = true, dataType = "Boolean", example = "true")
    private Boolean enabled;

    @ApiModelProperty(
            value = "Create Time",
            required = true,
            dataType = "LocalDateTime",
            example = "2021-05-28 00:00:00")
    private LocalDateTime createTime;

    @ApiModelProperty(
            value = "Update Time",
            required = true,
            dataType = "LocalDateTime",
            example = "2021-05-28 00:00:00")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "tenantId", required = true, dataType = "String", example = "1", notes = "the Tenant Id")
    private Integer tenantId;

    @ApiModelProperty(
            value = "type",
            required = true,
            dataType = "String",
            example = "test",
            notes = "cluster type, such as: yarn ,k8s-native ,k8s-session")
    private String type;

    @ApiModelProperty(
            value = "configJson",
            required = true,
            dataType = "String",
            example = "test",
            notes = "cluster config json")
    private String configJson;

    @ApiModelProperty(
            value = "isAvailable",
            required = true,
            dataType = "Boolean",
            example = "true",
            notes = "cluster is available, 0: not available, 1: available")
    private Boolean isAvailable;

    @ApiModelProperty(value = "note", required = true, dataType = "String", example = "test", notes = "cluster note")
    private String note;

    public static ClusterConfigurationMapping getClusterConfigurationMapping(ClusterConfiguration configuration) {
        ClusterConfigurationMapping clusterConfigurationMapping = new ClusterConfigurationMapping();
        BeanUtil.copyProperties(configuration, clusterConfigurationMapping);
        return clusterConfigurationMapping;
    }
}
