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
import org.dinky.metadata.convert.DorisType;

public class OracleType {
    private static final String VARCHAR2 = "VARCHAR2";
    private static final String NVARCHAR2 = "NVARCHAR2";
    private static final String NUMBER = "NUMBER";
    private static final String FLOAT = "FLOAT";
    private static final String LONG = "LONG";
    private static final String DATE = "DATE";
    private static final String BINARY_FLOAT = "BINARY_FLOAT";
    private static final String BINARY_DOUBLE = "BINARY_DOUBLE";
    private static final String TIMESTAMP = "TIMESTAMP";
    private static final String INTERVAL = "INTERVAL";
    private static final String RAW = "RAW";
    private static final String LONG_RAW = "LONG RAW";
    private static final String ROWID = "ROWID";
    private static final String UROWID = "UROWID";
    private static final String CHAR = "CHAR";
    private static final String NCHAR = "NCHAR";
    private static final String CLOB = "CLOB";
    private static final String NCLOB = "NCLOB";
    private static final String BLOB = "BLOB";
    private static final String BFILE = "BFILE";

    public static String toDorisType(String oracleType, Integer precision, Integer scale) {
        oracleType = oracleType.toUpperCase();
        if (oracleType.startsWith(INTERVAL)) {
            oracleType = oracleType.substring(0, 8);
        } else if (oracleType.startsWith(TIMESTAMP)) {
            return String.format("%s(%s)", DorisType.DATETIME_V2, Math.min(scale, 6));
        }
        switch (oracleType) {
            case NUMBER:
                if (scale <= 0) {
                    precision -= scale;
                    if (precision < 3) {
                        return DorisType.TINYINT;
                    } else if (precision < 5) {
                        return DorisType.SMALLINT;
                    } else if (precision < 10) {
                        return DorisType.INT;
                    } else if (precision < 19) {
                        return DorisType.BIGINT;
                    } else if (precision < 39) {
                        // LARGEINT supports up to 38 numbers.
                        return DorisType.LARGEINT;
                    } else {
                        return DorisType.STRING;
                    }
                }
                // scale > 0
                if (precision < scale) {
                    precision = scale;
                }
                return precision != null && precision <= 38
                        ? String.format(
                                "%s(%s,%s)", DorisType.DECIMAL_V3, precision, scale != null && scale >= 0 ? scale : 0)
                        : DorisType.STRING;
            case FLOAT:
                return DorisType.DOUBLE;
            case DATE:
                // can save date and time with second precision
                return DorisType.DATETIME_V2;
            case CHAR:
            case VARCHAR2:
            case NCHAR:
            case NVARCHAR2:
                Asserts.checkNotNull(precision, "NVARCHAR2 precision is null");
                return precision * 3 > 65533
                        ? DorisType.STRING
                        : String.format("%s(%s)", DorisType.VARCHAR, precision * 3);
            case LONG:
            case RAW:
            case LONG_RAW:
            case INTERVAL:
            case BLOB:
            case CLOB:
            case NCLOB:
                return DorisType.STRING;
            case BFILE:
            case BINARY_FLOAT:
            case BINARY_DOUBLE:
            default:
                throw new UnsupportedOperationException("Unsupported Oracle Type: " + oracleType);
        }
    }
}
