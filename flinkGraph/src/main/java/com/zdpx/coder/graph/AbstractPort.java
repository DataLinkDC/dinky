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

/**
 * 节点接口(输入/输出)抽象类, 实现接口的通用功能
 *
 * @author Licho Sun
 * @param <T> 表示接口包含的数据类型
 */
public class AbstractPort<T extends PseudoData<T>> implements Port<T> {
    /** 端口名称 */
    protected String name;

    /** 端口相关的连接信息 */
    protected Connection<T> connection;

    /** 端口所在节点 */
    protected Operator parent;

    public AbstractPort(Operator parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    // region g/s
    @Override
    public Connection<T> getConnection() {
        return connection;
    }

    @Override
    public void setConnection(Connection<T> value) {
        this.connection = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Operator getParent() {
        return parent;
    }

    public void setParent(Operator parent) {
        this.parent = parent;
    }
    // endregion
}
