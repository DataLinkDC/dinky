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

package org.dinky.data.model.rbac;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_row_permissions")
@ApiModel(value = "RowPermissions", description = "Row-Level Permissions Information")
public class RowPermissions implements Serializable {

    private static final long serialVersionUID = 8676666963206334660L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(
            value = "ID",
            dataType = "Integer",
            example = "1",
            notes = "Unique identifier for the row-level permissions")
    private Integer id;

    @ApiModelProperty(
            value = "Role ID",
            dataType = "Integer",
            example = "1001",
            notes = "ID of the role associated with the row permissions")
    private Integer roleId;

    @ApiModelProperty(
            value = "Table Name",
            dataType = "String",
            example = "users",
            notes = "Name of the table to which the row permissions apply")
    private String tableName;

    @ApiModelProperty(
            value = "Expression",
            dataType = "String",
            example = "user_id = 1001",
            notes = "Expression defining the row-level permissions")
    private String expression;

    @TableField(fill = FieldFill.INSERT)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(
            value = "Create Time",
            dataType = "String",
            notes = "Timestamp indicating the creation time of the row-level permissions")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(
            value = "Update Time",
            dataType = "String",
            notes = "Timestamp indicating the last update time of the row-level permissions")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    @ApiModelProperty(
            value = "Role Code",
            dataType = "String",
            example = "ROLE_ADMIN",
            notes = "Code representing the associated role")
    private String roleCode;

    @TableField(exist = false)
    @ApiModelProperty(
            value = "Role Name",
            dataType = "String",
            example = "Administrator",
            notes = "Name of the associated role")
    private String roleName;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "Creator", required = true, dataType = "Integer", example = "creator")
    private Integer creator;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "Updater", required = true, dataType = "Integer", example = "updater")
    private Integer updater;
}
