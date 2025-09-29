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

public class HiveTypeConvert extends AbstractJdbcTypeConvert {

    public HiveTypeConvert() {}

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
        } else if (type.contains("boolean")) {
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
        } else if (type.contains("binary")) {
            return ColumnType.of(DataTypes.BINARY, DataTypes.BINARY.copyLogicalType(logicalTypeParam));
        } else if (type.contains("tinyint")) {
            return ColumnType.of(DataTypes.TINYINT, DataTypes.TINYINT.copyLogicalType(logicalTypeParam));
        } else if (type.contains("smallint")) {
            return ColumnType.of(DataTypes.SMALLINT, DataTypes.SMALLINT.copyLogicalType(logicalTypeParam));
        } else if (type.contains("int")) {
            return ColumnType.of(DataTypes.INT, DataTypes.INT.copyLogicalType(logicalTypeParam));
        }
        // largeint
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
                return "timestamp";
            case DATE:
                return "date";
            case TIME:
                return "time";
            case VARCHAR:
                return "varchar";
            case CHAR:
                return "char";
            case BINARY:
                return "binary";
            case BYTES:
                return "blob";
            case TINYINT:
                return "tinyint";
            case INT:
                return "int";
            case STRING:
            default:
                return "varchar";
        }
    }
}
