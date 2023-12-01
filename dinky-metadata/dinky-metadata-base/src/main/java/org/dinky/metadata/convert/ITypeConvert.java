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
import org.dinky.metadata.config.DriverConfig;
import org.dinky.metadata.config.IConnectConfig;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ITypeConvert
 *
 * @since 2021/7/20 14:39
 */
public interface ITypeConvert<T extends IConnectConfig> {

    default String convertToDB(Column column) {
        return convertToDB(column.getJavaType());
    }

    ColumnType convert(Column column);

    default ColumnType convert(Column column, DriverConfig<T> driverConfig) {
        return convert(column);
    }

    String convertToDB(ColumnType columnType);

    default Object convertValue(ResultSet results, String columnName, String javaType) throws SQLException {
        if (Asserts.isNull(javaType)) {
            return results.getString(columnName);
        }
        switch (javaType.toLowerCase()) {
            case "string":
            case "text":
            case "varchar":
                return results.getString(columnName);
            case "double":
                return results.getDouble(columnName);
            case "int":
                return results.getInt(columnName);
            case "float":
                return results.getFloat(columnName);
            case "bigint":
                return results.getLong(columnName);
            case "decimal":
                return results.getBigDecimal(columnName);
            case "date":
            case "localdate":
                return results.getDate(columnName);
            case "time":
            case "localtime":
                return results.getTime(columnName);
            case "timestamp":
                return results.getTimestamp(columnName);
            case "blob":
                return results.getBlob(columnName);
            case "boolean":
            case "bool":
            case "bit":
                return results.getBoolean(columnName);
            case "byte":
                return results.getByte(columnName);
            case "bytes":
                return results.getBytes(columnName);
            default:
                return results.getString(columnName);
        }
    }
}
