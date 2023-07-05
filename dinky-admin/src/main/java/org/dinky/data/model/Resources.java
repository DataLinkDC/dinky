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

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Getter;
import lombok.Setter;

/** @TableName dinky_resources */
@TableName(value = "dinky_resources")
@Getter
@Setter
public class Resources extends Model<Resources> {
    /** key */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** file name */
    private String fileName;

    /** */
    private String description;

    /** user id */
    private Integer userId;

    /** resource type,0:FILE，1:UDF */
    private Integer type;

    /** resource size */
    private Long size;

    /** */
    private Integer pid;

    /** */
    private String fullName;

    /** */
    private Boolean isDirectory;

    /** create time */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** update time */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
