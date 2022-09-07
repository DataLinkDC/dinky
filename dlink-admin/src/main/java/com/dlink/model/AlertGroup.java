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

package com.dlink.model;

import com.dlink.db.model.SuperEntity;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AlertGroup
 *
 * @author wenmo
 * @since 2022/2/24 19:58
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_alert_group")
public class AlertGroup extends SuperEntity {

    private static final long serialVersionUID = 7027411164191682344L;

    private Integer tenantId;

    private String alertInstanceIds;

    private String note;

    @TableField(exist = false)
    private List<AlertInstance> instances;
}
