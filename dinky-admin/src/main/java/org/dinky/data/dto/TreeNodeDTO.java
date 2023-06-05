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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * GitProjectTreeNodeDTO
 *
 * @since 0.8.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TreeNodeDTO {
    private String name;
    private String path;
    private String content;
    private Long size;
    private boolean isLeaf;
    private List<TreeNodeDTO> children;

    public TreeNodeDTO(
            String name, String path, boolean isLeaf, List<TreeNodeDTO> children, Long size) {
        this.name = name;
        this.path = path;
        this.isLeaf = isLeaf;
        this.children = children;
        this.size = size;
    }
}
