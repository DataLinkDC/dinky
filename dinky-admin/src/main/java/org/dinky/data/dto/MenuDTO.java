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

import org.dinky.data.model.rbac.Menu;
import org.dinky.mybatis.annotation.Save;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MenuDTO
 *
 * @since 2023/10/23 15:23
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "MenuDTO", description = "API Menu Data Transfer Object")
public class MenuDTO {

    @ApiModelProperty(value = "ID", dataType = "Integer", example = "1", notes = "Unique identifier for the menu")
    private Integer id;

    @ApiModelProperty(value = "Parent ID", dataType = "Integer", example = "0", notes = "ID of the parent menu")
    private Integer parentId;

    @NotNull(
            message = "Name cannot be null",
            groups = {Save.class})
    @ApiModelProperty(value = "Name", dataType = "String", example = "Home", notes = "Name of the menu")
    private String name;

    @NotNull(
            message = "Path cannot be null",
            groups = {Save.class})
    @ApiModelProperty(value = "Path", dataType = "String", example = "/home", notes = "Path associated with the menu")
    private String path;

    @NotNull(
            message = "Component cannot be null",
            groups = {Save.class})
    @ApiModelProperty(
            value = "Component",
            dataType = "String",
            example = "HomeComponent",
            notes = "Component associated with the menu")
    private String component;

    @NotNull(
            message = "Permissions cannot be null",
            groups = {Save.class})
    @ApiModelProperty(
            value = "Permissions",
            dataType = "String",
            example = "menu:home:view",
            notes = "Permissions required to access the menu")
    private String perms;

    @ApiModelProperty(value = "Icon", dataType = "String", notes = "Icon associated with the menu")
    private String icon;

    @ApiModelProperty(value = "Type", dataType = "String", notes = "Type of the menu")
    private String type;

    @ApiModelProperty(
            value = "Order Number",
            dataType = "Double",
            example = "1.0",
            notes = "Order number for menu positioning")
    private Double orderNum;

    @ApiModelProperty(value = "Display", dataType = "Boolean", notes = "Flag indicating if the menu is displayed")
    private boolean display;

    @ApiModelProperty(value = "Note", dataType = "String", notes = "Additional notes or details about the menu")
    private String note;

    @ApiModelProperty(value = "Children", dataType = "List<Menu>", notes = "List of child menus")
    private List<Menu> children = new ArrayList<>();

    @ApiModelProperty(value = "Root Menu", dataType = "Boolean", notes = "Flag indicating if the menu is a root menu")
    private boolean rootMenu;

    public Menu toBean() {
        Menu menu = new Menu();
        BeanUtil.copyProperties(this, menu);
        return menu;
    }
}
