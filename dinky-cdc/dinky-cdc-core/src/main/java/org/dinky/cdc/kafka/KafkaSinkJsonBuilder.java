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

package org.dinky.cdc.kafka;

import org.dinky.assertion.Asserts;
import org.dinky.cdc.AbstractSinkBuilder;
import org.dinky.cdc.SinkBuilder;
import org.dinky.cdc.convert.DataTypeConverter;
import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;
import org.dinky.executor.CustomTableEnvironment;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

public class KafkaSinkJsonBuilder extends AbstractSinkBuilder implements Serializable {

    public static final String KEY_WORD = "datastream-kafka-json";
    private final transient ObjectMapper objectMapper = new ObjectMapper();

    public KafkaSinkJsonBuilder() {}

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

    @SuppressWarnings("rawtypes")
    @Override
    public void build(
            StreamExecutionEnvironment env,
            CustomTableEnvironment customTableEnvironment,
            DataStreamSource<String> dataStreamSource) {
        try {
            init(env, customTableEnvironment);

            SingleOutputStreamOperator<Map> mapOperator = deserialize(dataStreamSource);
            logger.info("Build deserialize successful...");
            List<Schema> sortedSchemaList = getSortedSchemaList();
            final Map<String, Table> tableMap = new LinkedHashMap<>();
            for (Schema schema : sortedSchemaList) {
                for (Table table : schema.getTables()) {
                    tableMap.put(table.getSchemaTableName(), table);
                }
            }
            partitionByTableAndPrimarykey(mapOperator, tableMap);
            logger.info("Build partitionBy successful...");
            for (Schema schema : sortedSchemaList) {
                for (Table table : schema.getTables()) {
                    final String tableName = table.getName();
                    final String schemaName = table.getSchema();
                    SingleOutputStreamOperator<Map> singleOutputStreamOperator =
                            shunt(mapOperator, schemaName, tableName);
                    logger.info("Build shunt successful...");

                    List<String> columnNameList = new LinkedList<>();
                    List<LogicalType> columnTypeList = new LinkedList<>();
                    buildColumn(columnNameList, columnTypeList, table.getColumns());

                    SingleOutputStreamOperator<String> stringOperator = singleOutputStreamOperator
                            .process(new ProcessFunction<Map, String>() {

                                @Override
                                public void processElement(Map value, Context context, Collector<String> collector)
                                        throws Exception {
                                    Map after = null;
                                    Map before = null;
                                    String tsMs = value.get("ts_ms").toString();
                                    try {
                                        switch (value.get("op").toString()) {
                                            case "r":
                                            case "c":
                                                after = (Map) value.get("after");
                                                convertAttr(
                                                        columnNameList,
                                                        columnTypeList,
                                                        after,
                                                        value.get("op").toString(),
                                                        0,
                                                        schemaName,
                                                        tableName,
                                                        tsMs);
                                                break;
                                            case "u":
                                                before = (Map) value.get("before");
                                                convertAttr(
                                                        columnNameList,
                                                        columnTypeList,
                                                        before,
                                                        value.get("op").toString(),
                                                        1,
                                                        schemaName,
                                                        tableName,
                                                        tsMs);

                                                after = (Map) value.get("after");
                                                convertAttr(
                                                        columnNameList,
                                                        columnTypeList,
                                                        after,
                                                        value.get("op").toString(),
                                                        0,
                                                        schemaName,
                                                        tableName,
                                                        tsMs);
                                                break;
                                            case "d":
                                                before = (Map) value.get("before");
                                                convertAttr(
                                                        columnNameList,
                                                        columnTypeList,
                                                        before,
                                                        value.get("op").toString(),
                                                        1,
                                                        schemaName,
                                                        tableName,
                                                        tsMs);
                                                break;
                                            default:
                                        }
                                    } catch (Exception e) {
                                        logger.error("SchemaTable: {} - Exception:", e.toString());
                                        throw e;
                                    }
                                    if (objectMapper == null) {
                                        initializeObjectMapper();
                                    }
                                    if (before != null) {
                                        collector.collect(objectMapper.writeValueAsString(before));
                                    }
                                    if (after != null) {
                                        collector.collect(objectMapper.writeValueAsString(after));
                                    }
                                }
                            })
                            .name("SerializerWithMetadata");

                    String topic = getSinkTableName(table);
                    if (Asserts.isNotNullString(config.getSink().get("topic"))) {
                        topic = config.getSink().get("topic");
                    } else {
                        Map<String, String> tableTopicMap = this.getTableTopicMap();
                        if (tableTopicMap != null) {
                            String newTopic = tableTopicMap.get(tableName);
                            if (Asserts.isNotNullString(newTopic)) {
                                topic = newTopic;
                            }
                        }
                    }

                    stringOperator.addSink(
                            new FlinkKafkaProducer<String>(topic, new SimpleStringSchema(), getProperties()));
                }
            }
        } catch (Exception ex) {
            logger.error("kafka sink error:", ex);
        }
    }

    private void initializeObjectMapper() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // Hack time module to allow 'Z' at the end of string (i.e. javascript json's)
        javaTimeModule.addDeserializer(
                LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @SuppressWarnings("rawtypes")
    private void convertAttr(
            List<String> columnNameList,
            List<LogicalType> columnTypeList,
            Map value,
            String op,
            int isDeleted,
            String schemaName,
            String tableName,
            String tsMs) {
        for (int i = 0; i < columnNameList.size(); i++) {
            String columnName = columnNameList.get(i);
            Object columnNameValue = value.remove(columnName);
            Object columnNameNewVal =
                    DataTypeConverter.convertToRowData(columnNameValue, columnTypeList.get(i), getSinkTimeZone());
            value.put(columnName, columnNameNewVal);
        }
        value.put("__op", op);
        value.put("is_deleted", isDeleted);
        value.put("db", schemaName);
        value.put("table", tableName);
        value.put("ts_ms", tsMs);
    }
}
