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

package org.dinky.data.model.rbac;

import org.dinky.mybatis.annotation.Save;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@ApiModel(value = "Menu", description = "Menu Information")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@TableName("dinky_sys_menu")
public class Menu implements Serializable {

    private static final long serialVersionUID = 8117367692336619625L;

    @TableId(type = IdType.AUTO)
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

    @TableField(fill = FieldFill.INSERT)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @ApiModelProperty(
            value = "Create Time",
            dataType = "String",
            notes = "Timestamp indicating the creation time of the menu")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @ApiModelProperty(
            value = "Update Time",
            dataType = "String",
            notes = "Timestamp indicating the last update time of the menu")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "Children", dataType = "List<Menu>", notes = "List of child menus")
    private List<Menu> children = new ArrayList<>();

    @TableField(exist = false)
    @ApiModelProperty(value = "Root Menu", dataType = "Boolean", notes = "Flag indicating if the menu is a root menu")
    private boolean rootMenu;

    public boolean isRootMenu() {
        return this.parentId == 0;
    }
}
