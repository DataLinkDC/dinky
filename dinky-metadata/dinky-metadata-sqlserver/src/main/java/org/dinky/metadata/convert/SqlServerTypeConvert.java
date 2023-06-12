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

public class SqlServerTypeConvert extends AbstractTypeConvert {

    public SqlServerTypeConvert() {
        this.convertMap.clear();
        this.convertMap.put("char", (c, d) -> getColumnType(c, ColumnType.STRING));
        this.convertMap.put("varchar", (c, d) -> getColumnType(c, ColumnType.STRING));
        this.convertMap.put("text", (c, d) -> getColumnType(c, ColumnType.STRING));
        this.convertMap.put("nchar", (c, d) -> getColumnType(c, ColumnType.STRING));
        this.convertMap.put("nvarchar", (c, d) -> getColumnType(c, ColumnType.STRING));
        this.convertMap.put("ntext", (c, d) -> getColumnType(c, ColumnType.STRING));
        this.convertMap.put("uniqueidentifier", (c, d) -> getColumnType(c, ColumnType.STRING));
        this.convertMap.put("sql_variant", (c, d) -> getColumnType(c, ColumnType.STRING));
        this.convertMap.put(
                "bigint", (c, d) -> getColumnType(c, ColumnType.LONG, ColumnType.JAVA_LANG_LONG));
        this.convertMap.put(
                "bit",
                (c, d) -> getColumnType(c, ColumnType.BOOLEAN, ColumnType.JAVA_LANG_BOOLEAN));
        this.convertMap.put("int", (c, d) -> getColumnType(c, ColumnType.INT, ColumnType.INTEGER));
        this.convertMap.put(
                "tinyint", (c, d) -> getColumnType(c, ColumnType.INT, ColumnType.INTEGER));
        this.convertMap.put(
                "smallint", (c, d) -> getColumnType(c, ColumnType.INT, ColumnType.INTEGER));
        this.convertMap.put(
                "float",
                (c, d) -> getColumnType(c, ColumnType.DOUBLE, ColumnType.JAVA_LANG_DOUBLE));
        this.convertMap.put("decimal", (c, d) -> getColumnType(c, ColumnType.DECIMAL));
        this.convertMap.put("money", (c, d) -> getColumnType(c, ColumnType.DECIMAL));
        this.convertMap.put("smallmoney", (c, d) -> getColumnType(c, ColumnType.DECIMAL));
        this.convertMap.put("numeric", (c, d) -> getColumnType(c, ColumnType.DECIMAL));
        this.convertMap.put(
                "real", (c, d) -> getColumnType(c, ColumnType.FLOAT, ColumnType.JAVA_LANG_FLOAT));
        this.convertMap.put("datetime", (c, d) -> getColumnType(c, ColumnType.TIMESTAMP));
        this.convertMap.put("smalldatetime", (c, d) -> getColumnType(c, ColumnType.TIMESTAMP));
        this.convertMap.put("datetime2", (c, d) -> getColumnType(c, ColumnType.TIMESTAMP));
        this.convertMap.put("datetimeoffset", (c, d) -> getColumnType(c, ColumnType.TIMESTAMP));
        this.convertMap.put("date", (c, d) -> getColumnType(c, ColumnType.LOCAL_DATE));
        this.convertMap.put("time", (c, d) -> getColumnType(c, ColumnType.LOCALTIME));
        this.convertMap.put("timestamp", (c, d) -> getColumnType(c, ColumnType.BYTES));
        this.convertMap.put("binary", (c, d) -> getColumnType(c, ColumnType.BYTES));
        this.convertMap.put("varbinary", (c, d) -> getColumnType(c, ColumnType.BYTES));
        this.convertMap.put("image", (c, d) -> getColumnType(c, ColumnType.BYTES));
    }

    @Override
    public String convertToDB(ColumnType columnType) {
        switch (columnType) {
            case STRING:
                return "varchar";
            case BOOLEAN:
            case JAVA_LANG_BOOLEAN:
                return "bit";
            case LONG:
            case JAVA_LANG_LONG:
                return "bigint";
            case INTEGER:
            case INT:
                return "int";
            case DOUBLE:
            case JAVA_LANG_DOUBLE:
                return "double";
            case FLOAT:
            case JAVA_LANG_FLOAT:
                return "float";
            case TIMESTAMP:
                return "datetime(0)";
            default:
                return "varchar";
        }
    }
}
