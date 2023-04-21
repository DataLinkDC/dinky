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
 * 数据类型枚举类, 目前只使用{@link #TABLE} 类型, 表示表结构抽象类型, 对应于{@link TableInfo TableInfo}
 *
 * @author Licho Sun
 */
public enum DataType {
    /** 表示表结构抽象类型, 对应于{@link TableInfo TableInfo} */
    TABLE,
    /** 可以直接传脚本,比如groovy,目前未使用 */
    CODE;
}
