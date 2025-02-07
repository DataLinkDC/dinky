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
import org.dinky.metadata.config.AbstractJdbcConfig;
import org.dinky.metadata.config.DriverConfig;

import org.apache.paimon.data.GenericRow;
import org.apache.paimon.data.InternalRow;
import org.apache.paimon.types.DataField;
import org.apache.paimon.types.DataTypeRoot;
import org.apache.paimon.types.DecimalType;
import org.apache.paimon.types.TimestampType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

/**
 * MySqlTypeConvert
 *
 * @since 2021/7/20 15:21
 */
public class PaimonTypeConvert extends AbstractJdbcTypeConvert {

    public PaimonTypeConvert() {
        this.convertMap.clear();
        register("numeric", ColumnType.DECIMAL);
        register("decimal", ColumnType.DECIMAL);
        register("bigint", ColumnType.LONG, ColumnType.JAVA_LANG_LONG);
        register("float", ColumnType.FLOAT, ColumnType.JAVA_LANG_FLOAT);
        register("double", ColumnType.DOUBLE, ColumnType.JAVA_LANG_DOUBLE);
        register("boolean", ColumnType.BOOLEAN, ColumnType.JAVA_LANG_BOOLEAN);
        register("bit", ColumnType.BOOLEAN, ColumnType.JAVA_LANG_BOOLEAN);
        register("datetime", ColumnType.TIMESTAMP);
        register("date", ColumnType.DATE);
        register("timestamp", ColumnType.TIMESTAMP);
        register("time", ColumnType.TIME);
        register("char", ColumnType.STRING);
        register("text", ColumnType.STRING);
        register("binary", ColumnType.BYTES);
        register("blob", ColumnType.BYTES);
        register("tinyint", PaimonTypeConvert::convertTinyint);
        register("mediumint", ColumnType.INT, ColumnType.INTEGER);
        register("smallint", ColumnType.INT, ColumnType.INTEGER);
        register("int", ColumnType.INT, ColumnType.INTEGER);
    }

    public static Object getRowDataSafe(DataField fieldType, InternalRow row, int ordinal) {
        if (row.isNullAt(ordinal)) {
            return null;
        }
        DataTypeRoot root = fieldType.type().getTypeRoot();
        switch (root) {
            case CHAR:
            case VARCHAR:
                return row.getString(ordinal).toString();
            case BOOLEAN:
                return String.valueOf(row.getBoolean(ordinal));
            case BINARY:
            case VARBINARY:
                return "<Binary Type>";
            case DECIMAL:
                DecimalType decimalType = (DecimalType) fieldType.type();
                return row.getDecimal(ordinal, decimalType.getPrecision(), decimalType.getScale())
                        .toString();
            case TINYINT:
            case SMALLINT:
                return row.getShort(ordinal);
            case INTEGER:
                return row.getInt(ordinal);
            case BIGINT:
                return row.getLong(ordinal);
            case FLOAT:
                return row.getFloat(ordinal);
            case DOUBLE:
                return row.getDouble(ordinal);
            case DATE:
                int dateInt = row.getInt(ordinal);
                return LocalDate.of(1970, 1, 1).plusDays(dateInt);
            case TIME_WITHOUT_TIME_ZONE:
                int timeInt = row.getInt(ordinal);
                return LocalTime.ofSecondOfDay(timeInt / 1000);
            case TIMESTAMP_WITHOUT_TIME_ZONE:
            case TIMESTAMP_WITH_LOCAL_TIME_ZONE:
                TimestampType timestampType = (TimestampType) fieldType.type();
                return row.getTimestamp(ordinal, timestampType.getPrecision()).toLocalDateTime();
            case ARRAY:
            case MULTISET:
                return row.getArray(ordinal).toString();
            case MAP:
                return row.getMap(ordinal).toString();
                // case ROW:
                //  return row.getRow(ordinal).toString();
            default:
                if (row instanceof GenericRow) {
                    return ((GenericRow) row).getField(ordinal).toString();
                }
                return row.getString(ordinal);
        }
    }

    private static Optional<ColumnType> convertTinyint(Column column, DriverConfig<AbstractJdbcConfig> driverConfig) {
        Integer length = Asserts.isNull(column.getLength()) ? 0 : column.getLength();
        if (!length.equals(1)) {
            return Optional.empty();
        }

        boolean isNullable = !column.isKeyFlag() && column.isNullable();
        boolean tinyInt1isBit =
                Asserts.isNotNullString(driverConfig.getConnectConfig().getUrl())
                        && !driverConfig.getConnectConfig().getUrl().contains("tinyInt1isBit=false");
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
    public ColumnType convert(Column column, DriverConfig<AbstractJdbcConfig> driverConfig) {
        ColumnType columnType = ColumnType.STRING;
        if (Asserts.isNull(column)) {
            return columnType;
        }
        Integer length = Asserts.isNull(column.getLength()) ? 0 : column.getLength();
        String t = Asserts.isNull(column.getType()) ? "" : column.getType().toLowerCase();
        boolean isNullable = !column.isKeyFlag() && column.isNullable();
        boolean tinyInt1isBit =
                Asserts.isNotNullString(driverConfig.getConnectConfig().getUrl())
                        && !driverConfig.getConnectConfig().getUrl().contains("tinyInt1isBit=false");
        if (t.contains("numeric") || t.contains("decimal")) {
            columnType = ColumnType.DECIMAL;
        } else if (t.contains("bigint")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_LONG;
            } else {
                columnType = ColumnType.LONG;
            }
        } else if (t.contains("float")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_FLOAT;
            } else {
                columnType = ColumnType.FLOAT;
            }
        } else if (t.contains("double")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_DOUBLE;
            } else {
                columnType = ColumnType.DOUBLE;
            }
        } else if (t.contains("boolean")
                || (tinyInt1isBit && t.contains("tinyint") && length.equals(1))
                || t.contains("bit")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_BOOLEAN;
            } else {
                columnType = ColumnType.BOOLEAN;
            }
        } else if (t.contains("datetime")) {
            columnType = ColumnType.TIMESTAMP;
        } else if (t.contains("date")) {
            columnType = ColumnType.DATE;
        } else if (t.contains("timestamp")) {
            columnType = ColumnType.TIMESTAMP;
        } else if (t.contains("time")) {
            columnType = ColumnType.TIME;
        } else if (t.contains("char") || t.contains("text")) {
            columnType = ColumnType.STRING;
        } else if (t.contains("binary") || t.contains("blob")) {
            columnType = ColumnType.BYTES;
        } else if (t.contains("tinyint") || t.contains("mediumint") || t.contains("smallint") || t.contains("int")) {
            if (isNullable) {
                columnType = ColumnType.INTEGER;
            } else {
                columnType = ColumnType.INT;
            }
        }
        return columnType;
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
