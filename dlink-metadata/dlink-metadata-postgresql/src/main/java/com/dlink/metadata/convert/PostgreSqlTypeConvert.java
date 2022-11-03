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

package com.dlink.metadata.convert;

import com.dlink.assertion.Asserts;
import com.dlink.model.Column;
import com.dlink.model.ColumnType;

/**
 * PostgreSqlTypeConvert
 *
 * @author wenmo
 * @since 2021/7/22 9:33
 **/
public class PostgreSqlTypeConvert implements ITypeConvert {

    @Override
    public ColumnType convert(Column column) {
        ColumnType columnType = ColumnType.STRING;
        if (Asserts.isNull(column)) {
            return columnType;
        }
        String t = column.getType().toLowerCase();
        boolean isNullable = !column.isKeyFlag() && column.isNullable();
        if (t.contains("smallint") || t.contains("int2") || t.contains("smallserial") || t.contains("serial2")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_SHORT;
            } else {
                columnType = ColumnType.SHORT;
            }
        } else if (t.contains("integer") || t.contains("int4") || t.contains("serial")) {
            if (isNullable) {
                columnType = ColumnType.INTEGER;
            } else {
                columnType = ColumnType.INT;
            }
        } else if (t.contains("bigint") || t.contains("bigserial")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_LONG;
            } else {
                columnType = ColumnType.LONG;
            }
        } else if (t.contains("real") || t.contains("float4")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_FLOAT;
            } else {
                columnType = ColumnType.FLOAT;
            }
        } else if (t.contains("float8") || t.contains("double precision")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_DOUBLE;
            } else {
                columnType = ColumnType.DOUBLE;
            }
        } else if (t.contains("numeric") || t.contains("decimal")) {
            columnType = ColumnType.DECIMAL;
        } else if (t.contains("boolean")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_BOOLEAN;
            } else {
                columnType = ColumnType.BOOLEAN;
            }
        } else if (t.contains("timestamp")) {
            columnType = ColumnType.TIMESTAMP;
        } else if (t.contains("date")) {
            columnType = ColumnType.DATE;
        } else if (t.contains("time")) {
            columnType = ColumnType.TIME;
        } else if (t.contains("char") || t.contains("text")) {
            columnType = ColumnType.STRING;
        } else if (t.contains("bytea")) {
            columnType = ColumnType.BYTES;
        } else if (t.contains("array")) {
            columnType = ColumnType.T;
        }
        return columnType;
    }

    @Override
    public String convertToDB(ColumnType columnType) {
        switch (columnType) {
            case SHORT:
            case JAVA_LANG_SHORT:
                return "int2";
            case INTEGER:
            case INT:
                return "integer";
            case LONG:
            case JAVA_LANG_LONG:
                return "bigint";
            case FLOAT:
            case JAVA_LANG_FLOAT:
                return "float4";
            case DOUBLE:
            case JAVA_LANG_DOUBLE:
                return "float8";
            case DECIMAL:
                return "decimal";
            case BOOLEAN:
            case JAVA_LANG_BOOLEAN:
                return "boolean";
            case TIMESTAMP:
                return "timestamp";
            case DATE:
                return "date";
            case TIME:
                return "time";
            case BYTES:
                return "bytea";
            case T:
                return "array";
            default:
                return "varchar";
        }
    }
}
