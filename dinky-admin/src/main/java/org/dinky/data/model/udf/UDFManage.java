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

package org.dinky.data.model.udf;

import org.dinky.mybatis.model.SuperEntity;

import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_udf_manage")
@ApiModel(value = "UDFTemplate", description = "User-Defined Function Template")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UDFManage extends SuperEntity<UDFManage> {

    @ApiModelProperty(value = "Class Name", dataType = "String", notes = "Class Name")
    private String className;

    @ApiModelProperty(value = "Task Id", dataType = "Integer", notes = "Task Id")
    private Integer taskId;

    @ApiModelProperty(value = "Resources Id", dataType = "Integer", notes = "Resources Id")
    private Integer resourcesId;
}
