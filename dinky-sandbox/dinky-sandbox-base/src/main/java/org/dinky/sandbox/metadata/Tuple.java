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

package org.dinky.sandbox.metadata;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * 不可变数组类型（元组），用于多值返回<br>
 * 多值可以支持每个元素值类型不同
 *
 */
public class Tuple implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Object[] members;

    /**
     * 构造
     *
     * @param members 成员数组
     */
    public Tuple(Object... members) {
        this.members = members;
    }

    /**
     * 获取指定位置元素
     *
     * @param <T>   返回对象类型
     * @param index 位置
     * @return 元素
     */
    @SuppressWarnings("unchecked")
    public <T> T get(int index) {
        return (T) members[index];
    }

    /**
     * 获得所有元素
     *
     * @return 获得所有元素
     */
    public Object[] getMembers() {
        return this.members;
    }

    /**
     * 将元组转换成列表
     *
     * @return 转换得到的列表
     */
    public final List<Object> toList() {
        return Arrays.asList(this.members);
    }

    /**
     * 得到元组的大小
     *
     * @return 元组的大小
     */
    public int size() {
        return this.members.length;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(members);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Tuple other = (Tuple) obj;
        return false != Arrays.deepEquals(members, other.members);
    }

    @Override
    public String toString() {
        return Arrays.toString(members);
    }
}
