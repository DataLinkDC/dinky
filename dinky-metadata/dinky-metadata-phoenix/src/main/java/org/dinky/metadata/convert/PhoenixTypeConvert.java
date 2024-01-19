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

public class PhoenixTypeConvert extends AbstractJdbcTypeConvert {

    public PhoenixTypeConvert() {
        this.convertMap.clear();
        register("char", ColumnType.STRING);
        register("varchar", ColumnType.STRING);
        register("text", ColumnType.STRING);
        register("nchar", ColumnType.STRING);
        register("nvarchar", ColumnType.STRING);
        register("ntext", ColumnType.STRING);
        register("uniqueidentifier", ColumnType.STRING);
        register("sql_variant", ColumnType.STRING);
        register("bigint", ColumnType.LONG, ColumnType.JAVA_LANG_LONG);
        register("int", ColumnType.INT, ColumnType.INTEGER);
        register("tinyint", ColumnType.INT, ColumnType.INTEGER);
        register("smallint", ColumnType.INT, ColumnType.INTEGER);
        register("integer", ColumnType.INT, ColumnType.INTEGER);
        register("float", ColumnType.FLOAT, ColumnType.JAVA_LANG_FLOAT);
        register("decimal", ColumnType.DECIMAL);
        register("money", ColumnType.DECIMAL);
        register("smallmoney", ColumnType.DECIMAL);
        register("numeric", ColumnType.DECIMAL);
        register("double", ColumnType.DOUBLE, ColumnType.JAVA_LANG_DOUBLE);
        register("boolean", ColumnType.BOOLEAN, ColumnType.JAVA_LANG_BOOLEAN);
        register("smalldatetime", ColumnType.TIMESTAMP);
        register("datetime", ColumnType.TIMESTAMP);
        register("timestamp", ColumnType.BYTES);
        register("binary", ColumnType.BYTES);
        register("varbinary", ColumnType.BYTES);
        register("image", ColumnType.BYTES);
        register("time", ColumnType.TIME);
        register("date", ColumnType.DATE);
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
