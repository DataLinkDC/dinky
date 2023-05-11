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

package com.zdpx.coder.graph;

import java.util.LinkedHashSet;
import java.util.Set;

import com.zdpx.coder.json.origin.Description;
import com.zdpx.coder.operator.TableInfo;

/** 过程类,相当于一个节点集合, 不参与计算图, 主要用于分组. */
public class Process {
    /** 该过程在界面上是否可以展开 */
    private boolean expanded;
    /** 版本 */
    private String version;

    /** 包含的节点包裹类 */
    private Set<OperatorWrapper> operatorWrappers = new LinkedHashSet<>();
    /** 包含的节点间连接信息 */
    private Set<Connection<TableInfo>> connects = new LinkedHashSet<>();
    /** 仓储的描述信息, 图上的注释信息 */
    private Set<Description> descriptions = new LinkedHashSet<>();

    // region getter/setter

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Set<OperatorWrapper> getOperators() {
        return operatorWrappers;
    }

    public void setOperators(Set<OperatorWrapper> operatorWrappers) {
        this.operatorWrappers = operatorWrappers;
    }

    public Set<Connection<TableInfo>> getConnects() {
        return connects;
    }

    public void setConnects(Set<Connection<TableInfo>> connects) {
        this.connects = connects;
    }

    public Set<Description> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Set<Description> descriptions) {
        this.descriptions = descriptions;
    }

    // endregion
}
