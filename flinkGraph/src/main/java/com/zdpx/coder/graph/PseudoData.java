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

/**
 * 表示在节点间传输数据的基类型, {@link #getType()} 用于获取类型的{@link DataType}枚举表示.
 *
 * @param <T> 自表示模式.
 * @author Licho Sun
 */
public interface PseudoData<T extends PseudoData<T>> {
    /**
     * 获取数据类型表示
     *
     * @return dataType
     */
    DataType getType();
}
