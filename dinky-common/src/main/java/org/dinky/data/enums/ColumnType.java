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

package org.dinky.data.enums;

/**
 * ColumnType
 *
 * @since 2022/2/17 10:59
 */
public enum ColumnType {
    STRING("java.lang.String", "STRING"),
    JAVA_LANG_BOOLEAN("java.lang.Boolean", "BOOLEAN"),
    BOOLEAN("Boolean", "BOOLEAN NOT NULL"),
    JAVA_LANG_BYTE("java.lang.Byte", "TINYINT"),
    BYTE("byte", "TINYINT NOT NULL"),
    JAVA_LANG_SHORT("java.lang.Short", "SMALLINT"),
    SHORT("short", "SMALLINT NOT NULL"),
    INTEGER("java.lang.Integer", "INT"),
    INT("int", "INT NOT NULL"),
    JAVA_LANG_LONG("java.lang.Long", "BIGINT"),
    LONG("long", "BIGINT NOT NULL"),
    JAVA_LANG_FLOAT("java.lang.Float", "FLOAT"),
    FLOAT("float", "FLOAT NOT NULL"),
    JAVA_LANG_DOUBLE("java.lang.Double", "DOUBLE"),
    DOUBLE("double", "DOUBLE NOT NULL"),
    DATE("java.sql.Date", "DATE"),
    LOCAL_DATE("java.time.LocalDate", "DATE"),
    TIME("java.sql.Time", "TIME"),
    LOCALTIME("java.time.LocalTime", "TIME"),
    TIMESTAMP("java.sql.Timestamp", "TIMESTAMP"),
    LOCAL_DATETIME("java.time.LocalDateTime", "TIMESTAMP"),
    OFFSET_DATETIME("java.time.OffsetDateTime", "TIMESTAMP WITH TIME ZONE"),
    INSTANT("java.time.Instant", "TIMESTAMP_LTZ"),
    DURATION("java.time.Duration", "INVERVAL SECOND"),
    PERIOD("java.time.Period", "INTERVAL YEAR TO MONTH"),
    DECIMAL("java.math.BigDecimal", "DECIMAL"),
    BYTES("byte[]", "BYTES"),
    T("T[]", "ARRAY"),
    MAP("java.util.Map<K, V>", "MAP");

    private String javaType;
    private String flinkType;

    ColumnType(String javaType, String flinkType) {
        this.javaType = javaType;
        this.flinkType = flinkType;
    }

    public String getJavaType() {
        return javaType;
    }

    public String getFlinkType() {
        return flinkType;
    }
}
