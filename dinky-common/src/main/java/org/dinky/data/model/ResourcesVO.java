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

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel(value = "Resources", description = "Resource Information")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourcesVO {

    @ApiModelProperty(value = "ID", dataType = "Integer", example = "1", notes = "Unique identifier for the resource")
    private Integer id;

    @ApiModelProperty(value = "File Name", dataType = "String", example = "example.txt", notes = "Name of the file")
    private String fileName;

    @ApiModelProperty(value = "Description", dataType = "String", notes = "Description or details about the resource")
    private String description;

    @ApiModelProperty(
            value = "User ID",
            dataType = "Integer",
            example = "1001",
            notes = "ID of the user who owns the resource")
    private Integer userId;

    @ApiModelProperty(
            value = "Resource Type",
            dataType = "Integer",
            example = "0",
            notes = "Type of the resource (0 for FILE, 1 for UDF)")
    private Integer type;

    @ApiModelProperty(
            value = "Resource Size",
            dataType = "Long",
            example = "1024",
            notes = "Size of the resource in bytes")
    private Long size;

    @ApiModelProperty(
            value = "Parent ID",
            dataType = "Integer",
            example = "0",
            notes = "ID of the parent resource (if applicable)")
    private Integer pid;

    @ApiModelProperty(
            value = "Full Name",
            dataType = "String",
            example = "path/to/example.txt",
            notes = "Full name or path of the resource")
    private String fullName;

    @ApiModelProperty(
            value = "Is Directory",
            dataType = "Boolean",
            notes = "Flag indicating if the resource is a directory")
    private Boolean isDirectory;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(
            value = "Create Time",
            dataType = "String",
            notes = "Timestamp indicating the creation time of the resource")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(
            value = "Update Time",
            dataType = "String",
            notes = "Timestamp indicating the last update time of the resource")
    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(
            value = "Is Leaf",
            dataType = "boolean",
            example = "false",
            notes = "Indicates whether the tree node is a leaf node (true/false)")
    private boolean isLeaf;

    @ApiModelProperty(value = "Creator", required = true, dataType = "Integer", example = "creator")
    private Integer creator;

    @ApiModelProperty(value = "Updater", required = true, dataType = "Integer", example = "updater")
    private Integer updater;
}
