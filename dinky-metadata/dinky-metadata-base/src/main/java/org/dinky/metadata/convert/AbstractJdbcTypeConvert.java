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

import org.dinky.data.types.ColumnType;
import org.dinky.metadata.config.AbstractJdbcConfig;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractJdbcTypeConvert extends AbstractTypeConvert<AbstractJdbcConfig> {

    public Object convertValue(ResultSet results, String columnName, ColumnType columnType) throws SQLException {
        switch (columnType.getValue()) {
            case BOOLEAN:
                return results.getBoolean(columnName);
            case TINYINT:
            case SMALLINT:
                return results.getShort(columnName);
            case INT:
                return results.getInt(columnName);
            case BIGINT:
                return results.getLong(columnName);
            case FLOAT:
                return results.getFloat(columnName);
            case DOUBLE:
                return results.getDouble(columnName);
            case DECIMAL:
                return results.getBigDecimal(columnName);
            case BINARY:
            case VARBINARY:
            case BYTES:
                return results.getBytes(columnName);
            case DATE:
                return results.getDate(columnName);
            case TIME:
                return results.getTime(columnName);
            case TIMESTAMP:
            case TIMESTAMP_LTZ:
            case TIMESTAMP_TZ:
                return results.getTimestamp(columnName);
            case ARRAY:
            case MAP:
            case STRING:
            case VARCHAR:
            case CHAR:
            case DAY:
            case DAY_TO_HOUR:
            case DAY_TO_MINUTE:
            case DAY_TO_SECOND:
            case HOUR:
            case HOUR_TO_MINUTE:
            case HOUR_TO_SECOND:
            case MINUTE:
            case MINUTE_TO_SECOND:
            case MONTH:
            case SECOND:
            case YEAR:
            case YEAR_TO_MONTH:
            default:
                return results.getString(columnName);
        }
    }
}
