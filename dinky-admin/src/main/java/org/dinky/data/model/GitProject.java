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

import java.util.Date;

import org.springframework.boot.jackson.JsonObjectDeserializer;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
@TableName(value = "dinky_git_project")
@Data
@ApiModel(value = "GitProject", description = "Git Project Information")
public class GitProject extends SuperEntity<GitProject> {
    /** */
    @ApiModelProperty(value = "Tenant ID", example = "1", dataType = "Long")
    @TableField(value = "tenant_id")
    private Long tenantId;

    @ApiModelProperty(
            value = "URL",
            example = "https://github.com/example/project.git",
            dataType = "String",
            required = true)
    @TableField(value = "url")
    private String url;

    @ApiModelProperty(value = "Branch", example = "main", dataType = "String")
    @TableField(value = "branch")
    private String branch;

    @ApiModelProperty(value = "Username", example = "john_doe", dataType = "String")
    @TableField(value = "username")
    private String username;

    @ApiModelProperty(value = "Password", example = "********", dataType = "String")
    @TableField(value = "password")
    private String password;

    @ApiModelProperty(value = "Private Key", dataType = "String")
    private String privateKey;

    @ApiModelProperty(value = "POM", example = "pom.xml", dataType = "String")
    @TableField(value = "pom")
    private String pom;

    @ApiModelProperty(value = "Build Arguments", dataType = "String")
    @TableField(value = "build_args")
    private String buildArgs;

    @ApiModelProperty(value = "Code Type", example = "1", dataType = "Integer")
    @TableField(value = "code_type")
    private Integer codeType;

    @ApiModelProperty(value = "Type", example = "1", dataType = "Integer")
    @TableField(value = "type")
    private Integer type;

    @ApiModelProperty(value = "Last Build Date", dataType = "Date")
    @TableField(value = "last_build")
    private Date lastBuild;

    @ApiModelProperty(value = "Description", dataType = "String")
    @TableField(value = "description")
    private String description;

    @ApiModelProperty(value = "Build State", example = "1", dataType = "Integer")
    @TableField(value = "build_state")
    private Integer buildState;

    /**
     * 区别于 java 和 Python 类型 | different from java and python;
     * 1. 构建 java 工程时:   步骤值映射如下: 0: 环境检查 1: 克隆项目 2: 编译构建 3: 获取产物 4: 分析 UDF 5: 完成; (when build java project, the step value is as follows: 0: environment check 1: clone project 2: compile and build 3: get artifact 4: analyze UDF 5: finish)
     * 2. 构建 python 工程时: 步骤值映射如下: 0: 环境检查 1: 克隆项目 2: 获取产物 3: 分析 UDF 4: 完成;(when build python project, the step value is as follows: 0: environment check 1: clone project 2: get artifact 3: analyze UDF 4: finish)
     */
    @ApiModelProperty(value = "Build Step", dataType = "Integer")
    @TableField(value = "build_step")
    private Integer buildStep;

    @ApiModelProperty(value = "UDF Class Map List", dataType = "String")
    @TableField(value = "udf_class_map_list")
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private String udfClassMapList;

    @ApiModelProperty(value = "Order Line", dataType = "Integer")
    @TableField(value = "order_line")
    private Integer orderLine;

    @ApiModelProperty(value = "Operator", dataType = "Integer")
    @TableField(value = "operator")
    private Integer operator;

    public Integer getExecState() {
        switch (getBuildState()) {
            case 1:
                return 1;
            case 3:
                return 2;
            case 0:
            case 2:
            default:
                return 0;
        }
    }
}
