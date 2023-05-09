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

package org.dinky.utils;

import org.apache.flink.table.types.logical.BigIntType;
import org.apache.flink.table.types.logical.DateType;
import org.apache.flink.table.types.logical.DecimalType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.TimestampType;
import org.apache.flink.table.types.logical.VarBinaryType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;

import javax.xml.bind.DatatypeConverter;

/** @className: org.dinky.utils.ObjectConvertUtil @Description: */
public class ObjectConvertUtil {

    public static Object convertValue(Object value, LogicalType logicalType) {
        return ObjectConvertUtil.convertValue(value, logicalType, null);
    }

    public static Object convertValue(Object value, LogicalType logicalType, ZoneId sinkTimeZone) {
        if (value == null) {
            return null;
        }
        if (sinkTimeZone == null) {
            sinkTimeZone = ZoneId.of("UTC");
        }
        if (logicalType instanceof DateType) {
            if (value instanceof Integer) {
                return Instant.ofEpochMilli(((Integer) value).longValue())
                        .atZone(sinkTimeZone)
                        .toLocalDate();
            } else {
                return Instant.ofEpochMilli((long) value)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }
        } else if (logicalType instanceof TimestampType) {
            if (value instanceof Integer) {
                return Instant.ofEpochMilli(((Integer) value).longValue())
                        .atZone(sinkTimeZone)
                        .toLocalDateTime();
            } else if (value instanceof String) {
                return Instant.parse((String) value)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            } else {
                return Instant.ofEpochMilli((long) value).atZone(sinkTimeZone).toLocalDateTime();
            }
        } else if (logicalType instanceof DecimalType) {
            return new BigDecimal(String.valueOf(value));
        } else if (logicalType instanceof BigIntType) {
            if (value instanceof Integer) {
                return ((Integer) value).longValue();
            } else {
                return value;
            }
        } else if (logicalType instanceof VarBinaryType) {
            // VARBINARY AND BINARY is converted to String with encoding base64 in FlinkCDC.
            if (value instanceof String) {
                return DatatypeConverter.parseBase64Binary((String) value);
            } else {
                return value;
            }
        } else {
            return value;
        }
    }
}
