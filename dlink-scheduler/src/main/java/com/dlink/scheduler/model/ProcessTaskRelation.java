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

import com.dlink.scheduler.enums.ConditionType;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProcessTaskRelation {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "工作流定义")
    private int processDefinitionVersion;

    @ApiModelProperty(value = "项目编号")
    private long projectCode;

    @ApiModelProperty(value = "工作流定义编号")
    private long processDefinitionCode;

    @ApiModelProperty(value = "前置任务编号")
    private long preTaskCode;

    @ApiModelProperty(value = "前置任务版本")
    private int preTaskVersion;

    @ApiModelProperty(value = "发布任务编号")
    private long postTaskCode;

    @ApiModelProperty(value = "发布任务版本")
    private int postTaskVersion;

    @ApiModelProperty(value = "条件类型")
    private ConditionType conditionType;

    @ApiModelProperty(value = "条件参数")
    private String conditionParams;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
