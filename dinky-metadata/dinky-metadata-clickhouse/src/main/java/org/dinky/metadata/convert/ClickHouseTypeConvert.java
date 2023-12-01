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
 * ClickHouseTypeConvert
 *
 * @since 2021/7/21 17:15
 */
public class ClickHouseTypeConvert extends AbstractJdbcTypeConvert {

    // Use mysql now,and welcome to fix it.
    public ClickHouseTypeConvert() {
        this.convertMap.clear();
        register("tinyint", ColumnType.BYTE);
        register("smallint", ColumnType.SHORT, ColumnType.JAVA_LANG_SHORT);
        register("bigint unsigned", ColumnType.DECIMAL);
        register("numeric", ColumnType.DECIMAL);
        register("decimal", ColumnType.DECIMAL);
        register("bigint", ColumnType.LONG, ColumnType.JAVA_LANG_LONG);
        register("int unsigned", ColumnType.LONG);
        register("float", ColumnType.FLOAT, ColumnType.JAVA_LANG_FLOAT);
        register("double", ColumnType.DOUBLE, ColumnType.JAVA_LANG_DOUBLE);
        register("boolean", ColumnType.BOOLEAN, ColumnType.JAVA_LANG_BOOLEAN);
        register("tinyint(1)", ColumnType.BOOLEAN, ColumnType.JAVA_LANG_BOOLEAN);
        register("datetime", ColumnType.TIMESTAMP);
        register("date", ColumnType.DATE);
        register("time", ColumnType.TIME);
        register("char", ColumnType.STRING);
        register("text", ColumnType.STRING);
        register("binary", ColumnType.BYTES);
        register("blob", ColumnType.BYTES);
        register("int", ColumnType.INT, ColumnType.INTEGER);
        register("mediumint", ColumnType.INT, ColumnType.INTEGER);
        register("smallint unsigned", ColumnType.INT, ColumnType.INTEGER);
    }

    @Override
    public String convertToDB(ColumnType columnType) {
        switch (columnType) {
            case STRING:
                return "varchar";
            case BYTE:
                return "tinyint";
            case SHORT:
            case JAVA_LANG_SHORT:
                return "smallint";
            case DECIMAL:
                return "decimal";
            case LONG:
            case JAVA_LANG_LONG:
                return "bigint";
            case FLOAT:
            case JAVA_LANG_FLOAT:
                return "float";
            case DOUBLE:
            case JAVA_LANG_DOUBLE:
                return "double";
            case BOOLEAN:
            case JAVA_LANG_BOOLEAN:
                return "boolean";
            case TIMESTAMP:
                return "datetime";
            case DATE:
                return "date";
            case TIME:
                return "time";
            case BYTES:
                return "binary";
            case INTEGER:
            case INT:
                return "int";
            default:
                return "varchar";
        }
    }
}
