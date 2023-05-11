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

package com.zdpx.coder.json.origin;

import java.util.LinkedHashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** */
public class ProcessNode {
    private boolean expanded;
    private String version;
    private Set<OperatorNode> operators = new LinkedHashSet<>();
    private Set<ConnectionNode> connects = new LinkedHashSet<>();
    private Set<DescriptionNode> descriptions = new LinkedHashSet<>();
    @JsonIgnore private OperatorNode parent;
    // region getter/setter

    public OperatorNode getParent() {
        return parent;
    }

    public void setParent(OperatorNode parent) {
        this.parent = parent;
    }

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

    public Set<OperatorNode> getOperators() {
        return operators;
    }

    public void setOperators(Set<OperatorNode> operators) {
        this.operators = operators;
    }

    public Set<ConnectionNode> getConnects() {
        return connects;
    }

    public void setConnects(Set<ConnectionNode> connects) {
        this.connects = connects;
    }

    public Set<DescriptionNode> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Set<DescriptionNode> descriptions) {
        this.descriptions = descriptions;
    }

    // endregion
}
