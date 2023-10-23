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

package org.dinky.data.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "JarClassesDTO", description = "DTO for Jar classes information")
public class JarClassesDTO {

    @ApiModelProperty(
            value = "Jar File Path",
            dataType = "String",
            example = "/path/to/my.jar",
            notes = "The path to the Jar file")
    private String jarPath;

    @ApiModelProperty(
            value = "List of Classes",
            dataType = "List<String>",
            example = "[\"com.example.Class1\", \"com.example.Class2\"]",
            notes = "A list of class names in the Jar file")
    private List<String> classList;
}
