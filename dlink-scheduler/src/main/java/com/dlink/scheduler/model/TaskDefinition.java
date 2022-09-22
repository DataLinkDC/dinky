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
import com.dlink.scheduler.enums.Priority;
import com.dlink.scheduler.enums.TaskExecuteType;
import com.dlink.scheduler.enums.TaskTimeoutStrategy;
import com.dlink.scheduler.enums.TimeoutFlag;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * task definition
 */
@Data
public class TaskDefinition {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "编号")
    private Long code;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "项目编号")
    private Long projectCode;

    @ApiModelProperty(value = "创建人")
    private Integer userId;

    @ApiModelProperty(value = "任务类型")
    private String taskType;

    @ApiModelProperty(value = "任务参数")
    private String taskParams;

    @ApiModelProperty(value = "任务参数列表")
    private List<Property> taskParamList;

    @ApiModelProperty(value = "任务参数映射")
    private Map<String, String> taskParamMap;

    @ApiModelProperty(value = "运行标志 yes 正常/no 禁止执行")
    private Flag flag;

    @ApiModelProperty(value = "优先级")
    private Priority taskPriority;

    @ApiModelProperty(value = "创建用户名")
    private String userName;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "worker分组")
    private String workerGroup;

    @ApiModelProperty(value = "环境编号")
    private Long environmentCode;

    @ApiModelProperty(value = "重试次数")
    private Integer failRetryTimes;

    @ApiModelProperty(value = "重试间隔")
    private Integer failRetryInterval;

    @ApiModelProperty(value = "超时标识")
    private TimeoutFlag timeoutFlag;

    @ApiModelProperty(value = "超时通知策略")
    private TaskTimeoutStrategy timeoutNotifyStrategy;

    @ApiModelProperty(value = "超时时间(分钟)")
    private Integer timeout;

    @ApiModelProperty(value = "延迟执行时间")
    private Integer delayTime;

    @ApiModelProperty(value = "资源ids")
    private String resourceIds;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "更新人")
    private String modifyBy;

    @ApiModelProperty(value = "任务组id")
    private Integer taskGroupId;

    @ApiModelProperty(value = "任务组id")
    private Integer taskGroupPriority;

    @ApiModelProperty(value = "cpu 配额")
    private Integer cpuQuota;

    @ApiModelProperty(value = "最大内存")
    private Integer memoryMax;

    @ApiModelProperty(value = "执行类型")
    private TaskExecuteType taskExecuteType;

    @ApiModelProperty(value = "工作流编号")
    private Long processDefinitionCode;

    @ApiModelProperty(value = "工作流编号")
    private Integer processDefinitionVersion;

    @ApiModelProperty(value = "工作流名")
    private String processDefinitionName;

    @ApiModelProperty(value = "前置任务集合")
    private Map<Long, String> upstreamTaskMap;
}
