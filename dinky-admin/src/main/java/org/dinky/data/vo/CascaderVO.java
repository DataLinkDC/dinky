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

package org.dinky.data.vo;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ApiModel(value = "CascaderVO", description = "Cascader Value Object")
public class CascaderVO implements Serializable {

    private static final long serialVersionUID = 1L; // 添加serialVersionUID字段

    @ApiModelProperty(value = "Value of the option", required = true, example = "1", dataType = "String")
    private String value;

    @ApiModelProperty(value = "Label of the option", required = true, example = "Option 1", dataType = "String")
    private String label;

    @ApiModelProperty(
            value = "Children options",
            notes = "List of child CascaderVO objects",
            dataType = "List<CascaderVO>")
    private List<CascaderVO> children;

    public CascaderVO() {}

    public CascaderVO(String label) {
        this.label = label;
        this.value = label;
    }

    public CascaderVO(String label, List<CascaderVO> children) {
        this.label = label;
        this.value = label;
        this.children = children;
    }
}
