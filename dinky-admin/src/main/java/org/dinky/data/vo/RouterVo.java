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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 路由配置信息
 *
 * @author ruoyi
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel(value = "RouterVo", description = "Router Value Object")
public class RouterVo {

    @ApiModelProperty(value = "Route name", dataType = "String", notes = "Name of the route.")
    private String name;

    @ApiModelProperty(value = "Route path", dataType = "String", notes = "Path of the route.")
    private String path;

    @ApiModelProperty(
            value = "Hide route",
            dataType = "boolean",
            notes = "Set to true to hide the route in the sidebar.",
            example = "false")
    private boolean hidden;

    @ApiModelProperty(
            value = "Redirect path",
            dataType = "String",
            notes =
                    "Redirect path for the route. If 'noRedirect' is set, the route cannot be clicked in breadcrumb navigation.")
    private String redirect;

    @ApiModelProperty(value = "Component path", dataType = "String", notes = "Path to the route's component.")
    private String component;

    @ApiModelProperty(
            value = "Always show sub-routes",
            dataType = "Boolean",
            notes =
                    "Automatically becomes nested mode when more than one route is declared under a route, such as a component page.",
            example = "true")
    private Boolean alwaysShow;

    @ApiModelProperty(value = "Metadata for the route", dataType = "MetaVo", notes = "Metadata for the route.")
    private MetaVo meta;

    @ApiModelProperty(value = "Child routes", dataType = "List<RouterVo>", notes = "Child routes of the route.")
    private List<RouterVo> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean getHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public Boolean getAlwaysShow() {
        return alwaysShow;
    }

    public void setAlwaysShow(Boolean alwaysShow) {
        this.alwaysShow = alwaysShow;
    }

    public MetaVo getMeta() {
        return meta;
    }

    public void setMeta(MetaVo meta) {
        this.meta = meta;
    }

    public List<RouterVo> getChildren() {
        return children;
    }

    public void setChildren(List<RouterVo> children) {
        this.children = children;
    }
}
