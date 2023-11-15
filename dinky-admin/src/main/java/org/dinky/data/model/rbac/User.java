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

import org.dinky.mybatis.annotation.Save;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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

/**
 * User
 *
 * @since 2021/5/28 15:57
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_user")
@ApiModel(value = "User", description = "User Information")
public class User implements Serializable {
    private static final long serialVersionUID = -1077801296270024204L;

    @ApiModelProperty(value = "ID", dataType = "Integer", notes = "Unique identifier for the user")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "Username", dataType = "String", required = true, notes = "User's username")
    @NotNull(
            message = "Username cannot be null",
            groups = {Save.class})
    private String username;

    @ApiModelProperty(value = "Password", dataType = "String", notes = "User's password")
    private String password;

    @ApiModelProperty(value = "Nickname", dataType = "String", notes = "User's nickname")
    private String nickname;

    @ApiModelProperty(value = "User Type", dataType = "int", notes = "Type of the user")
    private int userType;

    @ApiModelProperty(value = "Work Number", dataType = "String", notes = "User's work number")
    private String worknum;

    @ApiModelProperty(value = "Avatar", dataType = "byte[]", notes = "User's avatar image")
    private byte[] avatar;

    @ApiModelProperty(value = "Mobile", dataType = "String", notes = "User's mobile number")
    private String mobile;

    @ApiModelProperty(value = "Enabled", dataType = "Boolean", notes = "Whether the user is enabled")
    private Boolean enabled;

    @ApiModelProperty(value = "Is Delete", dataType = "Boolean", notes = "Whether the user is deleted")
    @TableLogic
    private Boolean isDelete;

    @ApiModelProperty(value = "Create Time", dataType = "LocalDateTime", notes = "Timestamp when the user was created")
    @TableField(fill = FieldFill.INSERT)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(
            value = "Update Time",
            dataType = "LocalDateTime",
            notes = "Timestamp when the user was last updated")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "Super Admin Flag", dataType = "Boolean", notes = "Whether the user is a super admin")
    private Boolean superAdminFlag;

    @ApiModelProperty(value = "Tenant Admin Flag", dataType = "Boolean", notes = "Whether the user is a tenant admin")
    @TableField(exist = false)
    private Boolean tenantAdminFlag;
}
