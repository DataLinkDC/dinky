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

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
@Getter
@Setter
@ApiModel(value = "GitProjectDTO", description = "DTO for Git project information")
public class GitProjectDTO {

    @ApiModelProperty(
            value = "Project ID",
            dataType = "Long",
            example = "1",
            notes = "The unique identifier for the project")
    private Long id;

    @ApiModelProperty(
            value = "Project Name",
            dataType = "String",
            example = "MyGitProject",
            notes = "The name of the Git project")
    @NotNull(message = "Name cannot be null")
    private String name;

    @ApiModelProperty(
            value = "Git URL",
            dataType = "String",
            example = "https://github.com/myusername/myrepo.git",
            notes = "The URL of the Git repository")
    @NotNull(message = "URL cannot be null")
    private String url;

    @ApiModelProperty(
            value = "Branch Name",
            dataType = "String",
            example = "main",
            notes = "The name of the Git branch")
    @NotNull(message = "Branch cannot be null")
    private String branch;

    @ApiModelProperty(value = "Username", dataType = "String", example = "myusername", notes = "The Git username")
    private String username;

    @ApiModelProperty(value = "Password", dataType = "String", example = "mypassword", notes = "The Git password")
    private String password;

    @ApiModelProperty(
            value = "Private Key",
            dataType = "String",
            example = "-----BEGIN RSA PRIVATE KEY-----\n... (private key content)",
            notes = "The Git private key")
    private String privateKey;

    @ApiModelProperty(value = "POM File", dataType = "String", example = "pom.xml", notes = "The name of the POM file")
    private String pom;

    @ApiModelProperty(
            value = "Build Arguments",
            dataType = "String",
            example = "-DskipTests",
            notes = "Arguments for building the project")
    private String buildArgs;

    @ApiModelProperty(value = "Code Type", dataType = "Integer", example = "1", notes = "The code type")
    private Integer codeType;

    @ApiModelProperty(value = "Project Type", dataType = "Integer", example = "2", notes = "The type of the project")
    private Integer type;

    @ApiModelProperty(
            value = "Project Description",
            dataType = "String",
            example = "My Git project",
            notes = "A description of the Git project")
    private String description;

    @ApiModelProperty(
            value = "Enabled Flag",
            dataType = "Boolean",
            example = "true",
            notes = "Indicates whether the project is enabled")
    @NotNull(message = "Enabled flag cannot be null")
    private Boolean enabled;

    @ApiModelProperty(
            value = "Order Line",
            dataType = "Integer",
            example = "1",
            notes = "The order line for the project")
    private Integer orderLine;
}
