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
import org.dinky.mybatis.model.SuperEntity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DataBase
 *
 * @since 2021/7/20 20:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_database")
public class DataBase extends SuperEntity {

    private static final long serialVersionUID = -5002272138861566408L;

    private Integer tenantId;

    private String groupName;
    private String type;
    private String url;
    private String username;
    private String password;

    private String note;

    private String flinkConfig;

    private String flinkTemplate;

    private String dbVersion;

    private Boolean status;

    private LocalDateTime healthTime;

    private LocalDateTime heartbeatTime;

    public DriverConfig getDriverConfig() {
        return new DriverConfig(getName(), type, url, username, password);
    }
}
