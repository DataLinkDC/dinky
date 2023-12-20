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

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/** assign role params */
@Data
@ApiModel(value = "AssignRoleDTO", description = "Parameters for Assigning Roles to a User")
public class AssignRoleDTO {

    @ApiModelProperty(value = "User ID", dataType = "Integer", notes = "ID of the user to assign roles to")
    private Integer userId;

    @ApiModelProperty(value = "Role IDs", dataType = "List<Integer>", notes = "List of role IDs to assign to the user")
    private List<Integer> roleIds;
}
