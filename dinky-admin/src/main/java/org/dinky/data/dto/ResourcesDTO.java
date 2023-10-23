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

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "ResourcesDTO", description = "DTO for representing resources")
public class ResourcesDTO implements Serializable {

    @ApiModelProperty(
            value = "Resource ID",
            dataType = "Integer",
            example = "1",
            notes = "The unique identifier of the resource")
    private Integer id;

    @ApiModelProperty(
            value = "File Name",
            dataType = "String",
            example = "resource_file.txt",
            notes = "The name of the resource file")
    private String fileName;

    @ApiModelProperty(
            value = "Parent ID",
            dataType = "Integer",
            example = "1",
            notes = "The unique identifier of the parent resource")
    private Integer pid;

    @ApiModelProperty(
            value = "Description",
            dataType = "String",
            example = "A sample resource file",
            notes = "A brief description of the resource")
    private String description;
}
