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

package org.dinky.data.model.ext;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel(value = "TaskUdfConfig", description = "UDF (User-Defined Function) Configuration for Task")
@AllArgsConstructor
@NoArgsConstructor
public class TaskUdfConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Template ID", dataType = "Integer", example = "1", notes = "ID of the UDF template")
    private Integer templateId;

    @ApiModelProperty(value = "Select Keys", dataType = "List<Object>", notes = "List of select keys")
    private List<Object> selectKeys;

    @ApiModelProperty(value = "Class Name", dataType = "String", notes = "Name of the UDF class")
    private String className;
}
