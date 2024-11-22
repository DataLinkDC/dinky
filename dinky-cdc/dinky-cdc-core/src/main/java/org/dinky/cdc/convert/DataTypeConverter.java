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

package org.dinky.cdc.convert;

import org.dinky.assertion.Asserts;
import org.dinky.data.model.Column;

import org.apache.flink.table.data.DecimalData;
import org.apache.flink.table.data.StringData;
import org.apache.flink.table.data.TimestampData;
import org.apache.flink.table.types.logical.BigIntType;
import org.apache.flink.table.types.logical.BooleanType;
import org.apache.flink.table.types.logical.DateType;
import org.apache.flink.table.types.logical.DecimalType;
import org.apache.flink.table.types.logical.DoubleType;
import org.apache.flink.table.types.logical.FloatType;
import org.apache.flink.table.types.logical.IntType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.SmallIntType;
import org.apache.flink.table.types.logical.TimeType;
import org.apache.flink.table.types.logical.TimestampType;
import org.apache.flink.table.types.logical.TinyIntType;
import org.apache.flink.table.types.logical.VarBinaryType;
import org.apache.flink.table.types.logical.VarCharType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.DatatypeConverter;

public class DataTypeConverter {

    static final long MILLISECONDS_PER_SECOND = TimeUnit.SECONDS.toMillis(1);
    static final long NANOSECONDS_PER_MILLISECOND = TimeUnit.MILLISECONDS.toNanos(1);
    static final long NANOSECONDS_PER_DAY = TimeUnit.DAYS.toNanos(1);
    public static final long MILLIS_PER_DAY = 86400000L; // = 24 * 60 * 60 * 1000

    public static LogicalType getLogicalType(Column column) {
        switch (column.getJavaType()) {
            case BOOLEAN:
            case JAVA_LANG_BOOLEAN:
                return new BooleanType();
            case BYTE:
            case JAVA_LANG_BYTE:
                return new TinyIntType();
            case SHORT:
            case JAVA_LANG_SHORT:
                return new SmallIntType();
            case LONG:
            case JAVA_LANG_LONG:
                return new BigIntType();
            case FLOAT:
            case JAVA_LANG_FLOAT:
                return new FloatType();
            case DOUBLE:
            case JAVA_LANG_DOUBLE:
                return new DoubleType();
            case DECIMAL:
                if (column.getPrecision() == null || column.getPrecision() == 0) {
                    return new DecimalType(38, column.getScale());
                } else {
                    return new DecimalType(column.getPrecision(), column.getScale());
                }
            case INT:
            case INTEGER:
                return new IntType();
            case TIME:
            case LOCALTIME:
                return new TimeType(
                        column.isNullable(),
                        column.getLength() == 0
                                ? (column.getPrecision() == null ? 0 : column.getPrecision())
                                : column.getLength());
            case DATE:
            case LOCAL_DATE:
                return new DateType();
            case LOCAL_DATETIME:
            case TIMESTAMP:
                if (Asserts.isNotNull(column.getLength())) {
                    return new TimestampType(column.getLength());
                } else {
                    return new TimestampType(3);
                }
            case BYTES:
                return new VarBinaryType(Integer.MAX_VALUE);
            case STRING:
            default:
                return new VarCharType(Asserts.isNull(column.getLength()) ? Integer.MAX_VALUE : column.getLength());
        }
    }

    public static Object convertToRow(Object value, LogicalType logicalType, ZoneId timeZone) {
        if (Asserts.isNull(value)) {
            return Optional.empty();
        }
        switch (logicalType.getTypeRoot()) {
            case BOOLEAN:
                return convertToBoolean(value);
            case TINYINT:
                return convertToByte(value);
            case SMALLINT:
                return convertToShort(value);
            case INTEGER:
                return convertToInt(value);
            case BIGINT:
                return convertToLong(value);
            case DATE:
                return convertToDate(value);
            case TIME_WITHOUT_TIME_ZONE:
                return convertToTime(value, logicalType, timeZone);
            case TIMESTAMP_WITHOUT_TIME_ZONE:
                return convertToTimestamp(value, logicalType);
            case TIMESTAMP_WITH_TIME_ZONE:
                return convertToTimestampWithTimeZone(value, logicalType, timeZone);
            case TIMESTAMP_WITH_LOCAL_TIME_ZONE:
                return convertToTimestampWithLocalTimeZone(value, logicalType);
            case FLOAT:
                return convertToFloat(value);
            case DOUBLE:
                return convertToDouble(value);
            case CHAR:
            case VARCHAR:
                return convertToString(value);
            case BINARY:
            case VARBINARY:
                return convertToBinary(value);
            case DECIMAL:
                return convertToDecimal(value, logicalType);
            case ROW:
                return value;
            case ARRAY:
            case MAP:
            default:
                throw new UnsupportedOperationException("Unsupported type: " + logicalType);
        }
    }

    public static Object convertToRowData(Object value, LogicalType logicalType, ZoneId timeZone) {
        if (Asserts.isNull(value)) {
            return Optional.empty();
        }
        switch (logicalType.getTypeRoot()) {
            case BOOLEAN:
                return convertToBoolean(value);
            case TINYINT:
                return convertToByte(value);
            case SMALLINT:
                return convertToShort(value);
            case INTEGER:
                return convertToInt(value);
            case BIGINT:
                return convertToLong(value);
            case DATE:
                return convertToDate(value);
            case TIME_WITHOUT_TIME_ZONE:
                return convertToTime(value, logicalType, timeZone);
            case TIMESTAMP_WITHOUT_TIME_ZONE:
                return convertToTimestampData(value, logicalType);
            case TIMESTAMP_WITH_TIME_ZONE:
                return convertToTimestampDataWithTimeZone(value, logicalType, timeZone);
            case TIMESTAMP_WITH_LOCAL_TIME_ZONE:
                return convertToTimestampDataWithLocalTimeZone(value, logicalType);
            case FLOAT:
                return convertToFloat(value);
            case DOUBLE:
                return convertToDouble(value);
            case CHAR:
            case VARCHAR:
                return convertToStringData(value);
            case BINARY:
            case VARBINARY:
                return convertToBinary(value);
            case DECIMAL:
                return convertToDecimalData(value, logicalType);
            case ROW:
                return value;
            case ARRAY:
            case MAP:
            default:
                throw new UnsupportedOperationException("Unsupported type: " + logicalType);
        }
    }

    private static Object convertToBoolean(Object obj) {
        if (obj instanceof Boolean) {
            return obj;
        } else if (obj instanceof Byte) {
            return (byte) obj == 1;
        } else if (obj instanceof Short) {
            return (short) obj == 1;
        } else if (obj instanceof Number) {
            return obj.equals(1);
        } else {
            return Boolean.parseBoolean(obj.toString());
        }
    }

    private static Object convertToByte(Object obj) {
        return Byte.parseByte(obj.toString());
    }

    private static Object convertToShort(Object obj) {
        return Short.parseShort(obj.toString());
    }

    private static Object convertToInt(Object obj) {
        if (obj instanceof Integer) {
            return obj;
        } else if (obj instanceof Long) {
            return ((Long) obj).intValue();
        } else {
            return Integer.parseInt(obj.toString());
        }
    }

    private static Object convertToLong(Object obj) {
        if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        } else if (obj instanceof Long) {
            return obj;
        } else {
            return Long.parseLong(obj.toString());
        }
    }

    private static Object convertToFloat(Object obj) {
        if (obj instanceof Float) {
            return obj;
        } else if (obj instanceof Double) {
            return ((Double) obj).floatValue();
        } else {
            return Float.parseFloat(obj.toString());
        }
    }

    private static Object convertToDouble(Object obj) {
        if (obj instanceof Float) {
            return ((Float) obj).doubleValue();
        } else if (obj instanceof Double) {
            return obj;
        } else {
            return Double.parseDouble(obj.toString());
        }
    }

    private static Object convertToDate(Object obj) {
        return toLocalDate(obj);
    }

    private static LocalDate toLocalDate(Object obj) {
        if (obj instanceof Long) {
            // Assume the value is the epoch day number
            return LocalDate.ofEpochDay((Long) obj);
        }
        if (obj instanceof Integer) {
            // Assume the value is the epoch day number
            return LocalDate.ofEpochDay((Integer) obj);
        }
        throw new IllegalArgumentException("Unable to convert to LocalDate from unexpected value '"
                + obj
                + "' of type "
                + obj.getClass().getName());
    }

    private static Object convertToTime(Object obj, LogicalType logicalType, ZoneId timeZone) {
        TimeType timeType = (TimeType) logicalType;
        if (obj instanceof Number) {
            Number number = (Number) obj;
            long value = number.longValue();
            if (value > MILLIS_PER_DAY) {
                value = value / 1000;
                if (value > MILLIS_PER_DAY) {
                    value = value / 1000;
                }
            }
            if (timeType.getPrecision() == 0) {
                return Instant.ofEpochSecond(value).atZone(timeZone).toLocalTime();
            }
            if (timeType.getPrecision() == 3) {
                return Instant.ofEpochMilli(value).atZone(timeZone).toLocalTime();
            }
            if (timeType.getPrecision() == 6) {
                return Instant.ofEpochMilli(value % 1000).atZone(timeZone).toLocalTime();
            }
            return Instant.ofEpochMilli(value % 1000000).atZone(timeZone).toLocalTime();
        }
        throw new IllegalArgumentException("Unable to convert to LocalTime from unexpected value '"
                + obj
                + "' of type "
                + obj.getClass().getName());
    }

    private static LocalTime toLocalTime(Object obj, ZoneId timeZone) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Long) {
            return Instant.ofEpochMilli((long) obj).atZone(timeZone).toLocalTime();
        }
        if (obj instanceof LocalTime) {
            return (LocalTime) obj;
        }
        if (obj instanceof LocalDateTime) {
            return ((LocalDateTime) obj).toLocalTime();
        }
        if (obj instanceof java.sql.Date) {
            throw new IllegalArgumentException(
                    "Unable to convert to LocalDate from a java.sql.Date value '" + obj + "'");
        }
        if (obj instanceof java.sql.Time) {
            java.sql.Time time = (java.sql.Time) obj;
            long millis = (int) (time.getTime() % MILLISECONDS_PER_SECOND);
            int nanosOfSecond = (int) (millis * NANOSECONDS_PER_MILLISECOND);
            return LocalTime.of(time.getHours(), time.getMinutes(), time.getSeconds(), nanosOfSecond);
        }
        if (obj instanceof java.sql.Timestamp) {
            java.sql.Timestamp timestamp = (java.sql.Timestamp) obj;
            return LocalTime.of(
                    timestamp.getHours(), timestamp.getMinutes(), timestamp.getSeconds(), timestamp.getNanos());
        }
        if (obj instanceof java.util.Date) {
            java.util.Date date = (java.util.Date) obj;
            long millis = (int) (date.getTime() % MILLISECONDS_PER_SECOND);
            int nanosOfSecond = (int) (millis * NANOSECONDS_PER_MILLISECOND);
            return LocalTime.of(date.getHours(), date.getMinutes(), date.getSeconds(), nanosOfSecond);
        }
        if (obj instanceof Duration) {
            Long value = ((Duration) obj).toNanos();
            if (value >= 0 && value <= NANOSECONDS_PER_DAY) {
                return LocalTime.ofNanoOfDay(value);
            } else {
                throw new IllegalArgumentException(
                        "Time values must use number of milliseconds greater than 0 and less than 86400000000000");
            }
        }
        throw new IllegalArgumentException("Unable to convert to LocalTime from unexpected value '"
                + obj
                + "' of type "
                + obj.getClass().getName());
    }

    private static Object convertToTimestamp(Object obj, LogicalType logicalType) {
        if (obj instanceof Integer) {
            return Instant.ofEpochMilli(((Integer) obj).longValue())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } else if (obj instanceof String) {
            return Instant.parse((String) obj).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (obj instanceof Long) {
            TimestampType logicalType1 = (TimestampType) logicalType;
            if (logicalType1.getPrecision() == 3) {
                return Instant.ofEpochMilli((long) obj)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            } else if (logicalType1.getPrecision() > 3) {
                return Instant.ofEpochMilli(((long) obj) / (long) Math.pow(10, logicalType1.getPrecision() - 3))
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }
            return Instant.ofEpochSecond(((long) obj))
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        throw new IllegalArgumentException("Unable to convert to TIMESTAMP from unexpected value '"
                + obj
                + "' of type "
                + obj.getClass().getName());
    }

    private static Object convertToTimestampData(Object obj, LogicalType logicalType) {
        return TimestampData.fromLocalDateTime((LocalDateTime) convertToTimestamp(obj, logicalType));
    }

    private static Object convertToTimestampWithTimeZone(Object obj, LogicalType logicalType, ZoneId timeZone) {
        if (obj instanceof Integer) {
            return Instant.ofEpochMilli(((Integer) obj).longValue())
                    .atZone(timeZone)
                    .toLocalDateTime();
        } else if (obj instanceof String) {
            return Instant.parse((String) obj).atZone(timeZone).toLocalDateTime();
        } else if (obj instanceof Long) {
            TimestampType logicalType1 = (TimestampType) logicalType;
            if (logicalType1.getPrecision() == 3) {
                return Instant.ofEpochMilli((long) obj).atZone(timeZone).toLocalDateTime();
            } else if (logicalType1.getPrecision() > 3) {
                return Instant.ofEpochMilli(((long) obj) / (long) Math.pow(10, logicalType1.getPrecision() - 3))
                        .atZone(timeZone)
                        .toLocalDateTime();
            }
            return Instant.ofEpochSecond(((long) obj)).atZone(timeZone).toLocalDateTime();
        }
        throw new IllegalArgumentException("Unable to convert to TIMESTAMP from unexpected value '"
                + obj
                + "' of type "
                + obj.getClass().getName());
    }

    private static Object convertToTimestampDataWithTimeZone(Object obj, LogicalType logicalType, ZoneId timeZone) {
        return TimestampData.fromLocalDateTime(
                (LocalDateTime) convertToTimestampWithTimeZone(obj, logicalType, timeZone));
    }

    private static Object convertToTimestampWithLocalTimeZone(Object obj, LogicalType logicalType) {
        if (obj instanceof Integer) {
            return Instant.ofEpochMilli(((Integer) obj).longValue())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } else if (obj instanceof String) {
            return Instant.parse((String) obj).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (obj instanceof Long) {
            TimestampType logicalType1 = (TimestampType) logicalType;
            if (logicalType1.getPrecision() == 3) {
                return Instant.ofEpochMilli((long) obj)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            } else if (logicalType1.getPrecision() > 3) {
                return Instant.ofEpochMilli(((long) obj) / (long) Math.pow(10, logicalType1.getPrecision() - 3))
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }
            return Instant.ofEpochSecond(((long) obj))
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        throw new IllegalArgumentException("Unable to convert to TIMESTAMP from unexpected value '"
                + obj
                + "' of type "
                + obj.getClass().getName());
    }

    private static Object convertToTimestampDataWithLocalTimeZone(Object obj, LogicalType logicalType) {
        return TimestampData.fromLocalDateTime((LocalDateTime) convertToTimestampWithLocalTimeZone(obj, logicalType));
    }

    private static Object convertToString(Object obj) {
        return String.valueOf(obj);
    }

    private static Object convertToStringData(Object obj) {
        return StringData.fromString(String.valueOf(obj));
    }

    private static Object convertToBinary(Object obj) {
        if (obj instanceof byte[]) {
            return obj;
        } else if (obj instanceof ByteBuffer) {
            ByteBuffer byteBuffer = (ByteBuffer) obj;
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            return bytes;
        } else if (obj instanceof String) {
            return DatatypeConverter.parseBase64Binary(String.valueOf(obj));
        } else {
            throw new UnsupportedOperationException(
                    "Unsupported BYTES value type: " + obj.getClass().getSimpleName());
        }
    }

    private static Object convertToDecimal(Object obj, LogicalType logicalType) {
        DecimalType decimalType = (DecimalType) logicalType;
        if (obj instanceof BigDecimal) {
            return obj;
        } else if (obj instanceof Number) {
            Number number = (Number) obj;
            return BigDecimal.valueOf(number.longValue(), decimalType.getScale());
        } else if (obj instanceof String) {
            MathContext mathContext = new MathContext(((DecimalType) logicalType).getPrecision());
            return new BigDecimal(String.valueOf(obj), mathContext);
        } else {
            throw new UnsupportedOperationException(
                    "Unsupported Decimal value type: " + obj.getClass().getSimpleName());
        }
    }

    private static Object convertToDecimalData(Object obj, LogicalType logicalType) {
        DecimalType decimalType = (DecimalType) logicalType;
        if (obj instanceof BigDecimal) {
            return DecimalData.fromBigDecimal(
                    new BigDecimal(String.valueOf(obj)), decimalType.getPrecision(), decimalType.getScale());
        } else if (obj instanceof Number) {
            Number number = (Number) obj;
            return DecimalData.fromBigDecimal(
                    BigDecimal.valueOf(number.longValue(), decimalType.getScale()),
                    decimalType.getPrecision(),
                    decimalType.getScale());
        } else if (obj instanceof String) {
            return DecimalData.fromBigDecimal(
                    new BigDecimal(String.valueOf(obj)), decimalType.getPrecision(), decimalType.getScale());
        } else {
            throw new UnsupportedOperationException(
                    "Unsupported Decimal value type: " + obj.getClass().getSimpleName());
        }
    }
}
