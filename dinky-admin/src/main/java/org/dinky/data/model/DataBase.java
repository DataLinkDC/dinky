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

import org.dinky.metadata.driver.DriverConfig;
import org.dinky.mybatis.crypto.CryptoTypeHandler;
import org.dinky.mybatis.model.SuperEntity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DataBase
 *
 * @since 2021/7/20 20:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "dinky_database", autoResultMap = true)
@ApiModel(value = "DataBase", description = "DataBase Register")
public class DataBase extends SuperEntity<DataBase> {

    private static final long serialVersionUID = -5002272138861566408L;

    @ApiModelProperty(value = "tenantId", required = true, dataType = "Integer", example = "1")
    private Integer tenantId;

    @ApiModelProperty(value = "groupName", required = true, dataType = "String", example = "source")
    private String groupName;

    @ApiModelProperty(value = "type", required = true, dataType = "String", example = "MySQL")
    private String type;

    @ApiModelProperty(value = "url", required = true, dataType = "String", example = "jdbc:mysql://localhost:3306/test")
    private String url;

    @ApiModelProperty(value = "username", required = true, dataType = "String", example = "root")
    private String username;

    @TableField(typeHandler = CryptoTypeHandler.class)
    @ApiModelProperty(value = "password", required = true, dataType = "String", example = "123456")
    private String password;

    @ApiModelProperty(value = "note", dataType = "String", example = "note")
    private String note;

    @ApiModelProperty(value = "flinkConfig", dataType = "String", example = "flinkConfig")
    private String flinkConfig;

    @ApiModelProperty(value = "flinkTemplate", dataType = "String", example = "flinkTemplate")
    private String flinkTemplate;

    @ApiModelProperty(value = "dbVersion", dataType = "String", example = "dbVersion")
    private String dbVersion;

    @ApiModelProperty(value = "status", dataType = "Boolean", example = "true")
    private Boolean status;

    @ApiModelProperty(value = "healthTime", dataType = "LocalDateTime", example = "2021-07-20 20:53:00")
    private LocalDateTime healthTime;

    @ApiModelProperty(value = "heartbeatTime", dataType = "LocalDateTime", example = "2021-07-20 20:53:00")
    private LocalDateTime heartbeatTime;

    public DriverConfig getDriverConfig() {
        return new DriverConfig(getName(), type, url, username, password);
    }
}
