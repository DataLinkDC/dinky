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
import com.dlink.model.FlinkCDCConfig;
import com.dlink.model.Schema;
import com.dlink.model.Table;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * MysqlCDCBuilder
 *
 * @author wenmo
 * @since 2022/4/12 21:29
 **/
public class KafkaSinkBuilder extends AbstractSinkBuilder implements Serializable {

    public static final String KEY_WORD = "datastream-kafka";

    public KafkaSinkBuilder() {
    }

    public KafkaSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public void addSink(
            StreamExecutionEnvironment env,
            DataStream<RowData> rowDataDataStream,
            Table table,
            List<String> columnNameList,
            List<LogicalType> columnTypeList) {

    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public SinkBuilder create(FlinkCDCConfig config) {
        return new KafkaSinkBuilder(config);
    }

    @Override
    public DataStreamSource build(
            CDCBuilder cdcBuilder,
            StreamExecutionEnvironment env,
            CustomTableEnvironment customTableEnvironment,
            DataStreamSource<String> dataStreamSource) {
        // 解决kafka的 properties 配置未加载问题
        Properties kafkaProducerConfig = getProperties();
        if (Asserts.isNotNullString(config.getSink().get("topic"))) {
            org.apache.flink.connector.kafka.sink.KafkaSinkBuilder<String> kafkaSinkBuilder = KafkaSink
                    .<String>builder()
                    .setBootstrapServers(config.getSink().get("brokers"))
                    .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                            .setTopic(config.getSink().get("topic"))
                            .setValueSerializationSchema(new SimpleStringSchema())
                            .build())
                    .setDeliverGuarantee(DeliveryGuarantee.valueOf(env.getCheckpointingMode().name()));
            if (!kafkaProducerConfig.isEmpty()) {
                kafkaSinkBuilder.setKafkaProducerConfig(kafkaProducerConfig);
            }
            if (!kafkaProducerConfig.isEmpty() && kafkaProducerConfig.containsKey("transactional.id")
                    && Asserts.isNotNullString(kafkaProducerConfig.getProperty("transactional.id"))) {
                kafkaSinkBuilder.setTransactionalIdPrefix(kafkaProducerConfig.getProperty("transactional.id"));
            }
            KafkaSink<String> kafkaSink = kafkaSinkBuilder.build();
            dataStreamSource.sinkTo(kafkaSink);
        } else {
            Map<Table, OutputTag<String>> tagMap = new HashMap<>();
            Map<String, Table> tableMap = new HashMap<>();
            ObjectMapper objectMapper = new ObjectMapper();
            SingleOutputStreamOperator<Map> mapOperator = dataStreamSource
                    .map(x -> objectMapper.readValue(x, Map.class)).returns(Map.class);
            final List<Schema> schemaList = config.getSchemaList();
            final String schemaFieldName = config.getSchemaFieldName();
            if (Asserts.isNotNullCollection(schemaList)) {
                for (Schema schema : schemaList) {
                    for (Table table : schema.getTables()) {
                        String sinkTableName = getSinkTableName(table);
                        OutputTag<String> outputTag = new OutputTag<String>(sinkTableName) {
                        };
                        tagMap.put(table, outputTag);
                        tableMap.put(table.getSchemaTableName(), table);
                    }
                }
                SingleOutputStreamOperator<String> process = mapOperator.process(new ProcessFunction<Map, String>() {

                    @Override
                    public void processElement(Map map, ProcessFunction<Map, String>.Context ctx, Collector<String> out)
                            throws Exception {
                        LinkedHashMap source = (LinkedHashMap) map.get("source");
                        try {
                            String result = objectMapper.writeValueAsString(map);
                            Table table = tableMap
                                    .get(source.get(schemaFieldName).toString() + "." + source.get("table").toString());
                            OutputTag<String> outputTag = tagMap.get(table);
                            ctx.output(outputTag, result);
                        } catch (Exception e) {
                            out.collect(objectMapper.writeValueAsString(map));
                        }
                    }
                });
                tagMap.forEach((k, v) -> {
                    String topic = getSinkTableName(k);
                    org.apache.flink.connector.kafka.sink.KafkaSinkBuilder<String> kafkaSinkBuilder = KafkaSink
                            .<String>builder()
                            .setBootstrapServers(config.getSink().get("brokers"))
                            .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                                    .setTopic(topic)
                                    .setValueSerializationSchema(new SimpleStringSchema())
                                    .build())
                            .setDeliverGuarantee(DeliveryGuarantee.valueOf(env.getCheckpointingMode().name()));
                    if (!kafkaProducerConfig.isEmpty()) {
                        kafkaSinkBuilder.setKafkaProducerConfig(kafkaProducerConfig);
                    }
                    if (!kafkaProducerConfig.isEmpty() && kafkaProducerConfig.containsKey("transactional.id")
                            && Asserts.isNotNullString(kafkaProducerConfig.getProperty("transactional.id"))) {
                        kafkaSinkBuilder.setTransactionalIdPrefix(
                                kafkaProducerConfig.getProperty("transactional.id") + "-" + topic);
                    }
                    KafkaSink<String> kafkaSink = kafkaSinkBuilder.build();
                    process.getSideOutput(v).rebalance().sinkTo(kafkaSink).name(topic);
                });
            }
        }
        return dataStreamSource;
    }
}
