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

import com.zdpx.coder.operator.Operator;
import com.zdpx.coder.utils.Preconditions;

/**
 * 输入端口
 *
 * @author Licho Sun
 */
public class InputPortObject<T extends PseudoData<T>> extends AbstractPort<T>
        implements InputPort<T> {

    /** 接口可接受类型 */
    T pseudoData;

    public InputPortObject(Operator parent, String name) {
        super(parent, name);
        parent.getInputPorts().put(name, this);
    }

    @Override
    public T getPseudoData() {
        return pseudoData;
    }

    @Override
    public void setPseudoData(T value) {
        this.pseudoData = value;
    }

    public OutputPort<T> getOutputPort() {
        Connection<T> connection = getConnection();
        Preconditions.checkNotNull(
                connection,
                String.format("Operator %s input not connection.", this.getParent().getName()));
        return getConnection().getFromPort();
    }

    @Override
    public void setConnection(Connection<T> value) {
        super.setConnection(value);
        value.setToPort(this);
    }

    /**
     * 获取连接另一节点的输出数据信息
     *
     * @return 连接另一节点的输出数据信息
     */
    public T getOutputPseudoData() {
        OutputPort<T> fromPort = getOutputPort();
        Preconditions.checkNotNull(
                fromPort,
                String.format("Operator %String input can not get sender port.", this.getName()));
        return fromPort.getPseudoData();
    }
}
