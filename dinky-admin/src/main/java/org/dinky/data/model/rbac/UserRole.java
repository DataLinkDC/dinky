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

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_user_role")
@ApiModel(value = "UserRole", description = "User Role Relationship")
public class UserRole extends DateBaseEntity<UserRole> implements Serializable {
    private static final long serialVersionUID = -6123386787317880485L;

    @ApiModelProperty(value = "ID", dataType = "Integer", notes = "Unique identifier for the user role relationship")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "User ID", dataType = "Integer", notes = "ID of the user associated with this role")
    private Integer userId;

    @ApiModelProperty(value = "Role ID", dataType = "Integer", notes = "ID of the role associated with this user")
    private Integer roleId;
}
