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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("dinky_sys_operate_log")
@ApiModel(value = "OperateLog", description = "Operate Log Information")
public class OperateLog implements Serializable {

    private static final long serialVersionUID = -8321546342131006474L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "ID", dataType = "Long", example = "1", notes = "Unique identifier for the operate log")
    private Long id;

    @ApiModelProperty(
            value = "Module Name",
            dataType = "String",
            example = "User Management",
            notes = "Name of the operation module")
    private String moduleName;

    @ApiModelProperty(
            value = "Business Type",
            dataType = "Integer",
            example = "1",
            notes = "Type of business operation")
    private Integer businessType;

    @ApiModelProperty(value = "Method", dataType = "String", example = "GET", notes = "HTTP request method")
    private String method;

    @ApiModelProperty(
            value = "Request Method",
            dataType = "String",
            example = "GET",
            notes = "Type of HTTP request (e.g., GET, POST)")
    private String requestMethod;

    @ApiModelProperty(
            value = "Operate Name",
            dataType = "String",
            example = "Create User",
            notes = "Name of the operation")
    private String operateName;

    @ApiModelProperty(
            value = "Operate User ID",
            dataType = "Integer",
            example = "1001",
            notes = "ID of the user who performed the operation")
    private Integer operateUserId;

    @ApiModelProperty(
            value = "Operate URL",
            dataType = "String",
            example = "/api/user/create",
            notes = "URL where the operation was performed")
    private String operateUrl;

    @ApiModelProperty(
            value = "Operate IP",
            dataType = "String",
            example = "192.168.0.1",
            notes = "IP address from which the operation was performed")
    private String operateIp;

    @ApiModelProperty(
            value = "Operate Location",
            dataType = "String",
            example = "New York, USA",
            notes = "Location where the operation was performed")
    private String operateLocation;

    @ApiModelProperty(value = "Operate Parameters", dataType = "String", notes = "Parameters of the operation request")
    private String operateParam;

    @ApiModelProperty(value = "JSON Result", dataType = "String", notes = "JSON result or response of the operation")
    private String jsonResult;

    @ApiModelProperty(
            value = "Status",
            dataType = "Integer",
            example = "0",
            notes = "Status of the operation (0 for normal, 1 for exception)")
    private Integer status;

    @ApiModelProperty(
            value = "Error Message",
            dataType = "String",
            notes = "Error message if the operation encountered an exception")
    private String errorMsg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(
            value = "Operate Time",
            dataType = "String",
            notes = "Timestamp indicating the time of the operation")
    private LocalDateTime operateTime;
}
