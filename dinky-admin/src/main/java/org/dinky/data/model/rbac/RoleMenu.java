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

import org.dinky.mybatis.model.DateBaseEntity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName("dinky_sys_role_menu")
@Data
@ApiModel(value = "RoleMenu", description = "Role-Menu Relationship Information")
public class RoleMenu extends DateBaseEntity<RoleMenu> implements Serializable {

    private static final long serialVersionUID = -6465347305968663704L;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(
            value = "ID",
            dataType = "Integer",
            example = "1",
            notes = "Unique identifier for the role-menu relationship")
    private Integer id;

    @ApiModelProperty(
            value = "Role ID",
            dataType = "Integer",
            example = "1001",
            notes = "ID of the role associated with the menu")
    private Integer roleId;

    @ApiModelProperty(
            value = "Menu ID",
            dataType = "Integer",
            example = "2001",
            notes = "ID of the menu associated with the role")
    private Integer menuId;
}
