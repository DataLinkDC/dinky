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

import lombok.Data;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
@TableName(value = "dinky_git_project")
@Data
public class GitProject extends SuperEntity<GitProject> {

    /** */
    @TableField(value = "tenant_id")
    private Long tenantId;

    /** */
    @TableField(value = "url")
    private String url;

    /** */
    @TableField(value = "branch")
    private String branch;

    /** */
    @TableField(value = "username")
    private String username;

    /** */
    @TableField(value = "password")
    private String password;

    private String privateKey;

    /** */
    @TableField(value = "pom")
    private String pom;

    /** */
    @TableField(value = "build_args")
    private String buildArgs;

    /** */
    @TableField(value = "code_type")
    private Integer codeType;
    /** */
    @TableField(value = "type")
    private Integer type;

    /** */
    @TableField(value = "last_build")
    private Date lastBuild;

    /** */
    @TableField(value = "description")
    private String description;

    /** */
    @TableField(value = "build_state")
    private Integer buildState;

    @TableField(value = "build_step")
    private Integer buildStep;

    /** scan udf class */
    @TableField(value = "udf_class_map_list")
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private String udfClassMapList;

    @TableField(value = "order_line")
    private Integer orderLine;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

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
