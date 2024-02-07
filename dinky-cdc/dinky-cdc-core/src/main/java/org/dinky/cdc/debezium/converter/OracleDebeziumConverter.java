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

package org.dinky.cdc.debezium.converter;

import org.dinky.cdc.debezium.DebeziumCustomConverter;

import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.SchemaBuilder;

import io.debezium.spi.converter.RelationalColumn;

/**
 * @author <a href="mailto:kindbgen@gmail.com">Kindbgen<a/>
 * @description Oracle 转换器
 * @date 2024/2/6
 */
public class OracleDebeziumConverter extends DebeziumCustomConverter {

    @Override
    public void converterFor(
            RelationalColumn relationalColumn, ConverterRegistration<SchemaBuilder> converterRegistration) {
        // 获取字段类型
        String columnType = relationalColumn.typeName().toUpperCase();
        this.registerConverter(columnType, converterRegistration);
    }

    public void registerConverter(String columnType, ConverterRegistration<SchemaBuilder> converterRegistration) {
        String schemaName = this.schemaNamePrefix + "." + columnType.toLowerCase();
        schemaBuilder = SchemaBuilder.string().name(schemaName);
        switch (columnType) {
            case "DATE":
                converterRegistration.register(schemaBuilder, value -> {
                    if (value == null) {
                        return null;
                    } else if (value instanceof java.sql.Date) {
                        return dateFormatter.format(((java.sql.Date) value).toLocalDate());
                    } else if (value instanceof java.time.LocalDate) {
                        return dateFormatter.format((java.time.LocalDate) value);
                    } else {
                        return this.failConvert(value, schemaName);
                    }
                });
                break;
            case "TIMESTAMP":
            case "TIMESTAMP WITH TIME ZONE":
            case "TIMESTAMP WITH LOCAL TIME ZONE":
                converterRegistration.register(schemaBuilder, value -> {
                    if (value == null) {
                        return null;
                    } else if (value instanceof java.sql.Timestamp) {
                        return datetimeFormatter.format(((java.sql.Timestamp) value).toLocalDateTime());
                    } else if (value instanceof java.time.LocalDateTime) {
                        return datetimeFormatter.format((java.time.LocalDateTime) value);
                    } else {
                        return this.failConvert(value, schemaName);
                    }
                });
                break;
            default:
                schemaBuilder = null;
                break;
        }
    }
}
