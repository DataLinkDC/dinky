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

package org.dinky.metadata.convert.source;

import org.dinky.metadata.convert.DorisType;

public class SqlServerType {
    private static final String BIT = "bit";
    private static final String TINYINT = "tinyint";
    private static final String SMALLINT = "smallint";
    private static final String INT = "int";
    private static final String BIGINT = "bigint";
    private static final String REAL = "real";
    private static final String FLOAT = "float";
    private static final String MONEY = "money";
    private static final String SMALLMONEY = "smallmoney";
    private static final String DECIMAL = "decimal";
    private static final String NUMERIC = "numeric";
    private static final String DATE = "date";
    private static final String DATETIME = "datetime";
    private static final String DATETIME2 = "datetime2";
    private static final String SMALLDATETIME = "smalldatetime";
    private static final String CHAR = "char";
    private static final String VARCHAR = "varchar";
    private static final String NCHAR = "nchar";
    private static final String NVARCHAR = "nvarchar";
    private static final String TEXT = "text";
    private static final String NTEXT = "ntext";
    private static final String UNIQUEIDENTIFIER = "uniqueidentifier";
    private static final String TIME = "time";
    private static final String TIMESTAMP = "timestamp";
    private static final String DATETIMEOFFSET = "datetimeoffset";
    private static final String IMAGE = "image";
    private static final String BINARY = "binary";
    private static final String VARBINARY = "varbinary";

    public static String toDorisType(String originSqlServerType, Integer precision, Integer scale) {
        originSqlServerType = originSqlServerType.toLowerCase();
        // For sqlserver IDENTITY type, such as 'INT IDENTITY'
        // originSqlServerType is "int identity", so we only get "int".
        String sqlServerType = originSqlServerType.split(" ")[0];
        switch (sqlServerType) {
            case BIT:
                return DorisType.BOOLEAN;
            case TINYINT:
                return DorisType.TINYINT;
            case SMALLINT:
                return DorisType.SMALLINT;
            case INT:
                return DorisType.INT;
            case BIGINT:
                return DorisType.BIGINT;
            case REAL:
                return DorisType.FLOAT;
            case FLOAT:
                return DorisType.DOUBLE;
            case MONEY:
                return String.format("%s(%s,%s)", DorisType.DECIMAL_V3, 19, 4);
            case SMALLMONEY:
                return String.format("%s(%s,%s)", DorisType.DECIMAL_V3, 10, 4);
            case DECIMAL:
            case NUMERIC:
                return precision != null && precision > 0 && precision <= 38
                        ? String.format(
                                "%s(%s,%s)", DorisType.DECIMAL_V3, precision, scale != null && scale >= 0 ? scale : 0)
                        : DorisType.STRING;
            case DATE:
                return DorisType.DATE_V2;
            case DATETIME:
            case DATETIME2:
            case SMALLDATETIME:
                return String.format("%s(%s)", DorisType.DATETIME_V2, Math.min(scale == null ? 0 : scale, 6));
            case CHAR:
            case VARCHAR:
            case NCHAR:
            case NVARCHAR:
                return precision * 3 > 65533
                        ? DorisType.STRING
                        : String.format("%s(%s)", DorisType.VARCHAR, precision * 3);
            case TEXT:
            case NTEXT:
            case TIME:
            case DATETIMEOFFSET:
            case TIMESTAMP:
            case UNIQUEIDENTIFIER:
            case BINARY:
            case VARBINARY:
                return DorisType.STRING;
            default:
                throw new UnsupportedOperationException("Unsupported SqlServer Type: " + sqlServerType);
        }
    }
}
