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

package org.dinky.metadata.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * DriverType
 *
 * @since 2024/2/6
 */
public enum DriverType {
    MYSQL("MySQL"),
    ORACLE("Oracle"),
    POSTGRESQL("PostgreSql"),
    SQLSERVER("SQLServer"),
    DORIS("Doris"),
    STARROCKS("StarRocks"),
    CLICKHOUSE("ClickHouse"),
    PHOENIX("Phoenix"),
    GREENPLUM("Greenplum"),
    HIVE("Hive"),
    PRESTO("Presto");

    public final String value;

    DriverType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private static final Map<String, DriverType> MAP =
            Arrays.stream(values()).collect(Collectors.toMap(DriverType::getValue, Function.identity()));

    public static DriverType get(String value) {
        return MAP.get(value);
    }
}
