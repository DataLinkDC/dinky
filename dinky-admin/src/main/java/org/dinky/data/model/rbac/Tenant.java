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

package org.dinky.data.model.rbac;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** tenant use to isolate data */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_tenant")
@ApiModel(value = "Tenant", description = "Tenant model")
public class Tenant implements Serializable {

    private static final long serialVersionUID = -7782313413034278131L;
    /** id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "Tenant ID", required = true, dataType = "Integer", example = "1")
    private Integer id;

    /** code */
    @ApiModelProperty(value = "Tenant Code", required = true, dataType = "String", example = "Default")
    private String tenantCode;

    /** note */
    @ApiModelProperty(value = "Tenant Note", required = true, dataType = "String", example = "Default")
    private String note;

    /** is delete */
    @ApiModelProperty(value = "Is Delete", required = true, dataType = "Boolean", example = "false")
    private Boolean isDelete;

    /** create time */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(
            value = "Create Time",
            required = true,
            dataType = "LocalDateTime",
            example = "2022-02-24 19:58:00")
    private LocalDateTime createTime;

    /** update time */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(
            value = "Update Time",
            required = true,
            dataType = "LocalDateTime",
            example = "2022-02-24 19:58:00")
    private LocalDateTime updateTime;
}
