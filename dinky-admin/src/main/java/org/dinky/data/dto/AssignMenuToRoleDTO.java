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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "AssignMenuToRoleDto", description = "DTO for assigning menus to a role")
public class AssignMenuToRoleDTO {

    @ApiModelProperty(value = "Role ID", dataType = "Integer", example = "1", notes = "The ID of the role")
    private Integer roleId;

    @ApiModelProperty(
            value = "Menu IDs",
            dataType = "List<Integer>",
            example = "[1, 2, 3]",
            notes = "A list of menu IDs to assign to the role")
    private List<Integer> menuIds;
}
