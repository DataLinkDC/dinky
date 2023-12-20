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

import org.dinky.data.model.DataBase;
import org.dinky.mybatis.model.SuperEntity;

import java.time.LocalDateTime;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DataBaseDTO
 *
 * @since 2023/10/23 10:59
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "DataBaseDTO", description = "API Database Data Transfer Object")
public class DataBaseDTO extends SuperEntity {

    @ApiModelProperty(value = "tenantId", required = true, dataType = "Integer", example = "1")
    private Integer tenantId;

    @ApiModelProperty(value = "groupName", required = true, dataType = "String", example = "source")
    private String groupName;

    @ApiModelProperty(value = "type", required = true, dataType = "String", example = "MySQL")
    private String type;

    @ApiModelProperty(value = "connectConfig", required = true, dataType = "String", example = "{}")
    private Map<String, Object> connectConfig;

    @ApiModelProperty(value = "note", dataType = "String", example = "note")
    private String note;

    @ApiModelProperty(value = "flinkConfig", dataType = "String", example = "flinkConfig")
    private String flinkConfig;

    @ApiModelProperty(value = "flinkTemplate", dataType = "String", example = "flinkTemplate")
    private String flinkTemplate;

    @ApiModelProperty(value = "dbVersion", dataType = "String", example = "dbVersion")
    private String dbVersion;

    @ApiModelProperty(value = "status", dataType = "Boolean", example = "true")
    private Boolean status;

    @ApiModelProperty(value = "healthTime", dataType = "LocalDateTime", example = "2021-07-20 20:53:00")
    private LocalDateTime healthTime;

    @ApiModelProperty(value = "heartbeatTime", dataType = "LocalDateTime", example = "2021-07-20 20:53:00")
    private LocalDateTime heartbeatTime;

    public DataBase toBean() {
        DataBase dataBase = new DataBase();
        BeanUtil.copyProperties(this, dataBase);
        return dataBase;
    }
}
