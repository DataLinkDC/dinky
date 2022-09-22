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

package com.dlink.scheduler.model;

import com.dlink.scheduler.enums.Flag;
import com.dlink.scheduler.enums.ProcessExecutionTypeEnum;
import com.dlink.scheduler.enums.ReleaseState;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * process definition
 */
@Data
public class ProcessDefinition {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "编号")
    private Long code;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "发布状态 online/offline")
    private ReleaseState releaseState;

    @ApiModelProperty(value = "项目编号")
    private Long projectCode;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "用户自定义参数")
    private String globalParams;

    @ApiModelProperty(value = "用户自定义参数列表")
    private List<Property> globalParamList;

    @ApiModelProperty(value = "用户自定义参数映射")
    private Map<String, String> globalParamMap;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "是否生效 yes/no")
    private Flag flag;

    @ApiModelProperty(value = "创建人id")
    private Integer userId;

    @ApiModelProperty(value = "创建人名称")
    private String userName;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "位置")
    private String locations;

    @ApiModelProperty(value = "计划发布状态 online/offline")
    private ReleaseState scheduleReleaseState;

    @ApiModelProperty(value = "超时告警(分钟)")
    private Integer timeout;

    @ApiModelProperty(value = "租户id")
    private Integer tenantId;

    @ApiModelProperty(value = "租户编号")
    private String tenantCode;

    @ApiModelProperty(value = "修改用户名")
    private String modifyBy;

    @ApiModelProperty(value = "告警分组id")
    private Integer warningGroupId;

    @ApiModelProperty(value = "执行类型")
    private ProcessExecutionTypeEnum executionType;

}
