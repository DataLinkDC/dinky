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

import org.dinky.assertion.Asserts;
import org.dinky.data.enums.ColumnType;
import org.dinky.data.model.Column;

/**
 * ClickHouseTypeConvert
 *
 * @since 2021/7/21 17:15
 */
public class ClickHouseTypeConvert extends AbstractJdbcTypeConvert {

    @Override
    public ColumnType convert(Column column) {
        if (Asserts.isNull(column)) {
            return ColumnType.STRING;
        }
        String type = column.getType();
        if (Asserts.isNull(type)) {
            return ColumnType.STRING;
        }
        type = type.replaceAll("Nullable\\(", "").replaceAll("\\)", "");

        if (type.startsWith("Array")) {
            return ColumnType.T;
        }
        if (type.startsWith("Map")) {
            return ColumnType.MAP;
        }
        if (type.startsWith("UInt") || type.startsWith("Int")) {
            return column.isNullable() ? ColumnType.JAVA_LANG_LONG : ColumnType.LONG;
        }
        if (type.startsWith("Float")) {
            return column.isNullable() ? ColumnType.JAVA_LANG_DOUBLE : ColumnType.DOUBLE;
        }
        if (type.startsWith("Boolean")) {
            return column.isNullable() ? ColumnType.JAVA_LANG_BOOLEAN : ColumnType.BOOLEAN;
        }
        if (type.startsWith("String") || type.startsWith("FixedString")) {
            return ColumnType.STRING;
        }
        if (type.startsWith("DateTime")) {
            return ColumnType.TIME;
        }
        if (type.startsWith("Date")) {
            return ColumnType.DATE;
        }

        // other type default String
        return ColumnType.STRING;
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
