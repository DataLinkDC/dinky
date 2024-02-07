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

package org.dinky.cdc.debezium;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.SchemaBuilder;

import io.debezium.spi.converter.CustomConverter;
import io.debezium.spi.converter.RelationalColumn;

/**
 * 处理 Debezium 源库 时间转换的问题
 * Debezium 默认将源库中 datetime 类型转成 UTC 的时间戳({@link io.debezium.time.Timestamp})，时区是写死的没法儿改，
 * 导致数据库中设置的 UTC+8，到目标库中变成了多八个小时的 long 型时间戳
 * Debezium 默认将源库中的 timestamp 类型转成UTC的字符串。
 * 以下是 mysql 中时间字段类型和 debezium 中字段类型的对应关系：
 * | mysql                               | mysql-binlog-connector                   | debezium                          |
 * | ----------------------------------- | ---------------------------------------- | --------------------------------- |
 * | date<br>(2021-01-28)                | LocalDate<br/>(2021-01-28)               | Integer<br/>(18655)               |
 * | time<br/>(17:29:04)                 | Duration<br/>(PT17H29M4S)                | Long<br/>(62944000000)            |
 * | timestamp<br/>(2021-01-28 17:29:04) | ZonedDateTime<br/>(2021-01-28T09:29:04Z) | String<br/>(2021-01-28T09:29:04Z) |
 * | Datetime<br/>(2021-01-28 17:29:04)  | LocalDateTime<br/>(2021-01-28T17:29:04)  | Long<br/>(1611854944000)          |
 *
 * @author <a href="mailto:kindbgen@gmail.com">Kindbgen<a/>
 * @description 自定义 debezium 转换器
 * @date 2024/2/6
 * @see io.debezium.connector.mysql.converters.TinyIntOneToBooleanConverter
 */
public class DebeziumCustomConverter implements CustomConverter<SchemaBuilder, RelationalColumn> {

    private static final Logger logger = LoggerFactory.getLogger(DebeziumCustomConverter.class);
    protected static final String DATE_FORMAT = "yyyy-MM-dd";
    protected static final String TIME_FORMAT = "HH:mm:ss";
    protected static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    protected DateTimeFormatter dateFormatter;
    protected DateTimeFormatter timeFormatter;
    protected DateTimeFormatter datetimeFormatter;
    protected SchemaBuilder schemaBuilder;
    protected String databaseType;
    protected String schemaNamePrefix;
    // 获取默认时区
    protected final ZoneId zoneId = ZoneOffset.systemDefault();

    @Override
    public void configure(Properties properties) {
        // 必填参数：database.type。获取数据库的类型，暂时支持mysql、sqlserver、oracle、postgresql
        this.databaseType = properties.getProperty("database.type");
        // 如果未设置，或者设置的不是mysql、sqlserver、oracle、postgresql，则抛出异常。
        switch (DataBaseType.get(this.databaseType)) {
            case MYSQL:
            case SQLSERVER:
            case ORACLE:
            case POSTGRESQL:
                break;
            default:
                String errMsg = "Not support " + databaseType + " database type";
                logger.error(errMsg);
                throw new UnsupportedOperationException(errMsg);
        }
        // 选填参数：format.date、format.time、format.datetime。获取时间格式化的格式
        String dateFormat = properties.getProperty("format.date", DATE_FORMAT);
        String timeFormat = properties.getProperty("format.time", TIME_FORMAT);
        String datetimeFormat = properties.getProperty("format.datetime", DATETIME_FORMAT);
        // 获取自身类的包名+数据库类型为默认schema.name
        String className = this.getClass().getName();
        // 查看是否设置schema.name.prefix
        this.schemaNamePrefix = properties.getProperty("schema.name.prefix", className + "." + this.databaseType);
        // 初始化时间格式化器
        dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
        timeFormatter = DateTimeFormatter.ofPattern(timeFormat);
        datetimeFormatter = DateTimeFormatter.ofPattern(datetimeFormat);
    }

    @Override
    public void converterFor(
            RelationalColumn relationalColumn, ConverterRegistration<SchemaBuilder> converterRegistration) {
        schemaBuilder = null;
    }

    public String failConvert(Object value, String type) {
        String valueClass = this.getClassName(value);
        String valueString = valueClass == null ? null : value.toString();
        return valueString;
    }

    public String getClassName(Object value) {
        if (value == null) {
            return null;
        }
        return value.getClass().getName();
    }
}
