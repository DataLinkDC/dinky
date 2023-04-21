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
 * Operators in a process are connected via input and output ports. Whenever an operator generates
 * output (or metadata about output), it is delivered to the connected input port.
 *
 * <p>This interface defines all behavior and properties common to input and output ports. This is
 * basically names, description etc., as well as adding messages about problems in the process setup
 * and quick fixes.
 *
 * @author Licho Sun
 * @param <T> the type that port can contains.
 */
public interface Port<T extends PseudoData<T>> {
    /**
     * get port name
     *
     * @return port name, the name is not null
     */
    String getName();

    /**
     * set port name
     *
     * @param name the name format according to java variable name.
     */
    void setName(String name);

    /**
     * get operator that port in
     *
     * @return operator that port in
     */
    Operator getParent();

    /**
     * set operator that port in
     *
     * @param parent operator that port in, not null
     */
    void setParent(Operator parent);

    /**
     * get {@link Connection connection} between ports.
     *
     * @return {@link Connection connection} between ports.
     */
    Connection<T> getConnection();

    /**
     * set {@link Connection connection} between ports.
     *
     * @param value {@link Connection connection} between ports.
     */
    void setConnection(Connection<T> value);
}
