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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "TreeVo", description = "The return value of the tree structure")
public class TreeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Name", required = true, example = "Create Time", dataType = "String")
    private String name;

    @ApiModelProperty(value = "Value", required = true, example = "Create Time", dataType = "String")
    private String value;

    @ApiModelProperty(value = "Children objects", notes = "List of child TreeVo objects", dataType = "List<TreeVo>")
    private List<TreeVo> children;
}
