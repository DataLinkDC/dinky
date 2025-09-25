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

/**
 * ClickHouseTypeConvert
 *
 * @since 2021/7/21 17:15
 */
public class ClickHouseTypeConvert extends AbstractJdbcTypeConvert {

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
        if (type.contains("array")) {
            return ColumnType.of(DataTypes.ARRAY, DataTypes.ARRAY.copyLogicalType(logicalTypeParam));
        } else if (type.contains("map")) {
            return ColumnType.of(DataTypes.MAP, DataTypes.MAP.copyLogicalType(logicalTypeParam));
        } else if (type.contains("int8")) {
            return ColumnType.of(DataTypes.TINYINT, DataTypes.TINYINT.copyLogicalType(logicalTypeParam));
        } else if (type.contains("int16")) {
            return ColumnType.of(DataTypes.SMALLINT, DataTypes.SMALLINT.copyLogicalType(logicalTypeParam));
        } else if (type.contains("int32")) {
            return ColumnType.of(DataTypes.INT, DataTypes.INT.copyLogicalType(logicalTypeParam));
        } else if (type.contains("int64")) {
            return ColumnType.of(DataTypes.BIGINT, DataTypes.BIGINT.copyLogicalType(logicalTypeParam));
        } else if (type.contains("int128") || type.contains("int256")) {
            return ColumnType.of(DataTypes.DECIMAL, DataTypes.DECIMAL.copyLogicalType(logicalTypeParam));
        } else if (type.contains("float32")) {
            return ColumnType.of(DataTypes.FLOAT, DataTypes.FLOAT.copyLogicalType(logicalTypeParam));
        } else if (type.contains("float64")) {
            return ColumnType.of(DataTypes.DOUBLE, DataTypes.DOUBLE.copyLogicalType(logicalTypeParam));
        } else if (type.contains("string")
                || type.contains("uuid")
                || type.contains("enum")
                || type.contains("tuple")) {
            return ColumnType.of(DataTypes.STRING, DataTypes.STRING.copyLogicalType(logicalTypeParam));
        } else if (type.contains("boolean")) {
            return ColumnType.of(DataTypes.BOOLEAN, DataTypes.BOOLEAN.copyLogicalType(logicalTypeParam));
        } else if (type.contains("datetime")) {
            return ColumnType.of(DataTypes.TIMESTAMP, DataTypes.TIMESTAMP.copyLogicalType(logicalTypeParam));
        } else if (type.contains("date")) {
            return ColumnType.of(DataTypes.DATE, DataTypes.DATE.copyLogicalType(logicalTypeParam));
        } else if (type.contains("decimal")) {
            return ColumnType.of(DataTypes.DECIMAL, DataTypes.DECIMAL.copyLogicalType(logicalTypeParam));
        }
        return ColumnType.of(DataTypes.STRING, DataTypes.STRING.copyLogicalType(logicalTypeParam));
    }

    @Override
    public String convertToDB(ColumnType columnType) {
        if (columnType == null) {
            return "string";
        }
        switch (columnType.getValue()) {
            case ARRAY:
                return "Array";
            case MAP:
                return "Map";
            case TINYINT:
                return "Int8";
            case SMALLINT:
                return "Int16";
            case INT:
                return "Int32";
            case BIGINT:
                return "Int64";
            case DECIMAL:
                return "decimal";
            case FLOAT:
                return "float32";
            case DOUBLE:
                return "float64";
            case BOOLEAN:
                return "boolean";
            case TIMESTAMP:
                return "datetime";
            case DATE:
                return "date";
            case STRING:
            default:
                return "string";
        }
    }
}
