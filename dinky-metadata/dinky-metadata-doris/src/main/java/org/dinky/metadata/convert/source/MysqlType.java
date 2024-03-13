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

import org.dinky.assertion.Asserts;
import org.dinky.metadata.constant.DorisConstant;
import org.dinky.metadata.convert.DorisType;

public class MysqlType {

    // MySQL driver returns width of timestamp types instead of precision.
    // 19 characters are used for zero-precision timestamps while others
    // require 19 + precision + 1 characters with the additional character
    // required for the decimal separator.
    private static final int ZERO_PRECISION_TIMESTAMP_COLUMN_SIZE = 19;
    private static final String BIT = "BIT";
    private static final String BOOLEAN = "BOOLEAN";
    private static final String BOOL = "BOOL";
    private static final String TINYINT = "TINYINT";
    private static final String TINYINT_UNSIGNED = "TINYINT UNSIGNED";
    private static final String TINYINT_UNSIGNED_ZEROFILL = "TINYINT UNSIGNED ZEROFILL";
    private static final String SMALLINT = "SMALLINT";
    private static final String SMALLINT_UNSIGNED = "SMALLINT UNSIGNED";
    private static final String SMALLINT_UNSIGNED_ZEROFILL = "SMALLINT UNSIGNED ZEROFILL";
    private static final String MEDIUMINT = "MEDIUMINT";
    private static final String MEDIUMINT_UNSIGNED = "MEDIUMINT UNSIGNED";
    private static final String MEDIUMINT_UNSIGNED_ZEROFILL = "MEDIUMINT UNSIGNED ZEROFILL";
    private static final String INT = "INT";
    private static final String INT_UNSIGNED = "INT UNSIGNED";
    private static final String INT_UNSIGNED_ZEROFILL = "INT UNSIGNED ZEROFILL";
    private static final String BIGINT = "BIGINT";
    private static final String SERIAL = "SERIAL";
    private static final String BIGINT_UNSIGNED = "BIGINT UNSIGNED";
    private static final String BIGINT_UNSIGNED_ZEROFILL = "BIGINT UNSIGNED ZEROFILL";
    private static final String REAL = "REAL";
    private static final String REAL_UNSIGNED = "REAL UNSIGNED";
    private static final String REAL_UNSIGNED_ZEROFILL = "REAL UNSIGNED ZEROFILL";
    private static final String FLOAT = "FLOAT";
    private static final String FLOAT_UNSIGNED = "FLOAT UNSIGNED";
    private static final String FLOAT_UNSIGNED_ZEROFILL = "FLOAT UNSIGNED ZEROFILL";
    private static final String DOUBLE = "DOUBLE";
    private static final String DOUBLE_UNSIGNED = "DOUBLE UNSIGNED";
    private static final String DOUBLE_UNSIGNED_ZEROFILL = "DOUBLE UNSIGNED ZEROFILL";
    private static final String DOUBLE_PRECISION = "DOUBLE PRECISION";
    private static final String DOUBLE_PRECISION_UNSIGNED = "DOUBLE PRECISION UNSIGNED";
    private static final String DOUBLE_PRECISION_UNSIGNED_ZEROFILL = "DOUBLE PRECISION UNSIGNED ZEROFILL";
    private static final String NUMERIC = "NUMERIC";
    private static final String NUMERIC_UNSIGNED = "NUMERIC UNSIGNED";
    private static final String NUMERIC_UNSIGNED_ZEROFILL = "NUMERIC UNSIGNED ZEROFILL";
    private static final String FIXED = "FIXED";
    private static final String FIXED_UNSIGNED = "FIXED UNSIGNED";
    private static final String FIXED_UNSIGNED_ZEROFILL = "FIXED UNSIGNED ZEROFILL";
    private static final String DECIMAL = "DECIMAL";
    private static final String DECIMAL_UNSIGNED = "DECIMAL UNSIGNED";
    private static final String DECIMAL_UNSIGNED_ZEROFILL = "DECIMAL UNSIGNED ZEROFILL";
    private static final String CHAR = "CHAR";
    private static final String VARCHAR = "VARCHAR";
    private static final String TINYTEXT = "TINYTEXT";
    private static final String MEDIUMTEXT = "MEDIUMTEXT";
    private static final String TEXT = "TEXT";
    private static final String LONGTEXT = "LONGTEXT";
    private static final String DATE = "DATE";
    private static final String TIME = "TIME";
    private static final String DATETIME = "DATETIME";
    private static final String TIMESTAMP = "TIMESTAMP";
    private static final String YEAR = "YEAR";
    private static final String BINARY = "BINARY";
    private static final String VARBINARY = "VARBINARY";
    private static final String TINYBLOB = "TINYBLOB";
    private static final String MEDIUMBLOB = "MEDIUMBLOB";
    private static final String BLOB = "BLOB";
    private static final String LONGBLOB = "LONGBLOB";
    private static final String JSON = "JSON";
    private static final String ENUM = "ENUM";
    private static final String SET = "SET";

    public static String toDorisType(String type, Integer length, Integer scale) {
        switch (type.toUpperCase()) {
            case BIT:
            case BOOLEAN:
            case BOOL:
                return DorisType.BOOLEAN;
            case TINYINT:
                return DorisType.TINYINT;
            case TINYINT_UNSIGNED:
            case TINYINT_UNSIGNED_ZEROFILL:
            case SMALLINT:
                return DorisType.SMALLINT;
            case SMALLINT_UNSIGNED:
            case SMALLINT_UNSIGNED_ZEROFILL:
            case INT:
            case MEDIUMINT:
            case YEAR:
                return DorisType.INT;
            case INT_UNSIGNED:
            case INT_UNSIGNED_ZEROFILL:
            case MEDIUMINT_UNSIGNED:
            case MEDIUMINT_UNSIGNED_ZEROFILL:
            case BIGINT:
                return DorisType.BIGINT;
            case BIGINT_UNSIGNED:
            case BIGINT_UNSIGNED_ZEROFILL:
                return DorisType.LARGEINT;
            case FLOAT:
            case FLOAT_UNSIGNED:
            case FLOAT_UNSIGNED_ZEROFILL:
                return DorisType.FLOAT;
            case REAL:
            case REAL_UNSIGNED:
            case REAL_UNSIGNED_ZEROFILL:
            case DOUBLE:
            case DOUBLE_UNSIGNED:
            case DOUBLE_UNSIGNED_ZEROFILL:
            case DOUBLE_PRECISION:
            case DOUBLE_PRECISION_UNSIGNED:
            case DOUBLE_PRECISION_UNSIGNED_ZEROFILL:
                return DorisType.DOUBLE;
            case NUMERIC:
            case NUMERIC_UNSIGNED:
            case NUMERIC_UNSIGNED_ZEROFILL:
            case FIXED:
            case FIXED_UNSIGNED:
            case FIXED_UNSIGNED_ZEROFILL:
            case DECIMAL:
            case DECIMAL_UNSIGNED:
            case DECIMAL_UNSIGNED_ZEROFILL:
                return length != null && length <= 38
                        ? String.format(
                                "%s(%s,%s)", DorisType.DECIMAL_V3, length, scale != null && scale >= 0 ? scale : 0)
                        : DorisType.STRING;
            case DATE:
                return DorisType.DATE_V2;
            case DATETIME:
            case TIMESTAMP:
                // default precision is 0
                // see https://dev.mysql.com/doc/refman/8.0/en/date-and-time-type-syntax.html
                if (length == null || length <= 0 || length == ZERO_PRECISION_TIMESTAMP_COLUMN_SIZE) {
                    return String.format("%s(%s)", DorisType.DATETIME_V2, 0);
                } else if (length > ZERO_PRECISION_TIMESTAMP_COLUMN_SIZE + 1) {
                    // Timestamp with a fraction of seconds.
                    // For example, 2024-01-01 01:01:01.1
                    // The decimal point will occupy 1 character.
                    // Thus,the length of the timestamp is 21.
                    return String.format(
                            "%s(%s)",
                            DorisType.DATETIME_V2,
                            Math.min(
                                    length - ZERO_PRECISION_TIMESTAMP_COLUMN_SIZE - 1,
                                    DorisConstant.MAX_SUPPORTED_DATE_TIME_PRECISION));
                } else if (length <= DorisConstant.MAX_TIMESTAMP_PRECISION) {
                    // For Debezium JSON data, the timestamp/datetime length ranges from 0 to 9.
                    return String.format(
                            "%s(%s)",
                            DorisType.DATETIME_V2, Math.min(length, DorisConstant.MAX_SUPPORTED_DATE_TIME_PRECISION));
                } else {
                    throw new UnsupportedOperationException(
                            "Unsupported length: " + length + " for MySQL TIMESTAMP/DATETIME types");
                }
            case CHAR:
            case VARCHAR:
                Asserts.checkNotNull(length, "VARCHAR length is null");
                return length * 3 > 65533 ? DorisType.STRING : String.format("%s(%s)", DorisType.VARCHAR, length * 3);
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT:
            case ENUM:
            case TIME:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
            case LONGBLOB:
            case BINARY:
            case VARBINARY:
            case SET:
                return DorisType.STRING;
            case JSON:
                return DorisType.JSONB;
            default:
                throw new UnsupportedOperationException("Unsupported MySQL Type: " + type);
        }
    }
}
