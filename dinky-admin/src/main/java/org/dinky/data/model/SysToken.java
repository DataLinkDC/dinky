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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.EnumValue;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("dinky_sys_token")
@ApiModel(value = "SysToken", description = "System Token Information")
public class SysToken implements Serializable {
    private static final long serialVersionUID = 3579444102399317143L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(
            value = "ID",
            dataType = "Integer",
            example = "1",
            notes = "Unique identifier for the system token")
    private Integer id;

    @ApiModelProperty(value = "Token Value", dataType = "String", notes = "Value of the system token")
    private String tokenValue;

    @ApiModelProperty(
            value = "User ID",
            dataType = "Integer",
            example = "1001",
            notes = "ID of the user associated with the token")
    private Integer userId;

    @ApiModelProperty(
            value = "Role ID",
            dataType = "Integer",
            example = "2001",
            notes = "ID of the role associated with the token")
    private Integer roleId;

    @ApiModelProperty(
            value = "Tenant ID",
            dataType = "Integer",
            example = "3001",
            notes = "ID of the tenant associated with the token")
    private Integer tenantId;

    @ApiModelProperty(value = "Expire Type", dataType = "Integer", example = "1", notes = "Type of token expiration")
    private Integer expireType;

    @ApiModelProperty(value = "Expire Start Time", dataType = "Date", notes = "Start time for token expiration")
    private Date expireStartTime;

    @ApiModelProperty(value = "Expire End Time", dataType = "Date", notes = "End time for token expiration")
    private Date expireEndTime;

    @TableField(fill = FieldFill.INSERT)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(
            value = "Create Time",
            dataType = "String",
            notes = "Timestamp indicating the creation time of the token")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(
            value = "Update Time",
            dataType = "String",
            notes = "Timestamp indicating the last update time of the token")
    private LocalDateTime updateTime;

    @ApiModelProperty(
            value = "Creator",
            dataType = "Integer",
            example = "1001",
            notes = "ID of the user who created the token")
    private Integer creator;

    @ApiModelProperty(
            value = "updater",
            dataType = "Integer",
            example = "1002",
            notes = "ID of the user who last updated the token")
    private Integer updater;

    @TableField(exist = false)
    @ApiModelProperty(
            value = "User Name",
            dataType = "String",
            example = "John Doe",
            notes = "Name of the associated user")
    private String userName;

    @TableField(exist = false)
    @ApiModelProperty(
            value = "Role Name",
            dataType = "String",
            example = "ROLE_ADMIN",
            notes = "Name of the associated role")
    private String roleName;

    @TableField(exist = false)
    @ApiModelProperty(
            value = "Tenant Code",
            dataType = "String",
            example = "TENANT001",
            notes = "Code representing the associated tenant")
    private String tenantCode;

    @TableField(exist = false)
    @ApiModelProperty(
            value = "Expire Time Range",
            dataType = "List<LocalDateTime>",
            notes = "List of timestamps indicating the time range for token expiration")
    private List<LocalDateTime> expireTimeRange;

    private Source source;

    @Getter
    @AllArgsConstructor
    public enum Source {
        LOGIN(1),
        CUSTOM(2);

        @EnumValue
        private final int type;
    }
}
