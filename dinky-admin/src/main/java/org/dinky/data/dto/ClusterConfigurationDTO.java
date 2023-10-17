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

import org.dinky.data.model.ClusterConfiguration;
import org.dinky.gateway.model.FlinkClusterConfig;
import org.dinky.mybatis.annotation.Save;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ClusterConfigurationDTO {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "ID", required = true, dataType = "Integer", example = "1", notes = "Primary Key")
    private Integer id;

    @NotNull(
            message = "Name cannot be null",
            groups = {Save.class})
    @ApiModelProperty(value = "Name", required = true, dataType = "String", example = "Name")
    private String name;

    @NotNull(
            message = "Enabled cannot be null",
            groups = {Save.class})
    @ApiModelProperty(value = "Enabled", required = true, dataType = "Boolean", example = "true")
    private Boolean enabled;

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
    private FlinkClusterConfig config;

    @ApiModelProperty(
            value = "isAvailable",
            required = true,
            dataType = "Boolean",
            example = "true",
            notes = "cluster is available, 0: not available, 1: available")
    private Boolean isAvailable;

    @ApiModelProperty(value = "note", required = true, dataType = "String", example = "test", notes = "cluster note")
    private String note;

    public static ClusterConfigurationDTO fromBean(ClusterConfiguration bean) {
        ClusterConfigurationDTO clusterConfigurationDTO = new ClusterConfigurationDTO();
        BeanUtil.copyProperties(bean, clusterConfigurationDTO);
        clusterConfigurationDTO.setConfig(FlinkClusterConfig.create(bean.getType(), bean.getConfigJson()));
        return clusterConfigurationDTO;
    }

    public ClusterConfiguration toBean() {
        ClusterConfiguration clusterConfiguration = new ClusterConfiguration();
        BeanUtil.copyProperties(this, clusterConfiguration);
        clusterConfiguration.setConfigJson(this.getConfig());
        return clusterConfiguration;
    }
}
