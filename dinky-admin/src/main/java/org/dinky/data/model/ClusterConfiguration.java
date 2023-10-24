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

import org.dinky.data.typehandler.JSONObjectHandler;
import org.dinky.gateway.model.FlinkClusterConfig;
import org.dinky.mybatis.model.SuperEntity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ClusterConfig
 *
 * @since 2021/11/6
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_cluster_configuration")
@ApiModel(value = "ClusterConfiguration", description = "if your cluster type is yarn ,the record is there")
public class ClusterConfiguration extends SuperEntity {

    private static final long serialVersionUID = 5830130188542066241L;

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
    @TableField(typeHandler = JSONObjectHandler.class)
    private FlinkClusterConfig configJson;

    @ApiModelProperty(
            value = "isAvailable",
            required = true,
            dataType = "Boolean",
            example = "true",
            notes = "cluster is available, 0: not available, 1: available")
    private Boolean isAvailable;

    @ApiModelProperty(value = "note", required = true, dataType = "String", example = "test", notes = "cluster note")
    private String note;
}
