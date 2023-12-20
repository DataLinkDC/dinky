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

package org.dinky.data.model.alert;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AlertHistory
 *
 * @since 2022/2/24 20:12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_alert_history")
@ApiModel(value = "AlertHistory", description = "Alert History Record")
public class AlertHistory implements Serializable {

    private static final long serialVersionUID = -7904869940473678282L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "ID", example = "1", required = true, dataType = "Integer")
    private Integer id;

    @ApiModelProperty(value = "Tenant ID", example = "1", required = true, dataType = "Integer")
    private Integer tenantId;

    @ApiModelProperty(value = "Alert Group ID", example = "1", required = true, dataType = "Integer")
    private Integer alertGroupId;

    @ApiModelProperty(value = "Alert Group", dataType = "AlertGroup")
    @TableField(exist = false)
    private AlertGroup alertGroup;

    @ApiModelProperty(value = "Alert Instance ID", example = "1", required = true, dataType = "Integer")
    private Integer jobInstanceId;

    @ApiModelProperty(value = "Alert title", example = "Alert title", required = true, dataType = "String")
    private String title;

    @ApiModelProperty(value = "Alert content", example = "Alert content", required = true, dataType = "String")
    private String content;

    @ApiModelProperty(value = "Alert status", example = "1", required = true, dataType = "Integer")
    private Integer status;

    @ApiModelProperty(value = "Alert log", example = "Alert log", required = true, dataType = "String")
    private String log;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "Create Time", example = "2022-02-24 20:12:00", dataType = "LocalDateTime")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "Update Time", example = "2022-02-24 20:12:00", dataType = "LocalDateTime")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;
}
