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
import org.dinky.data.model.Column;
import org.dinky.data.types.ColumnType;
import org.dinky.data.types.DataTypes;
import org.dinky.data.types.LogicalTypeParam;

import org.apache.paimon.data.GenericRow;
import org.apache.paimon.data.InternalRow;
import org.apache.paimon.data.columnar.ColumnarArray;
import org.apache.paimon.types.ArrayType;
import org.apache.paimon.types.DataField;
import org.apache.paimon.types.DataTypeRoot;
import org.apache.paimon.types.DecimalType;
import org.apache.paimon.types.TimestampType;

import java.time.LocalDate;
import java.time.LocalTime;

public class PaimonTypeConvert extends AbstractJdbcTypeConvert {

    public PaimonTypeConvert() {}

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
                try {
                    return row.getShort(ordinal);
                } catch (Exception e) {
                    return row.getByte(ordinal);
                }
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
                if (row.getArray(ordinal) instanceof ColumnarArray) {
                    ColumnarArray columnarArray = (ColumnarArray) row.getArray(ordinal);
                    ArrayType arrayType = (ArrayType) fieldType.type();
                    boolean isString = true;
                    switch (arrayType.getElementType().asSQLString().toUpperCase()) {
                        case "SHORT":
                        case "TINYINT":
                        case "INT":
                        case "BIGINT":
                        case "FLOAT":
                        case "DOUBLE":
                        case "DECIMAL":
                            isString = false;
                            break;
                        default:
                            break;
                    }
                    StringBuilder columnarArrayStringBuild = new StringBuilder("[ ");
                    for (int i = 0; i < columnarArray.size(); i++) {
                        if (i > 0) {
                            columnarArrayStringBuild.append(", ");
                        }
                        if (isString) {
                            columnarArrayStringBuild.append("\"");
                        }
                        columnarArrayStringBuild.append(columnarArray.getString(i));
                        if (isString) {
                            columnarArrayStringBuild.append("\"");
                        }
                    }
                    columnarArrayStringBuild.append(" ]");
                    return columnarArrayStringBuild.toString();
                }
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

    @Override
    public ColumnType convert(Column column) {
        if (Asserts.isNull(column)) {
            throw new RuntimeException("Column is null");
        }
        int length = Asserts.isNull(column.getLength()) ? 0 : column.getLength();
        String type = Asserts.isNull(column.getType()) ? "" : column.getType().toLowerCase();
        boolean isNullable = !column.isKeyFlag() && column.isNullable();
        final LogicalTypeParam logicalTypeParam =
                LogicalTypeParam.of(isNullable, length, column.getPrecision(), column.getScale());
        if (type.contains("decimal")) {
            return ColumnType.of(DataTypes.DECIMAL, DataTypes.DECIMAL.copyLogicalType(logicalTypeParam));
        } else if (type.contains("bigint")) {
            return ColumnType.of(DataTypes.BIGINT, DataTypes.BIGINT.copyLogicalType(logicalTypeParam));
        } else if (type.contains("float")) {
            return ColumnType.of(DataTypes.FLOAT, DataTypes.FLOAT.copyLogicalType(logicalTypeParam));
        } else if (type.contains("double")) {
            return ColumnType.of(DataTypes.DOUBLE, DataTypes.DOUBLE.copyLogicalType(logicalTypeParam));
        } else if (type.contains("boolean") || type.contains("bit")) {
            return ColumnType.of(DataTypes.BOOLEAN, DataTypes.BOOLEAN.copyLogicalType(logicalTypeParam));
        } else if (type.contains("datetime") || type.contains("timestamp")) {
            return ColumnType.of(DataTypes.TIMESTAMP, DataTypes.TIMESTAMP.copyLogicalType(logicalTypeParam));
        } else if (type.contains("date")) {
            return ColumnType.of(DataTypes.DATE, DataTypes.DATE.copyLogicalType(logicalTypeParam));
        } else if (type.contains("time")) {
            return ColumnType.of(DataTypes.TIME, DataTypes.TIME.copyLogicalType(logicalTypeParam));
        } else if (type.contains("varchar")) {
            return ColumnType.of(DataTypes.VARCHAR, DataTypes.VARCHAR.copyLogicalType(logicalTypeParam));
        } else if (type.contains("char")) {
            return ColumnType.of(DataTypes.CHAR, DataTypes.CHAR.copyLogicalType(logicalTypeParam));
        } else if (type.contains("varbinary")) {
            return ColumnType.of(DataTypes.VARBINARY, DataTypes.VARBINARY.copyLogicalType(logicalTypeParam));
        } else if (type.contains("binary")) {
            return ColumnType.of(DataTypes.BINARY, DataTypes.BINARY.copyLogicalType(logicalTypeParam));
        } else if (type.contains("blob")) {
            return ColumnType.of(DataTypes.BYTES, DataTypes.BYTES.copyLogicalType(logicalTypeParam));
        } else if (type.contains("tinyint")) {
            return ColumnType.of(DataTypes.TINYINT, DataTypes.TINYINT.copyLogicalType(logicalTypeParam));
        } else if (type.contains("smallint")) {
            return ColumnType.of(DataTypes.SMALLINT, DataTypes.SMALLINT.copyLogicalType(logicalTypeParam));
        } else if (type.contains("int")) {
            return ColumnType.of(DataTypes.INT, DataTypes.INT.copyLogicalType(logicalTypeParam));
        }
        // text
        return ColumnType.of(DataTypes.STRING, DataTypes.STRING.copyLogicalType(logicalTypeParam));
    }

    @Override
    public String convertToDB(ColumnType columnType) {
        if (columnType == null) {
            return "varchar";
        }
        switch (columnType.getValue()) {
            case DECIMAL:
                return "decimal";
            case BIGINT:
                return "bigint";
            case FLOAT:
                return "float";
            case DOUBLE:
                return "double";
            case BOOLEAN:
                return "boolean";
            case TIMESTAMP:
                return "datetime";
            case DATE:
                return "date";
            case TIME:
                return "time";
            case CHAR:
                return "char";
            case VARBINARY:
                return "varbinary";
            case BINARY:
                return "binary";
            case BYTES:
                return "blob";
            case TINYINT:
                return "tinyint";
            case SMALLINT:
                return "smallint";
            case INT:
                return "int";
            case STRING:
            case VARCHAR:
            default:
                return "varchar";
        }
    }
}
