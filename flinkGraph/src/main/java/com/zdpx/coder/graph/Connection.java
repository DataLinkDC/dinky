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

import com.zdpx.coder.operator.TableInfo;

/**
 * 表示节点间的连接关系,{@link OutputPort <T> fromPort} 表示源节点的输出端口, {@link InputPort <T> toPort} 表示目的节点的输入端口,
 * 并在此类注入转换功能.
 *
 * @param <T> 目前类型统一为{@link TableInfo TableInfo}, 将来可以为不一致类型,
 * @author Licho Sun
 */
public class Connection<T extends PseudoData<T>> extends Node {

    /** 源端口 */
    private OutputPort<T> fromPort;
    /** 目标端口 */
    private InputPort<T> toPort;

    // region g/s

    public OutputPort<T> getFromPort() {
        return fromPort;
    }

    public void setFromPort(OutputPort<T> fromPort) {
        this.fromPort = fromPort;
        if (fromPort.getConnection() != this) {
            fromPort.setConnection(this);
        }
    }

    public InputPort<T> getToPort() {
        return toPort;
    }

    public void setToPort(InputPort<T> toPort) {
        this.toPort = toPort;
        if (toPort.getConnection() != this) {
            toPort.setConnection(this);
        }
    }

    // endregion
}
