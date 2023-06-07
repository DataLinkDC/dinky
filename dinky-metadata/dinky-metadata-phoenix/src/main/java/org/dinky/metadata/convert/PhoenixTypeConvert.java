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

public class PhoenixTypeConvert implements ITypeConvert {

    @Override
    public ColumnType convert(Column column) {
        ColumnType columnType = ColumnType.STRING;
        if (Asserts.isNull(column)) {
            return columnType;
        }
        String t = column.getType().toLowerCase();
        boolean isNullable = !column.isKeyFlag() && column.isNullable();
        if (t.contains("char")
                || t.contains("varchar")
                || t.contains("text")
                || t.contains("nchar")
                || t.contains("nvarchar")
                || t.contains("ntext")
                || t.contains("uniqueidentifier")
                || t.contains("sql_variant")) {
            columnType = ColumnType.STRING;
        } else if (t.contains("bigint")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_LONG;
            } else {
                columnType = ColumnType.LONG;
            }
        } else if (t.contains("int")
                || t.contains("tinyint")
                || t.contains("smallint")
                || t.contains("integer")) {
            if (isNullable) {
                columnType = ColumnType.INTEGER;
            } else {
                columnType = ColumnType.INT;
            }
        } else if (t.contains("float")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_FLOAT;
            } else {
                columnType = ColumnType.FLOAT;
            }
        } else if (t.contains("decimal")
                || t.contains("money")
                || t.contains("smallmoney")
                || t.contains("numeric")) {
            columnType = ColumnType.DECIMAL;
        } else if (t.contains("double")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_DOUBLE;
            } else {
                columnType = ColumnType.DOUBLE;
            }
        } else if (t.contains("boolean")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_BOOLEAN;
            } else {
                columnType = ColumnType.BOOLEAN;
            }
        } else if (t.contains("smalldatetime") || t.contains("datetime")) {
            columnType = ColumnType.TIMESTAMP;
        } else if (t.contains("timestamp")
                || t.contains("binary")
                || t.contains("varbinary")
                || t.contains("image")) {
            columnType = ColumnType.BYTES;
        } else if (t.contains("time")) {
            columnType = ColumnType.TIME;
        } else if (t.contains("date")) {
            columnType = ColumnType.DATE;
        }
        return columnType;
    }

    @Override
    public String convertToDB(ColumnType columnType) {
        switch (columnType) {
            case INTEGER:
            case INT:
                return "integer";
            case DOUBLE:
            case JAVA_LANG_DOUBLE:
                return "double";
            case LONG:
            case JAVA_LANG_LONG:
                return "bigint";
            case FLOAT:
            case JAVA_LANG_FLOAT:
                return "float";
            case DECIMAL:
                return "decimal";
            case BOOLEAN:
            case JAVA_LANG_BOOLEAN:
                return "boolean";
            case TIME:
                return "time";
            case DATE:
                return "date";
            case TIMESTAMP:
                return "timestamp";
            case STRING:
                return "varchar";
            case BYTES:
                return "binary";
            default:
                return "varchar";
        }
    }
}
