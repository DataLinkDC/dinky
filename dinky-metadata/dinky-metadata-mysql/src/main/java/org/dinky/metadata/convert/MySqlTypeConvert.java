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
import org.dinky.metadata.driver.DriverConfig;

import java.util.Optional;

/**
 * MySqlTypeConvert
 *
 * @since 2021/7/20 15:21
 */
public class MySqlTypeConvert extends AbstractTypeConvert {

    public MySqlTypeConvert() {
        this.convertMap.clear();
        this.convertMap.put("numeric", (c, d) -> getColumnType(c, ColumnType.DECIMAL));
        this.convertMap.put("decimal", (c, d) -> getColumnType(c, ColumnType.DECIMAL));
        this.convertMap.put(
                "bigint", (c, d) -> getColumnType(c, ColumnType.LONG, ColumnType.JAVA_LANG_LONG));
        this.convertMap.put(
                "float", (c, d) -> getColumnType(c, ColumnType.FLOAT, ColumnType.JAVA_LANG_FLOAT));
        this.convertMap.put(
                "double",
                (c, d) -> getColumnType(c, ColumnType.DOUBLE, ColumnType.JAVA_LANG_DOUBLE));
        this.convertMap.put(
                "boolean",
                (c, d) -> getColumnType(c, ColumnType.BOOLEAN, ColumnType.JAVA_LANG_BOOLEAN));
        this.convertMap.put(
                "bit",
                (c, d) -> getColumnType(c, ColumnType.BOOLEAN, ColumnType.JAVA_LANG_BOOLEAN));
        this.convertMap.put("datetime", (c, d) -> getColumnType(c, ColumnType.TIMESTAMP));
        this.convertMap.put("date", (c, d) -> getColumnType(c, ColumnType.DATE));
        this.convertMap.put("timestamp", (c, d) -> getColumnType(c, ColumnType.TIMESTAMP));
        this.convertMap.put("time", (c, d) -> getColumnType(c, ColumnType.TIME));
        this.convertMap.put("char", (c, d) -> getColumnType(c, ColumnType.STRING));
        this.convertMap.put("text", (c, d) -> getColumnType(c, ColumnType.STRING));
        this.convertMap.put("binary", (c, d) -> getColumnType(c, ColumnType.BYTES));
        this.convertMap.put("blob", (c, d) -> getColumnType(c, ColumnType.BYTES));
        this.convertMap.put("tinyint", MySqlTypeConvert::convertTinyint);
        this.convertMap.put(
                "mediumint", (c, d) -> getColumnType(c, ColumnType.INT, ColumnType.INTEGER));
        this.convertMap.put(
                "smallint", (c, d) -> getColumnType(c, ColumnType.INT, ColumnType.INTEGER));
        this.convertMap.put("int", (c, d) -> getColumnType(c, ColumnType.INT, ColumnType.INTEGER));
    }

    private static Optional<ColumnType> convertTinyint(Column column, DriverConfig driverConfig) {
        Integer length = Asserts.isNull(column.getLength()) ? 0 : column.getLength();
        if (!length.equals(1)) {
            return Optional.empty();
        }

        boolean isNullable = !column.isKeyFlag() && column.isNullable();
        boolean tinyInt1isBit =
                Asserts.isNotNullString(driverConfig.getUrl())
                        && !driverConfig.getUrl().contains("tinyInt1isBit=false");
        if (tinyInt1isBit) {
            if (isNullable) {
                return Optional.of(ColumnType.JAVA_LANG_BOOLEAN);
            }
            return Optional.of(ColumnType.BOOLEAN);
        }

        if (isNullable) {
            return Optional.of(ColumnType.INTEGER);
        }
        return Optional.of(ColumnType.INT);
    }

    @Override
    public String convertToDB(ColumnType columnType) {
        switch (columnType) {
            case STRING:
                return "varchar";
            case BYTE:
                return "tinyint";
            case SHORT:
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
