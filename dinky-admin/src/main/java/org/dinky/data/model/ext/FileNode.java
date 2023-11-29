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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FileNode
 *
 * @since 2022/10/15 18:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "FileNode", description = "File Node Information")
public class FileNode {

    @ApiModelProperty(
            value = "Name of the file or directory",
            required = true,
            example = "example.txt",
            dataType = "String")
    private String name;

    @ApiModelProperty(value = "Indicates if it is a directory", required = true, example = "true", dataType = "boolean")
    private boolean isDir;

    @ApiModelProperty(value = "Size of the file in bytes", required = true, example = "1024", dataType = "long")
    private long size;

    @ApiModelProperty(
            value = "Path to the file or directory",
            required = true,
            example = "/path/to/file",
            dataType = "String")
    private String path;
}
