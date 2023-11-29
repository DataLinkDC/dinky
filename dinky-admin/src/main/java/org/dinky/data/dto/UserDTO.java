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

import org.dinky.data.model.rbac.Menu;
import org.dinky.data.model.rbac.Role;
import org.dinky.data.model.rbac.Tenant;
import org.dinky.data.model.rbac.User;

import java.util.List;

import cn.dev33.satoken.stp.SaTokenInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "UserDTO", description = "User DTO")
public class UserDTO {

    @ApiModelProperty(value = "user", required = true, dataType = "User", allowEmptyValue = false)
    private User user;

    @ApiModelProperty(value = "roleList", required = true, dataType = "List<Role>", allowEmptyValue = false)
    private List<Role> roleList;

    @ApiModelProperty(value = "tenantList", required = true, dataType = "List<Tenant>", allowEmptyValue = false)
    private List<Tenant> tenantList;

    @ApiModelProperty(value = "menuList", required = true, dataType = "List<Menu>", allowEmptyValue = false)
    private List<Menu> menuList;

    @ApiModelProperty(value = "currentTenant", required = true, dataType = "Tenant", allowEmptyValue = true)
    private Tenant currentTenant;

    @ApiModelProperty(value = "tokenInfo", required = true, dataType = "SaTokenInfo", allowEmptyValue = true)
    private SaTokenInfo tokenInfo;
}
