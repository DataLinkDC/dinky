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

public class SqlServerTypeConvert extends AbstractJdbcTypeConvert {

    public SqlServerTypeConvert() {
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
        register("bit", ColumnType.BOOLEAN, ColumnType.JAVA_LANG_BOOLEAN);
        register("tinyint", ColumnType.INT, ColumnType.INTEGER);
        register("smallint", ColumnType.INT, ColumnType.INTEGER);
        register("int", ColumnType.INT, ColumnType.INTEGER);
        register("float", ColumnType.DOUBLE, ColumnType.JAVA_LANG_DOUBLE);
        register("decimal", ColumnType.DECIMAL);
        register("money", ColumnType.DECIMAL);
        register("smallmoney", ColumnType.DECIMAL);
        register("numeric", ColumnType.DECIMAL);
        register("real", ColumnType.FLOAT, ColumnType.JAVA_LANG_FLOAT);
        register("datetimeoffset", ColumnType.TIMESTAMP);
        register("smalldatetime", ColumnType.TIMESTAMP);
        register("datetime2", ColumnType.TIMESTAMP);
        register("datetime", ColumnType.TIMESTAMP);
        register("date", ColumnType.LOCAL_DATE);
        register("time", ColumnType.LOCALTIME);
        register("timestamp", ColumnType.STRING);
        register("binary", ColumnType.BYTES);
        register("varbinary", ColumnType.BYTES);
        register("image", ColumnType.BYTES);
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
