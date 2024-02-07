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

package org.dinky.data.model.Doris;

public class DefaultDorisTypeConverters implements DorisTypeConverters {
    public static final String STRING = "java.lang.String";
    public static final String INT = "int";
    public static final String LONG = "long";
    public static final String DOUBLE = "double";
    public static final String BOOLEAN = "java.lang.Boolean";
    public static final String JAVA_LANG_BOOLEAN = "Boolean";
    public static final String DATE = "java.sql.Date";
    public static final String TIMESTAMP = "java.sql.Timestamp";
    public static final String DECIMAL = "java.math.BigDecimal";
    public static final String BYTE = "java.lang.Byte";
    public static final String JAVA_LANG_BYTE = "Byte";
    public static final String JAVA_LANG_SHORT = "java.lang.Short";
    public static final String SHORT = "short";
    public static final String INTEGER = "java.lang.Integer";
    public static final String JAVA_LANG_LONG = "java.lang.Long";
    public static final String JAVA_LANG_FLOAT = "java.lang.Float";
    public static final String FLOAT = "float";
    public static final String JAVA_LANG_DOUBLE = "java.lang.Double";
    public static final String LOCAL_DATE = "java.time.LocalDate";
    public static final String LOCAL_TIME = "java.time.LocalTime";
    public static final String TIME = "java.sql.Time";
    public static final String LOCAL_DATETIME = "java.time.LocalDateTime";
    public static final String OFFSET_DATETIME = "java.time.OffsetDateTime";
    public static final String INSTANT = "java.time.Instant";
    public static final String DURATION = "java.time.Duration";
    public static final String PERIOD = "java.time.Period";
    public static final String BYTES = "byte[]";
    public static final String T = "T[]";
    public static final String MAP = "java.util.Map<K, V>";

    @Override
    public String toDorisType(String type, Integer length, Integer scale) {
        switch (type) {
            case SHORT:
            case JAVA_LANG_SHORT:
                return DorisType.SMALLINT;
            case INTEGER:
            case LONG:
            case JAVA_LANG_LONG:
            case INT:
                return DorisType.INT;
            case FLOAT:
            case JAVA_LANG_FLOAT:
                return DorisType.FLOAT;
            case DOUBLE:
            case JAVA_LANG_DOUBLE:
                return DorisType.DOUBLE;
            case BOOLEAN:
            case JAVA_LANG_BOOLEAN:
                return DorisType.BOOLEAN;
            case STRING:
            case TIME:
            case DECIMAL:
            case TIMESTAMP:
            case LOCAL_TIME:
            case LOCAL_DATETIME:
            case OFFSET_DATETIME:
            case INSTANT:
            case DURATION:
            case PERIOD:
            case BYTES:
                return DorisType.STRING;
            case DATE:
            case LOCAL_DATE:
                return DorisType.DATE;
            case BYTE:
            case JAVA_LANG_BYTE:
                return DorisType.TINYINT;
            case T:
                return DorisType.ARRAY;
            case MAP:
                return DorisType.MAP;
            default:
                throw new UnsupportedOperationException("Unsupported type: " + type);
        }
    }
}
