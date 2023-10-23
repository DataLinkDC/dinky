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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * ModifyPasswordDTO
 *
 * @since 2022/2/22 23:27
 */
@Getter
@Setter
@ApiModel(value = "ModifyPasswordDTO", description = "DTO for modifying user password")
public class ModifyPasswordDTO {

    @ApiModelProperty(
            value = "User ID",
            dataType = "String",
            example = "123",
            notes = "The unique identifier of the user")
    private String id;

    @ApiModelProperty(value = "Username", dataType = "String", example = "john_doe", notes = "The username of the user")
    private String username;

    @ApiModelProperty(
            value = "Current Password",
            dataType = "String",
            example = "current_password",
            notes = "The current password of the user")
    private String password;

    @ApiModelProperty(
            value = "New Password",
            dataType = "String",
            example = "new_password",
            notes = "The new password to set for the user")
    private String newPassword;
}
