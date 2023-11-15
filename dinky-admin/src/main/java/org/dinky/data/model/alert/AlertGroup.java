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

import org.dinky.mybatis.model.SuperEntity;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AlertGroup
 *
 * @since 2022/2/24 19:58
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_alert_group")
@ApiModel(value = "AlertGroup", description = "Alert Group")
public class AlertGroup extends SuperEntity<AlertGroup> {

    private static final long serialVersionUID = 7027411164191682344L;

    @ApiModelProperty(value = "Tenant ID", required = true, dataType = "Integer", example = "1")
    private Integer tenantId;

    @ApiModelProperty(value = "Alert Instance Ids", required = true, dataType = "String", example = "1,2,3")
    private String alertInstanceIds;

    @ApiModelProperty(value = "Alert Group Note", required = true, dataType = "String", example = "Alert Group Note")
    private String note;

    @TableField(exist = false)
    @ApiModelProperty(value = "Alert Instances", required = true, dataType = "List<AlertInstance>")
    private List<AlertInstance> instances;
}
