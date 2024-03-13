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

public class HiveTypeConvert extends AbstractJdbcTypeConvert {

    public HiveTypeConvert() {
        this.convertMap.clear();
        register("char", ColumnType.STRING);
        register("boolean", ColumnType.BOOLEAN, ColumnType.JAVA_LANG_BOOLEAN);
        register("tinyint", ColumnType.BYTE, ColumnType.JAVA_LANG_BYTE);
        register("smallint", ColumnType.SHORT, ColumnType.JAVA_LANG_SHORT);
        register("bigint", ColumnType.LONG, ColumnType.JAVA_LANG_LONG);
        register("largeint", ColumnType.STRING);
        register("int", ColumnType.INT, ColumnType.INTEGER);
        register("float", ColumnType.FLOAT, ColumnType.JAVA_LANG_FLOAT);
        register("double", ColumnType.DOUBLE, ColumnType.JAVA_LANG_DOUBLE);
        register("timestamp", ColumnType.TIMESTAMP);
        register("date", ColumnType.STRING);
        register("datetime", ColumnType.STRING);
        register("decimal", ColumnType.DECIMAL);
        register("time", ColumnType.DOUBLE, ColumnType.JAVA_LANG_DOUBLE);
    }

    @Override
    public String convertToDB(ColumnType columnType) {
        switch (columnType) {
            case STRING:
                return "varchar";
            case BOOLEAN:
            case JAVA_LANG_BOOLEAN:
                return "boolean";
            case BYTE:
            case JAVA_LANG_BYTE:
                return "tinyint";
            case SHORT:
            case JAVA_LANG_SHORT:
                return "smallint";
            case LONG:
            case JAVA_LANG_LONG:
                return "bigint";
            case FLOAT:
            case JAVA_LANG_FLOAT:
                return "float";
            case DOUBLE:
            case JAVA_LANG_DOUBLE:
                return "double";
            case DECIMAL:
                return "decimal";
            case INT:
            case INTEGER:
                return "int";
            case TIMESTAMP:
                return "timestamp";
            default:
                return "varchar";
        }
    }
}
