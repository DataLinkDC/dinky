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

import org.dinky.mybatis.annotation.Save;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

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

@Data
@ApiModel(value = "LoginLog", description = "Login Log Information")
@TableName("dinky_sys_login_log")
public class LoginLog implements Serializable {

    private static final long serialVersionUID = -3922488670506709018L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "ID", dataType = "Integer", example = "1", notes = "Unique identifier for the login log")
    private Integer id;

    @NotNull(
            message = "User ID cannot be null",
            groups = {Save.class})
    @ApiModelProperty(value = "User ID", dataType = "Integer", example = "1001", notes = "ID of the user who logged in")
    private Integer userId;

    @NotNull(
            message = "Username cannot be null",
            groups = {Save.class})
    @ApiModelProperty(
            value = "Username",
            dataType = "String",
            example = "john_doe",
            notes = "Username of the user who logged in")
    private String username;

    @NotNull(
            message = "IP cannot be null",
            groups = {Save.class})
    @ApiModelProperty(
            value = "IP",
            dataType = "String",
            example = "192.168.0.1",
            notes = "IP address from which the login occurred")
    private String ip;

    @ApiModelProperty(
            value = "Login Type",
            dataType = "Integer",
            example = "1",
            notes = "Type of login (if applicable)")
    private Integer loginType;

    @ApiModelProperty(value = "Status", dataType = "Integer", example = "0", notes = "Status of the login")
    private Integer status;

    @ApiModelProperty(value = "Message", dataType = "String", notes = "Additional message or details about the login")
    private String msg;

    @ApiModelProperty(value = "Is Deleted", dataType = "Boolean", notes = "Flag indicating if the login log is deleted")
    private Boolean isDeleted;

    @ApiModelProperty(value = "Access Time", dataType = "String", notes = "Timestamp indicating the time of login")
    private LocalDateTime accessTime;

    @TableField(fill = FieldFill.INSERT)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @ApiModelProperty(
            value = "Create Time",
            dataType = "String",
            notes = "Timestamp indicating the creation time of the login log")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @ApiModelProperty(
            value = "Update Time",
            dataType = "String",
            notes = "Timestamp indicating the last update time of the login log")
    private LocalDateTime updateTime;
}
