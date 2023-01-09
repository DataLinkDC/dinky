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

package com.dlink.cdc.kafka;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.AbstractSinkBuilder;
import com.dlink.cdc.CDCBuilder;
import com.dlink.cdc.SinkBuilder;
import com.dlink.executor.CustomTableEnvironment;
import com.dlink.model.Column;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.model.Schema;
import com.dlink.model.Table;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.logical.BigIntType;
import org.apache.flink.table.types.logical.BooleanType;
import org.apache.flink.table.types.logical.DateType;
import org.apache.flink.table.types.logical.DecimalType;
import org.apache.flink.table.types.logical.DoubleType;
import org.apache.flink.table.types.logical.FloatType;
import org.apache.flink.table.types.logical.IntType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.SmallIntType;
import org.apache.flink.table.types.logical.TimestampType;
import org.apache.flink.table.types.logical.TinyIntType;
import org.apache.flink.table.types.logical.VarBinaryType;
import org.apache.flink.table.types.logical.VarCharType;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

/**
 * @className: com.dlink.cdc.kafka.KafkaSinkSimpleBuilder
 */
public class KafkaSinkJsonBuilder extends AbstractSinkBuilder implements SinkBuilder, Serializable {

    public static final String KEY_WORD = "datastream-kafka-json";

    public KafkaSinkJsonBuilder() {
    }

    public KafkaSinkJsonBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public SinkBuilder create(FlinkCDCConfig config) {
        return new KafkaSinkJsonBuilder(config);
    }

    @Override
    public DataStreamSource build(
            CDCBuilder cdcBuilder,
            StreamExecutionEnvironment env,
            CustomTableEnvironment customTableEnvironment,
            DataStreamSource<String> dataStreamSource) {
        try {
            SingleOutputStreamOperator<Map> mapOperator = dataStreamSource.map(new MapFunction<String, Map>() {

                @Override
                public Map map(String value) throws Exception {
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readValue(value, Map.class);
                }
            });
            final List<Schema> schemaList = config.getSchemaList();
            final String schemaFieldName = config.getSchemaFieldName();
            if (Asserts.isNotNullString(config.getSink().get("topic"))) {
                SingleOutputStreamOperator<String> process = mapOperator.process(new KafkaProcessFunction(schemaList));
                process.addSink(new FlinkKafkaProducer<String>(config.getSink().get("brokers"),
                        config.getSink().get("topic"),
                        new SimpleStringSchema()));
            } else {
                if (Asserts.isNotNullCollection(schemaList)) {
                    for (Schema schema : schemaList) {
                        for (Table table : schema.getTables()) {
                            final String tableName = table.getName();
                            final String schemaName = table.getSchema();
                            SingleOutputStreamOperator<Map> filterOperator = mapOperator
                                    .filter(new FilterFunction<Map>() {

                                        @Override
                                        public boolean filter(Map value) throws Exception {
                                            LinkedHashMap source = (LinkedHashMap) value.get("source");
                                            return tableName.equals(source.get("table").toString())
                                                    && schemaName.equals(source.get(schemaFieldName).toString());
                                        }
                                    });
                            SingleOutputStreamOperator<String> stringOperator = filterOperator
                                    .process(new KafkaProcessFunction(schemaList));
                            stringOperator.addSink(new FlinkKafkaProducer<String>(config.getSink().get("brokers"),
                                    getSinkTableName(table),
                                    new SimpleStringSchema()));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("kafka sink error:", ex);
        }
        return dataStreamSource;
    }

    private static class KafkaProcessFunction extends ProcessFunction<Map, String> {

        private ObjectMapper objectMapper;
        private List<Schema> schemaList;

        public KafkaProcessFunction(List<Schema> schemaList) {
            this.schemaList = schemaList;
        }

        private void initializeObjectMapper() {
            this.objectMapper = new ObjectMapper();
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            // Hack time module to allow 'Z' at the end of string (i.e. javascript json's)
            javaTimeModule.addDeserializer(LocalDateTime.class,
                    new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
            objectMapper.registerModule(javaTimeModule);
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        }

        @Override
        public void processElement(Map value, Context ctx, Collector<String> out) throws Exception {
            Map after = null;
            Map before = null;
            String tsMs = value.get("ts_ms").toString();
            try {
                LinkedHashMap source = (LinkedHashMap) value.get("source");
                String tableName = null;
                String schemaName = null;
                Table tableObject = null;
                for (Schema schema : schemaList) {
                    for (Table table : schema.getTables()) {
                        final String tableName1 = table.getName();
                        final String schemaName1 = table.getSchema();
                        if (tableName1.equals(source.get("table").toString())
                                && schemaName1.equals(source.get("db").toString())) {
                            tableObject = table;
                            tableName = tableName1;
                            schemaName = schemaName1;
                            break;
                        }
                    }
                }
                if (tableObject != null) {
                    List<String> columnNameList = new LinkedList<>();
                    List<LogicalType> columnTypeList = new LinkedList<>();
                    buildColumn(columnNameList, columnTypeList, tableObject.getColumns());
                    switch (value.get("op").toString()) {
                        case "r":
                        case "c":
                            after = (Map) value.get("after");
                            for (int i = 0; i < columnNameList.size(); i++) {
                                String columnName = columnNameList.get(i);
                                Object columnNameValue = after.remove(columnName);
                                Object columnNameNewVal = convertValue(columnNameValue, columnTypeList.get(i));
                                after.put(columnName, columnNameNewVal);
                            }
                            after.put("__op", Integer.valueOf(0));
                            after.put("is_deleted", Integer.valueOf(0));
                            after.put("db", schemaName);
                            after.put("table", tableName);
                            after.put("ts_ms", tsMs);
                            break;
                        case "u":
                            before = (Map) value.get("before");
                            for (int i = 0; i < columnNameList.size(); i++) {
                                String columnName = columnNameList.get(i);
                                Object columnNameValue = before.remove(columnName);
                                Object columnNameNewVal = convertValue(columnNameValue, columnTypeList.get(i));
                                before.put(columnName, columnNameNewVal);
                            }
                            before.put("__op", Integer.valueOf(1));
                            before.put("is_deleted", Integer.valueOf(1));
                            before.put("db", schemaName);
                            before.put("table", tableName);
                            before.put("ts_ms", tsMs);

                            after = (Map) value.get("after");
                            for (int i = 0; i < columnNameList.size(); i++) {
                                String columnName = columnNameList.get(i);
                                Object columnNameValue = after.remove(columnName);
                                Object columnNameNewVal = convertValue(columnNameValue, columnTypeList.get(i));
                                after.put(columnName, columnNameNewVal);
                            }
                            after.put("__op", Integer.valueOf(0));
                            after.put("is_deleted", Integer.valueOf(0));
                            after.put("db", schemaName);
                            after.put("table", tableName);
                            after.put("ts_ms", tsMs);
                            break;
                        case "d":
                            before = (Map) value.get("before");
                            for (int i = 0; i < columnNameList.size(); i++) {
                                String columnName = columnNameList.get(i);
                                Object columnNameValue = before.remove(columnName);
                                Object columnNameNewVal = convertValue(columnNameValue, columnTypeList.get(i));
                                before.put(columnName, columnNameNewVal);
                            }
                            before.put("__op", Integer.valueOf(1));
                            before.put("is_deleted", Integer.valueOf(1));
                            before.put("db", schemaName);
                            before.put("table", tableName);
                            before.put("ts_ms", tsMs);
                            break;
                        default:
                    }
                }
            } catch (Exception e) {
                logger.error("SchameTable: {} - Exception:", e);
                throw e;
            }
            if (objectMapper == null) {
                initializeObjectMapper();
            }
            if (before != null) {
                out.collect(objectMapper.writeValueAsString(before));
            }
            if (after != null) {
                out.collect(objectMapper.writeValueAsString(after));
            }
        }

        protected void buildColumn(List<String> columnNameList, List<LogicalType> columnTypeList,
                List<Column> columns) {
            for (Column column : columns) {
                columnNameList.add(column.getName());
                columnTypeList.add(getLogicalType(column));
            }
        }

        public LogicalType getLogicalType(Column column) {
            switch (column.getJavaType()) {
                case STRING:
                    return new VarCharType();
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
                case DATE:
                case LOCALDATE:
                    return new DateType();
                case LOCALDATETIME:
                case TIMESTAMP:
                    return new TimestampType();
                case BYTES:
                    return new VarBinaryType(Integer.MAX_VALUE);
                default:
                    return new VarCharType();
            }
        }

        protected Object convertValue(Object value, LogicalType logicalType) {
            if (value == null) {
                return null;
            }
            if (logicalType instanceof DateType) {
                ZoneId utc = ZoneId.of("UTC");
                if (value instanceof Integer) {
                    return Instant.ofEpochMilli(((Integer) value).longValue()).atZone(utc).toLocalDate();
                } else {
                    return Instant.ofEpochMilli((long) value).atZone(utc).toLocalDate();
                }
            } else if (logicalType instanceof TimestampType) {
                if (value instanceof Integer) {
                    return Instant.ofEpochMilli(((Integer) value).longValue()).atZone(ZoneId.of("UTC"))
                            .toLocalDateTime();
                } else if (value instanceof String) {
                    return Instant.parse((String) value).atZone(ZoneId.systemDefault()).toLocalDateTime();
                } else {
                    return Instant.ofEpochMilli((long) value).atZone(ZoneId.of("UTC")).toLocalDateTime();
                }
            } else if (logicalType instanceof DecimalType) {
                return new BigDecimal((String) value);
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

    @Override
    public void addSink(
            StreamExecutionEnvironment env,
            DataStream<RowData> rowDataDataStream,
            Table table,
            List<String> columnNameList,
            List<LogicalType> columnTypeList) {
    }
}
