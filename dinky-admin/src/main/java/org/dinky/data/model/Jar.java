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

import org.dinky.mybatis.model.SuperEntity;

import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Jar
 *
 * @since 2021/11/13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_jar")
@ApiModel(value = "Jar", description = "Jar Information")
public class Jar extends SuperEntity {

    private static final long serialVersionUID = 3769276772487490408L;

    @ApiModelProperty(value = "Tenant ID", dataType = "Integer", example = "1", notes = "Tenant ID of the Jar")
    private Integer tenantId;

    @ApiModelProperty(value = "Type", dataType = "String", example = "type", notes = "Type of the Jar")
    private String type;

    @ApiModelProperty(value = "Path", dataType = "String", example = "/path/to/jar.jar", notes = "Path to the Jar file")
    private String path;

    @ApiModelProperty(
            value = "Main Class",
            dataType = "String",
            example = "com.example.Main",
            notes = "Main class of the Jar")
    private String mainClass;

    @ApiModelProperty(
            value = "Parameters",
            dataType = "String",
            example = "--param1 value1 --param2 value2",
            notes = "Parameters for running the Jar")
    private String paras;

    @ApiModelProperty(
            value = "Note",
            dataType = "String",
            example = "This is a note about the Jar",
            notes = "Additional notes or description")
    private String note;
}
