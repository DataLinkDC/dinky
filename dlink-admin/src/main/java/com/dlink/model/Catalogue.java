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

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Catalogue
 *
 * @author wenmo
 * @since 2021/5/28 13:51
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_catalogue")
public class Catalogue extends SuperEntity {

    private static final long serialVersionUID = 4659379420249868394L;

    private Integer tenantId;

    private Integer taskId;

    private String type;

    private Integer parentId;

    private Boolean isLeaf;

    public Catalogue() {
    }

    public Catalogue(String name, Integer taskId, String type, Integer parentId, Boolean isLeaf) {
        this.setName(name);
        this.taskId = taskId;
        this.type = type;
        this.parentId = parentId;
        this.isLeaf = isLeaf;
    }
}
