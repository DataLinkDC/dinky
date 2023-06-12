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

public class StarRocksTypeConvert extends AbstractTypeConvert {

    public StarRocksTypeConvert() {
        this.convertMap.clear();
        this.convertMap.put("char", (c, d) -> getColumnType(c, ColumnType.STRING));
        this.convertMap.put(
                "boolean",
                (c, d) -> getColumnType(c, ColumnType.BOOLEAN, ColumnType.JAVA_LANG_BOOLEAN));
        this.convertMap.put(
                "tinyint", (c, d) -> getColumnType(c, ColumnType.BYTE, ColumnType.JAVA_LANG_BYTE));
        this.convertMap.put(
                "smallint",
                (c, d) -> getColumnType(c, ColumnType.SHORT, ColumnType.JAVA_LANG_SHORT));
        this.convertMap.put(
                "bigint", (c, d) -> getColumnType(c, ColumnType.LONG, ColumnType.JAVA_LANG_LONG));
        this.convertMap.put("largeint", (c, d) -> getColumnType(c, ColumnType.STRING));
        this.convertMap.put("int", (c, d) -> getColumnType(c, ColumnType.INT, ColumnType.INTEGER));
        this.convertMap.put(
                "float", (c, d) -> getColumnType(c, ColumnType.FLOAT, ColumnType.JAVA_LANG_FLOAT));
        this.convertMap.put(
                "double",
                (c, d) -> getColumnType(c, ColumnType.DOUBLE, ColumnType.JAVA_LANG_DOUBLE));
        this.convertMap.put("date", (c, d) -> getColumnType(c, ColumnType.STRING));
        this.convertMap.put("datetime", (c, d) -> getColumnType(c, ColumnType.STRING));
        this.convertMap.put("decimal", (c, d) -> getColumnType(c, ColumnType.DECIMAL));
        this.convertMap.put(
                "time", (c, d) -> getColumnType(c, ColumnType.DOUBLE, ColumnType.JAVA_LANG_DOUBLE));
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
            default:
                return "varchar";
        }
    }
}
