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

import org.dinky.mybatis.model.SuperEntity;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Catalogue
 *
 * @since 2021/5/28 13:51
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_catalogue")
public class Catalogue extends SuperEntity<Catalogue> {

    private static final long serialVersionUID = 4659379420249868394L;

    @ApiModelProperty(value = "Tenant ID", required = true, dataType = "Integer", example = "1")
    private Integer tenantId;

    @ApiModelProperty(value = "Task ID", required = true, dataType = "Integer", example = "1")
    private Integer taskId;

    @ApiModelProperty(value = "Type", required = true, dataType = "String", example = "Flinksql")
    private String type;

    @ApiModelProperty(value = "Parent ID", required = true, dataType = "Integer", example = "1")
    private Integer parentId;

    @ApiModelProperty(value = "Is Leaf", required = true, dataType = "Boolean", example = "true")
    private Boolean isLeaf;

    @TableField(exist = false)
    @ApiModelProperty(value = "Children", required = true, dataType = "List<Catalogue>", example = "[]")
    private List<Catalogue> children = new ArrayList<>();

    @TableField(exist = false)
    private Task task;

    @TableField(exist = false)
    private String note;

    public Catalogue() {}

    public void setTaskAndNote(Task task) {
        this.task = task;
        this.note = task.getNote();
    }

    public Catalogue(String name, Integer taskId, String type, Integer parentId, Boolean isLeaf) {
        this.setName(name);
        this.taskId = taskId;
        this.type = type;
        this.parentId = parentId;
        this.isLeaf = isLeaf;
    }
}
