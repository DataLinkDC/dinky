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

package org.dinky.cdc.debezium;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:kindbgen@gmail.com">Kindbgen<a/>
 * @description 数据库类型
 * @date 2024/2/6
 */
public enum DataBaseType {
    MYSQL("mysql"),
    SQLSERVER("sqlserver"),
    ORACLE("oracle"),
    POSTGRESQL("postgresql");

    private String type;

    DataBaseType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    private static final Map<String, DataBaseType> MAP =
            Arrays.stream(values()).collect(Collectors.toMap(DataBaseType::getType, Function.identity()));

    public static DataBaseType get(String type) {
        return MAP.get(type);
    }
}
