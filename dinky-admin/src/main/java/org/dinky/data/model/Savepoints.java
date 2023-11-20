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

import org.dinky.mybatis.annotation.Save;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Savepoints
 *
 * @since 2021/11/21 16:10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_savepoints")
@ApiModel(value = "Savepoints", description = "Savepoints Information")
public class Savepoints implements Serializable {

    private static final long serialVersionUID = 115345627846554078L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "ID", dataType = "Integer", example = "1", notes = "Unique identifier for the savepoint")
    private Integer id;

    @ApiModelProperty(
            value = "Tenant ID",
            dataType = "Integer",
            example = "1001",
            notes = "ID of the tenant associated with the savepoint")
    private Integer tenantId;

    @NotNull(
            message = "Task ID cannot be null",
            groups = {Save.class})
    @ApiModelProperty(
            value = "Task ID",
            dataType = "Integer",
            example = "2001",
            notes = "ID of the job/task associated with the savepoint")
    private Integer taskId;

    @ApiModelProperty(value = "Name", dataType = "String", notes = "Name of the savepoint")
    private String name;

    @ApiModelProperty(value = "Type", dataType = "String", notes = "Type of the savepoint")
    private String type;

    @ApiModelProperty(value = "Path", dataType = "String", notes = "Path to the savepoint")
    private String path;

    @TableField(fill = FieldFill.INSERT)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(
            value = "Create Time",
            dataType = "String",
            notes = "Timestamp indicating the creation time of the savepoint")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "Creator", dataType = "String", notes = "Creator of the savepoint")
    private Integer creator;

    protected Serializable pkVal() {
        return this.id;
    }
}
