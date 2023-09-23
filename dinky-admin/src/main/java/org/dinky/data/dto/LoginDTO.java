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
 * LoginUTO
 *
 * @since 2021/11/28 17:02
 */
@Getter
@Setter
@ApiModel(value = "LoginDTO", description = "Login DTO")
public class LoginDTO {

    @ApiModelProperty(value = "username", required = true, example = "admin", dataType = "String")
    private String username;

    @ApiModelProperty(value = "password", required = true, example = "admin", dataType = "String")
    private String password;

    @ApiModelProperty(value = "tenantId", required = true, example = "1", dataType = "Integer")
    private Integer tenantId;

    @ApiModelProperty(value = "autoLogin", required = true, example = "true", dataType = "Boolean")
    private boolean autoLogin;

    @ApiModelProperty(value = "ldapLogin", required = true, example = "false", dataType = "Boolean")
    private boolean ldapLogin;
}
