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

package com.dlink.cdc.doris;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.AbstractSinkBuilder;
import com.dlink.cdc.CDCBuilder;
import com.dlink.cdc.SinkBuilder;
import com.dlink.executor.CustomTableEnvironment;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.model.Schema;
import com.dlink.model.Table;

import org.apache.doris.flink.cfg.DorisExecutionOptions;
import org.apache.doris.flink.cfg.DorisOptions;
import org.apache.doris.flink.cfg.DorisReadOptions;
import org.apache.doris.flink.sink.DorisSink;
import org.apache.doris.flink.sink.writer.JsonDebeziumSchemaSerializer;
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
import java.util.UUID;

/**
 * DorisSchemaEvolutionSinkBuilder
 *
 * @author wenmo
 * @since 2022/12/6
 **/
public class DorisSchemaEvolutionSinkBuilder extends AbstractSinkBuilder implements Serializable {

    private static final String KEY_WORD = "datastream-doris-schema-evolution";

    public DorisSchemaEvolutionSinkBuilder() {
    }

    public DorisSchemaEvolutionSinkBuilder(FlinkCDCConfig config) {
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
        return new DorisSchemaEvolutionSinkBuilder(config);
    }

    @Override
    public DataStreamSource build(
            CDCBuilder cdcBuilder,
            StreamExecutionEnvironment env,
            CustomTableEnvironment customTableEnvironment,
            DataStreamSource<String> dataStreamSource) {

        Map<String, String> sink = config.getSink();

        Properties properties = getProperties();
        // schema evolution need json format
        properties.setProperty("format", "json");
        properties.setProperty("read_json_by_line", "true");

        Map<Table, OutputTag<String>> tagMap = new HashMap<>();
        Map<String, Table> tableMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        SingleOutputStreamOperator<Map> mapOperator = dataStreamSource.map(x -> objectMapper.readValue(x, Map.class))
                .returns(Map.class);
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
                public void processElement(Map map, Context ctx, Collector<String> out) throws Exception {
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
            tagMap.forEach((table, v) -> {
                DorisOptions dorisOptions = DorisOptions.builder()
                        .setFenodes(config.getSink().get(DorisSinkOptions.FENODES.key()))
                        .setTableIdentifier(getSinkSchemaName(table) + "." + getSinkTableName(table))
                        .setUsername(config.getSink().get(DorisSinkOptions.USERNAME.key()))
                        .setPassword(config.getSink().get(DorisSinkOptions.PASSWORD.key())).build();

                DorisExecutionOptions.Builder executionBuilder = DorisExecutionOptions.builder();
                if (sink.containsKey(DorisSinkOptions.SINK_BUFFER_COUNT.key())) {
                    executionBuilder
                            .setBufferCount(Integer.valueOf(sink.get(DorisSinkOptions.SINK_BUFFER_COUNT.key())));
                }
                if (sink.containsKey(DorisSinkOptions.SINK_BUFFER_SIZE.key())) {
                    executionBuilder.setBufferSize(Integer.valueOf(sink.get(DorisSinkOptions.SINK_BUFFER_SIZE.key())));
                }
                if (sink.containsKey(DorisSinkOptions.SINK_ENABLE_DELETE.key())) {
                    executionBuilder.setDeletable(Boolean.valueOf(sink.get(DorisSinkOptions.SINK_ENABLE_DELETE.key())));
                } else {
                    executionBuilder.setDeletable(true);
                }
                if (sink.containsKey(DorisSinkOptions.SINK_LABEL_PREFIX.key())) {
                    executionBuilder.setLabelPrefix(sink.get(DorisSinkOptions.SINK_LABEL_PREFIX.key()) + "-"
                            + getSinkSchemaName(table) + "_" + getSinkTableName(table));
                } else {
                    executionBuilder.setLabelPrefix(
                            "dlink-" + getSinkSchemaName(table) + "_" + getSinkTableName(table) + UUID.randomUUID());
                }
                if (sink.containsKey(DorisSinkOptions.SINK_MAX_RETRIES.key())) {
                    executionBuilder.setMaxRetries(Integer.valueOf(sink.get(DorisSinkOptions.SINK_MAX_RETRIES.key())));
                }

                executionBuilder.setStreamLoadProp(properties).setDeletable(true);

                DorisSink.Builder<String> builder = DorisSink.builder();
                builder.setDorisReadOptions(DorisReadOptions.builder().build())
                        .setDorisExecutionOptions(executionBuilder.build())
                        .setDorisOptions(dorisOptions)
                        .setSerializer(JsonDebeziumSchemaSerializer.builder().setDorisOptions(dorisOptions).build());

                process.getSideOutput(v).rebalance().sinkTo(builder.build()).name("Doris Schema Evolution Sink(table=["
                        + getSinkSchemaName(table) + "." + getSinkTableName(table) + "])");
            });
        }
        return dataStreamSource;
    }

    @Override
    protected Properties getProperties() {
        Properties properties = new Properties();
        Map<String, String> sink = config.getSink();
        for (Map.Entry<String, String> entry : sink.entrySet()) {
            if (Asserts.isNotNullString(entry.getKey()) && entry.getKey().startsWith("sink.properties")
                    && Asserts.isNotNullString(entry.getValue())) {
                properties.setProperty(entry.getKey().replace("sink.properties.", ""), entry.getValue());
            }
        }
        return properties;
    }
}
