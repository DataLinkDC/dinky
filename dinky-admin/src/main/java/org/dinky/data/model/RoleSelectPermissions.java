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

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_role_select_permissions")
public class RoleSelectPermissions implements Serializable {

    private static final long serialVersionUID = 8676666963206334660L;

    /** id */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /** role id */
    private Integer roleId;

    /** table_name */
    private String tableName;

    /** expression */
    private String expression;

    /** create time */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** update time */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** role code */
    @TableField(exist = false)
    private String roleCode;

    /** role name */
    @TableField(exist = false)
    private String roleName;
}
