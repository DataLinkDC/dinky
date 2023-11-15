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

import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("dinky_alert_rules")
@ApiModel(value = "AlertRule", description = "AlertRule")
public class AlertRule extends SuperEntity<AlertRule> {

    @ApiModelProperty(value = "rule", required = true, dataType = "String", example = "rule")
    String rule;

    @ApiModelProperty(value = "templateId", required = true, dataType = "int", example = "1")
    int templateId;

    @ApiModelProperty(
            value = "ruleType",
            required = true,
            dataType = "String",
            example = "ruleType",
            allowableValues = "CUSTOM,SYSTEM",
            extensions = {
                @Extension(
                        name = "ruleType-enum",
                        properties = {@ExtensionProperty(name = "values", value = "CUSTOM,SYSTEM")})
            })
    String ruleType;

    @ApiModelProperty(
            value = "triggerConditions",
            required = true,
            dataType = "String",
            example = "or",
            allowableValues = "or,and",
            extensions = {
                @Extension(
                        name = "triggerConditions-enum",
                        properties = {@ExtensionProperty(name = "values", value = "or,and")})
            })
    String triggerConditions;

    @ApiModelProperty(value = "description", required = true, dataType = "String", example = "description")
    String description;
}
