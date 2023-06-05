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
 * OracleTypeConvert
 *
 * @since 2021/7/21 16:00
 */
public class OracleTypeConvert implements ITypeConvert {

    @Override
    public ColumnType convert(Column column) {
        ColumnType columnType = ColumnType.STRING;
        if (Asserts.isNull(column)) {
            return columnType;
        }
        String t = column.getType().toLowerCase();
        boolean isNullable = !column.isKeyFlag() && column.isNullable();
        if (t.contains("char")) {
            columnType = ColumnType.STRING;
        } else if (t.contains("date")) {
            columnType = ColumnType.LOCAL_DATETIME;
        } else if (t.contains("timestamp")) {
            columnType = ColumnType.TIMESTAMP;
        } else if (t.contains("number")) {
            if (t.matches("number\\(+\\d\\)")) {
                if (isNullable) {
                    columnType = ColumnType.INTEGER;
                } else {
                    columnType = ColumnType.INT;
                }
            } else if (t.matches("number\\(+\\d{2}+\\)")) {
                if (isNullable) {
                    columnType = ColumnType.JAVA_LANG_LONG;
                } else {
                    columnType = ColumnType.LONG;
                }
            } else {
                columnType = ColumnType.DECIMAL;
            }
        } else if (t.contains("float")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_FLOAT;
            } else {
                columnType = ColumnType.FLOAT;
            }
        } else if (t.contains("clob")) {
            columnType = ColumnType.STRING;
        } else if (t.contains("blob")) {
            columnType = ColumnType.BYTES;
        }
        return columnType;
    }

    @Override
    public String convertToDB(ColumnType columnType) {
        switch (columnType) {
            case STRING:
                return "varchar";
            case DATE:
                return "date";
            case TIMESTAMP:
                return "timestamp";
            case INTEGER:
            case INT:
            case LONG:
            case JAVA_LANG_LONG:
            case DECIMAL:
                return "number";
            case FLOAT:
            case JAVA_LANG_FLOAT:
                return "float";
            case BYTES:
                return "blob";
            default:
                return "varchar";
        }
    }
}
