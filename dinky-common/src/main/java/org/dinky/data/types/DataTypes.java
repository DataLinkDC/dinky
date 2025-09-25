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

package org.dinky.data.types;

import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.Maps;

public enum DataTypes {
    ARRAY("ARRAY", new ArrayType(new VarCharType())),
    BIGINT("BIGINT", new BigIntType()),
    BINARY("BINARY", new BinaryType()),
    BOOLEAN("BOOLEAN", new BooleanType()),
    BYTES("BYTES", new VarBinaryType(VarBinaryType.MAX_LENGTH)),
    CHAR("CHAR", new CharType()),
    DATE("DATE", new DateType()),
    DAY("DAY", new DayTimeIntervalType(DayTimeIntervalType.DayTimeResolution.DAY)),
    DAY_TO_HOUR("DAY_TO_HOUR", new DayTimeIntervalType(DayTimeIntervalType.DayTimeResolution.DAY_TO_HOUR)),
    DAY_TO_MINUTE("DAY_TO_MINUTE", new DayTimeIntervalType(DayTimeIntervalType.DayTimeResolution.DAY_TO_MINUTE)),
    DAY_TO_SECOND("DAY", new DayTimeIntervalType(DayTimeIntervalType.DayTimeResolution.DAY_TO_SECOND)),
    DECIMAL("DECIMAL", new DecimalType()),
    DOUBLE("DOUBLE", new DoubleType()),
    FLOAT("FLOAT", new FloatType()),
    HOUR("HOUR", new DayTimeIntervalType(DayTimeIntervalType.DayTimeResolution.HOUR)),
    HOUR_TO_MINUTE("HOUR_TO_MINUTE", new DayTimeIntervalType(DayTimeIntervalType.DayTimeResolution.HOUR_TO_MINUTE)),
    HOUR_TO_SECOND("HOUR_TO_SECOND", new DayTimeIntervalType(DayTimeIntervalType.DayTimeResolution.HOUR_TO_SECOND)),
    INT("INT", new IntType()),
    MAP("MAP", new MapType(new VarCharType(), new VarCharType())),
    MINUTE("MINUTE", new DayTimeIntervalType(DayTimeIntervalType.DayTimeResolution.MINUTE)),
    MINUTE_TO_SECOND(
            "MINUTE_TO_SECOND", new DayTimeIntervalType(DayTimeIntervalType.DayTimeResolution.MINUTE_TO_SECOND)),
    MONTH("MONTH", new YearMonthIntervalType(YearMonthIntervalType.YearMonthResolution.MONTH)),
    SECOND("SECOND", new DayTimeIntervalType(DayTimeIntervalType.DayTimeResolution.SECOND)),
    SMALLINT("SMALLINT", new SmallIntType()),
    STRING("STRING", VarCharType.STRING_TYPE),
    TIME("TIME", new TimeType()),
    TIMESTAMP("TIMESTAMP", new TimestampType()),
    TIMESTAMP_LTZ("TIMESTAMP_LTZ", new LocalZonedTimestampType()),
    TINYINT("TINYINT", new TinyIntType()),
    VARBINARY("VARBINARY", new VarBinaryType()),
    VARCHAR("VARCHAR", new VarCharType()),
    YEAR("YEAR", new YearMonthIntervalType(YearMonthIntervalType.YearMonthResolution.YEAR)),
    YEAR_TO_MONTH("YEAR_TO_MONTH", new YearMonthIntervalType(YearMonthIntervalType.YearMonthResolution.YEAR_TO_MONTH)),
    TIMESTAMP_TZ("TIMESTAMP_TZ", new ZonedTimestampType());

    private final String value;
    private final LogicalType logicalType;

    private static final Map<String, DataTypes> DATA_TYPE_MAP = Maps.newHashMap();

    static {
        Arrays.stream(DataTypes.values()).forEach(dataType -> {
            DATA_TYPE_MAP.put(dataType.getValue(), dataType);
        });
    }

    DataTypes(String value, LogicalType logicalType) {
        this.value = value;
        this.logicalType = logicalType;
    }

    public String getValue() {
        return value;
    }

    public LogicalType getLogicalType() {
        return logicalType;
    }

    public LogicalType copyLogicalType(LogicalTypeParam logicalTypeParam) {
        return logicalType.copy(logicalTypeParam);
    }

    public LogicalType copyLogicalType(Boolean isNullable, Integer length, Integer precision, Integer scale) {
        return logicalType.copy(LogicalTypeParam.of(isNullable, length, precision, scale));
    }

    public LogicalType copyLogicalType(Boolean isNullable, Integer length, Integer precision) {
        return logicalType.copy(LogicalTypeParam.of(isNullable, length, precision));
    }

    public LogicalType copyLogicalType(Boolean isNullable, Integer length) {
        return logicalType.copy(LogicalTypeParam.of(isNullable, length));
    }

    public LogicalType copyLogicalType(Boolean isNullable) {
        return logicalType.copy(LogicalTypeParam.of(isNullable));
    }

    public ColumnType toColumnType(Boolean isNullable, Integer length, Integer precision, Integer scale) {
        return ColumnType.of(this, copyLogicalType(isNullable, length, precision, scale));
    }

    public ColumnType toColumnType(Boolean isNullable, Integer length, Integer precision) {
        return ColumnType.of(this, copyLogicalType(isNullable, length, precision));
    }

    public ColumnType toColumnType(Boolean isNullable, Integer length) {
        return ColumnType.of(this, copyLogicalType(isNullable, length));
    }

    public ColumnType toColumnType(Boolean isNullable) {
        return ColumnType.of(this, copyLogicalType(isNullable));
    }

    public static DataTypes getByValue(String value) {
        return DATA_TYPE_MAP.getOrDefault(value, STRING);
    }

    public static DataTypes of(String value) {
        if (DATA_TYPE_MAP.containsKey(value)) {
            return DATA_TYPE_MAP.get(value);
        }
        switch (LogicalTypeRoot.valueOf(value)) {
            case ARRAY:
                return ARRAY;
            case BIGINT:
                return BIGINT;
            case BINARY:
                return BINARY;
            case BOOLEAN:
                return BOOLEAN;
            case CHAR:
                return CHAR;
            case DATE:
                return DATE;
            case DECIMAL:
                return DECIMAL;
            case DOUBLE:
                return DOUBLE;
            case FLOAT:
                return FLOAT;
            case INTEGER:
                return INT;
            case MAP:
            case ROW:
                return MAP;
            case SMALLINT:
                return SMALLINT;
            case TIME_WITHOUT_TIME_ZONE:
                return TIME;
            case TIMESTAMP_WITH_TIME_ZONE:
                return TIMESTAMP;
            case TIMESTAMP_WITHOUT_TIME_ZONE:
                return TIMESTAMP_TZ;
            case TINYINT:
                return TINYINT;
            case VARBINARY:
                return VARBINARY;
            case VARCHAR:
                return VARCHAR;
            case INTERVAL_YEAR_MONTH:
                return YEAR;
            case TIMESTAMP_WITH_LOCAL_TIME_ZONE:
                return TIMESTAMP_LTZ;
            default:
                return STRING;
        }
    }
}
