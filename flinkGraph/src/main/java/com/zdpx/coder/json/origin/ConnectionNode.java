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

import com.fasterxml.jackson.annotation.JsonIgnore;

/** */
public class ConnectionNode {
    private String fromOp;
    private String fromPort;
    private String toOp;
    private String toPort;

    @JsonIgnore private OperatorNode from;
    @JsonIgnore private OperatorNode to;

    // region getter/setter

    public OperatorNode getFrom() {
        return from;
    }

    public void setFrom(OperatorNode from) {
        this.from = from;
    }

    public OperatorNode getTo() {
        return to;
    }

    public void setTo(OperatorNode to) {
        this.to = to;
    }

    public String getFromOp() {
        return fromOp;
    }

    public void setFromOp(String fromOp) {
        this.fromOp = fromOp;
    }

    public String getFromPort() {
        return fromPort;
    }

    public void setFromPort(String fromPort) {
        this.fromPort = fromPort;
    }

    public String getToOp() {
        return toOp;
    }

    public void setToOp(String toOp) {
        this.toOp = toOp;
    }

    public String getToPort() {
        return toPort;
    }

    public void setToPort(String toPort) {
        this.toPort = toPort;
    }

    // endregion
}
