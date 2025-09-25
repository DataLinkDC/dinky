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

public class OracleTypeConvert extends AbstractJdbcTypeConvert {

    public OracleTypeConvert() {}

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
        if (type.matches("number\\(+\\d\\)")) {
            return ColumnType.of(DataTypes.INT, DataTypes.INT.copyLogicalType(logicalTypeParam));
        } else if (type.matches("number\\(+\\d{2}+\\)")) {
            return ColumnType.of(DataTypes.DECIMAL, DataTypes.DECIMAL.copyLogicalType(logicalTypeParam));
        } else if (type.contains("numeric") || type.contains("decimal")) {
            return ColumnType.of(DataTypes.DECIMAL, DataTypes.DECIMAL.copyLogicalType(logicalTypeParam));
        } else if (type.contains("float")) {
            return ColumnType.of(DataTypes.FLOAT, DataTypes.FLOAT.copyLogicalType(logicalTypeParam));
        } else if (type.contains("timestamp")) {
            return ColumnType.of(DataTypes.TIMESTAMP, DataTypes.TIMESTAMP.copyLogicalType(logicalTypeParam));
        } else if (type.contains("date")) {
            return ColumnType.of(DataTypes.DATE, DataTypes.DATE.copyLogicalType(logicalTypeParam));
        } else if (type.contains("varchar")) {
            return ColumnType.of(DataTypes.VARCHAR, DataTypes.VARCHAR.copyLogicalType(logicalTypeParam));
        } else if (type.contains("char")) {
            return ColumnType.of(DataTypes.CHAR, DataTypes.CHAR.copyLogicalType(logicalTypeParam));
        } else if (type.contains("blob")) {
            return ColumnType.of(DataTypes.BYTES, DataTypes.BYTES.copyLogicalType(logicalTypeParam));
        } else if (type.contains("int")) {
            return ColumnType.of(DataTypes.INT, DataTypes.INT.copyLogicalType(logicalTypeParam));
        }
        // clob
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
            case FLOAT:
                return "float";
            case DOUBLE:
                return "number";
            case BOOLEAN:
                return "boolean";
            case TIMESTAMP:
                return "datetime";
            case DATE:
                return "date";
            case CHAR:
                return "char";
            case BYTES:
                return "blob";
            case INT:
                return "int";
            case STRING:
            case VARCHAR:
            default:
                return "varchar";
        }
    }
}
