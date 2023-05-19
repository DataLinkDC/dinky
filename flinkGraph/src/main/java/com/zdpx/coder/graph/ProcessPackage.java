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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.zdpx.coder.operator.Operator;
import com.zdpx.coder.operator.TableInfo;

/** 过程类,相当于一个节点集合, 不参与计算图, 主要用于分组. */
public class ProcessPackage extends Node {
    /** 该过程在界面上是否可以展开 */
    private boolean expanded;

    // region getter/setter

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public Set<Operator> getOperators() {
        return getNodeWrapper().getChildren().stream()
                .filter(Operator.class::isInstance)
                .map(t -> (Operator) t)
                .collect(Collectors.toSet());
    }

    public Set<ProcessPackage> getProcessPackages() {
        return getNodeWrapper().getChildren().stream()
                .filter(ProcessPackage.class::isInstance)
                .map(t -> (ProcessPackage) t)
                .collect(Collectors.toSet());
    }

    public void setChildren(Set<Node> originOperatorWrappers) {
        getNodeWrapper().setChildren(new ArrayList<>(originOperatorWrappers));
    }

    public void addOperator(Operator operator) {
        getNodeWrapper().getChildren().add(operator);
    }

    public List<Node> getChildren() {
        return getNodeWrapper().getChildren();
    }

    public List<Connection> getConnects() {
        return getNodeWrapper().getChildren().stream()
                .filter(Connection.class::isInstance)
                .map(t -> (Connection) t)
                .collect(Collectors.toList());
    }

    // endregion
}
