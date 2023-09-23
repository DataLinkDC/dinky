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
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AlertGroup
 *
 * @since 2022/2/24 19:58
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_udf_template")
@ApiModel(value = "UDFTemplate", description = "User-Defined Function Template")
public class UDFTemplate extends SuperEntity {

    @ApiModelProperty(value = "Code Type", dataType = "String", notes = "Type of the code")
    private String codeType;

    @ApiModelProperty(value = "Function Type", dataType = "String", notes = "Type of the function")
    private String functionType;

    @ApiModelProperty(value = "Template Code", dataType = "String", notes = "Code template")
    private String templateCode;
}
