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

package com.dlink.model;

import com.dlink.dto.TaskVersionConfigureDTO;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
    * 作业
    */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "dlink_task_version", autoResultMap = true)
public class TaskVersion implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * tenant id
     */
    private Integer tenantId;

    /**
     * 作业ID
     */
    @TableField(value = "task_id")
    private Integer taskId;

    /**
     * 版本ID
     */
    @TableField(value = "version_id")
    private Integer versionId;

    /**
     * flink sql 内容
     */
    @TableField(value = "`statement`")
    private String statement;

    /**
     * 名称
     */
    @TableField(value = "`name`")
    private String name;

    /**
     * 别名
     */
    @TableField(value = "`alias`")
    private String alias;

    /**
     * 方言
     */
    @TableField(value = "dialect")
    private String dialect;

    /**
     * 类型
     */
    @TableField(value = "`type`")
    private String type;

    @TableField(value = "task_configure",typeHandler = JacksonTypeHandler.class)
    private TaskVersionConfigureDTO taskConfigure;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;
}
