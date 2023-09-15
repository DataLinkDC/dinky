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

package org.dinky.data.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AlertRuleDTO", description = "Alert Rule Data Transfer Object")
public class AlertRuleDTO {

    @ApiModelProperty(value = "Rule ID", dataType = "Integer", example = "1", notes = "The ID of the rule")
    private Integer id;

    @ApiModelProperty(value = "Template ID", dataType = "Integer", example = "2", notes = "The ID of the template")
    private Integer templateId;

    @ApiModelProperty(
            value = "Rule Name",
            dataType = "String",
            example = "High CPU Usage",
            notes = "The name of the rule")
    private String name;

    @ApiModelProperty(
            value = "Rule Expression",
            dataType = "String",
            example = "cpuUsage > 90",
            notes = "The expression of the rule")
    private String rule;

    @ApiModelProperty(value = "Rule Type", dataType = "String", example = "Threshold", notes = "The type of the rule")
    private String ruleType;

    @ApiModelProperty(
            value = "Trigger Conditions",
            dataType = "String",
            example = "CPU usage exceeds 90%",
            notes = "The trigger conditions of the rule")
    private String triggerConditions;

    @ApiModelProperty(
            value = "Description",
            dataType = "String",
            example = "Alert when CPU usage is too high",
            notes = "The description of the rule")
    private String description;

    @ApiModelProperty(
            value = "Enabled Flag",
            dataType = "Boolean",
            example = "true",
            notes = "Whether the rule is enabled")
    private Boolean enabled;

    @ApiModelProperty(
            value = "Template Name",
            dataType = "String",
            example = "High CPU Alert Template",
            notes = "The name of the template")
    private String templateName;

    @ApiModelProperty(
            value = "Template Content",
            dataType = "String",
            example = "Alert template content...",
            notes = "The content of the template")
    private String templateContent;
}
