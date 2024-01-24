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

package org.dinky.metadata.convert;

import org.dinky.data.enums.ColumnType;

/**
 * PostgreSqlTypeConvert
 *
 * @since 2021/7/22 9:33
 */
public class PostgreSqlTypeConvert extends AbstractJdbcTypeConvert {

    public PostgreSqlTypeConvert() {
        this.convertMap.clear();
        register("smallint", ColumnType.INT, ColumnType.INTEGER);
        register("int2", ColumnType.SHORT, ColumnType.JAVA_LANG_SHORT);
        register("smallserial", ColumnType.SHORT, ColumnType.JAVA_LANG_SHORT);
        register("serial2", ColumnType.SHORT, ColumnType.JAVA_LANG_SHORT);
        register("integer", ColumnType.INT, ColumnType.INTEGER);
        register("int4", ColumnType.INT, ColumnType.INTEGER);
        register("serial", ColumnType.INT, ColumnType.INTEGER);
        register("bigint", ColumnType.LONG, ColumnType.JAVA_LANG_LONG);
        register("int8", ColumnType.LONG, ColumnType.JAVA_LANG_LONG);
        register("bigserial", ColumnType.LONG, ColumnType.JAVA_LANG_LONG);
        register("real", ColumnType.FLOAT, ColumnType.JAVA_LANG_FLOAT);
        register("float4", ColumnType.FLOAT, ColumnType.JAVA_LANG_FLOAT);
        register("float8", ColumnType.DOUBLE, ColumnType.JAVA_LANG_DOUBLE);
        register("double precision", ColumnType.DOUBLE, ColumnType.JAVA_LANG_DOUBLE);
        register("numeric", ColumnType.DECIMAL);
        register("decimal", ColumnType.DECIMAL);
        register("boolean", ColumnType.BOOLEAN, ColumnType.JAVA_LANG_BOOLEAN);
        register("bool", ColumnType.BOOLEAN, ColumnType.JAVA_LANG_BOOLEAN);
        register("timestamp", ColumnType.TIMESTAMP);
        register("date", ColumnType.DATE);
        register("time", ColumnType.TIME);
        register("char", ColumnType.STRING);
        register("text", ColumnType.STRING);
        register("bytea", ColumnType.BYTES);
        register("jsonb", ColumnType.STRING);
        register("json", ColumnType.STRING);
    }
}
