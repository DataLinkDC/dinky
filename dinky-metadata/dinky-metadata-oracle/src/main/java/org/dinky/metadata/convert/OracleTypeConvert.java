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
import org.dinky.data.model.Column;
import org.dinky.metadata.config.DriverConfig;

import java.util.Optional;

/**
 * OracleTypeConvert
 *
 * @since 2021/7/21 16:00
 */
public class OracleTypeConvert extends AbstractJdbcTypeConvert {

    public OracleTypeConvert() {
        this.convertMap.clear();
        register("char", ColumnType.STRING);
        register("date", ColumnType.LOCAL_DATETIME);
        register("timestamp", ColumnType.TIMESTAMP);
        register("number", OracleTypeConvert::convertNumber);
        register("float", ColumnType.JAVA_LANG_FLOAT);
        register("clob", ColumnType.STRING);
        register("blob", ColumnType.BYTES);
    }

    private static Optional<ColumnType> convertNumber(Column column, DriverConfig driverConfig) {
        boolean isNullable = !column.isKeyFlag() && column.isNullable();
        String t = column.getType().toLowerCase();

        if (t.matches("number\\(+\\d\\)")) {
            if (isNullable) {
                return Optional.of(ColumnType.INTEGER);
            }
            return Optional.of(ColumnType.INT);
        }
        if (t.matches("number\\(+\\d{2}+\\)")) {
            if (isNullable) {
                return Optional.of(ColumnType.JAVA_LANG_LONG);
            }
            return Optional.of(ColumnType.LONG);
        }
        return Optional.of(ColumnType.DECIMAL);
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
