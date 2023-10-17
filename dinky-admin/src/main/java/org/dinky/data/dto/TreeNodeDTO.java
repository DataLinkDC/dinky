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
@ApiModel(value = "TreeNodeDTO", description = "DTO for tree nodes")
public class TreeNodeDTO {

    @ApiModelProperty(value = "ID", dataType = "Integer", example = "1", notes = "The ID of the tree node")
    private Object id;

    @ApiModelProperty(value = "Name", dataType = "String", example = "Node 1", notes = "The name of the tree node")
    private String name;

    @ApiModelProperty(value = "Path", dataType = "String", example = "/node/1", notes = "The path of the tree node")
    private String path;

    @ApiModelProperty(
            value = "Content",
            dataType = "String",
            example = "Node content",
            notes = "The content of the tree node")
    private String content;

    @ApiModelProperty(
            value = "Parent ID",
            dataType = "Integer",
            example = "0",
            notes = "The ID of the parent tree node (0 for root)")
    private Object parentId;

    @ApiModelProperty(value = "Size", dataType = "Long", example = "1024", notes = "The size of the tree node")
    private Long size;

    @ApiModelProperty(
            value = "Is Leaf",
            dataType = "boolean",
            example = "false",
            notes = "Indicates whether the tree node is a leaf node (true/false)")
    private boolean isLeaf;

    @ApiModelProperty(
            value = "Description",
            dataType = "String",
            example = "Node description",
            notes = "Additional description for the tree node")
    private String desc;

    @ApiModelProperty(value = "Children", dataType = "List<TreeNodeDTO>", notes = "List of child tree nodes")
    private List<TreeNodeDTO> children;

    public TreeNodeDTO(String name, String path, boolean isLeaf, List<TreeNodeDTO> children, Long size) {
        this.name = name;
        this.path = path;
        this.isLeaf = isLeaf;
        this.children = children;
        this.size = size;
    }

    public TreeNodeDTO(
            Integer id, String name, String path, Integer parentId, String desc, List<TreeNodeDTO> children) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.parentId = parentId;
        this.desc = desc;
        this.children = children;
        this.isLeaf = (children == null || children.size() == 0);
    }

    public TreeNodeDTO(String id, String name, String path, String parentId, String desc, List<TreeNodeDTO> children) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.parentId = parentId;
        this.desc = desc;
        this.children = children;
        this.isLeaf = (children == null || children.size() == 0);
    }
}
