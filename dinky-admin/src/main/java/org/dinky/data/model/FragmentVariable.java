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

import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * FragmentVariable
 *
 * @since 2022/8/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_fragment")
@ApiModel(value = "FragmentVariable", description = "Fragment Variable Information")
public class FragmentVariable extends SuperEntity<FragmentVariable> {

    @ApiModelProperty(value = "Tenant ID", required = true, example = "1", dataType = "Integer")
    private Integer tenantId;

    @ApiModelProperty(value = "Fragment Value", required = true, example = "exampleValue", dataType = "String")
    private String fragmentValue;

    @ApiModelProperty(value = "Note", example = "This is a note about the variable", dataType = "String")
    private String note;
}
