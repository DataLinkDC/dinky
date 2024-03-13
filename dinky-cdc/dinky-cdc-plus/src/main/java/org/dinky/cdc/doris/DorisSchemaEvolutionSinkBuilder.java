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

package org.dinky.cdc.doris;

import org.dinky.assertion.Asserts;
import org.dinky.cdc.AbstractSinkBuilder;
import org.dinky.cdc.CDCBuilder;
import org.dinky.cdc.SinkBuilder;
import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;
import org.dinky.executor.CustomTableEnvironment;

import org.apache.doris.flink.cfg.DorisExecutionOptions;
import org.apache.doris.flink.cfg.DorisOptions;
import org.apache.doris.flink.cfg.DorisReadOptions;
import org.apache.doris.flink.sink.DorisSink;
import org.apache.doris.flink.sink.writer.serializer.JsonDebeziumSchemaSerializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class DorisSchemaEvolutionSinkBuilder extends AbstractSinkBuilder implements Serializable {

    public static final String KEY_WORD = "datastream-doris-schema-evolution";

    public DorisSchemaEvolutionSinkBuilder() {}

    public DorisSchemaEvolutionSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public SinkBuilder create(FlinkCDCConfig config) {
        return new DorisSchemaEvolutionSinkBuilder(config);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DataStreamSource<String> build(
            CDCBuilder cdcBuilder,
            StreamExecutionEnvironment env,
            CustomTableEnvironment customTableEnvironment,
            DataStreamSource<String> dataStreamSource) {

        Map<String, String> sink = config.getSink();

        Properties properties = getProperties();
        // schema evolution need json format
        properties.setProperty("format", "json");
        properties.setProperty("read_json_by_line", "true");

        final List<Schema> schemaList = config.getSchemaList();
        if (!Asserts.isNotNullCollection(schemaList)) {
            return dataStreamSource;
        }

        SingleOutputStreamOperator<Map> mapOperator =
                dataStreamSource.map(x -> objectMapper.readValue(x, Map.class)).returns(Map.class);
        final String schemaFieldName = config.getSchemaFieldName();

        Map<Table, OutputTag<String>> tagMap = new HashMap<>();
        Map<String, Table> tableMap = new HashMap<>();
        for (Schema schema : schemaList) {
            for (Table table : schema.getTables()) {
                OutputTag<String> outputTag = new OutputTag<String>(getSinkTableName(table)) {};
                tagMap.put(table, outputTag);
                tableMap.put(table.getSchemaTableName(), table);
            }
        }

        SingleOutputStreamOperator<String> process = mapOperator.process(new ProcessFunction<Map, String>() {

            @Override
            public void processElement(Map map, Context ctx, Collector<String> out) throws Exception {
                LinkedHashMap source = (LinkedHashMap) map.get("source");
                String result = objectMapper.writeValueAsString(map);
                try {
                    Table table = tableMap.get(source.get(schemaFieldName).toString()
                            + "."
                            + source.get("table").toString());
                    ctx.output(tagMap.get(table), result);
                } catch (Exception e) {
                    out.collect(result);
                }
            }
        });

        tagMap.forEach((table, v) -> {
            DorisOptions dorisOptions = DorisOptions.builder()
                    .setFenodes(config.getSink().get(DorisSinkOptions.FENODES.key()))
                    .setTableIdentifier(getSinkSchemaName(table) + "." + getSinkTableName(table))
                    .setUsername(config.getSink().get(DorisSinkOptions.USERNAME.key()))
                    .setPassword(config.getSink().get(DorisSinkOptions.PASSWORD.key()))
                    .build();

            DorisExecutionOptions.Builder executionBuilder = DorisExecutionOptions.builder();

            if (sink.containsKey(DorisSinkOptions.SINK_BUFFER_COUNT.key())) {
                executionBuilder.setBufferCount(Integer.parseInt(sink.get(DorisSinkOptions.SINK_BUFFER_COUNT.key())));
            }

            if (sink.containsKey(DorisSinkOptions.SINK_BUFFER_SIZE.key())) {
                executionBuilder.setBufferSize(Integer.parseInt(sink.get(DorisSinkOptions.SINK_BUFFER_SIZE.key())));
            }

            if (sink.containsKey(DorisSinkOptions.SINK_ENABLE_DELETE.key())) {
                executionBuilder.setDeletable(Boolean.valueOf(sink.get(DorisSinkOptions.SINK_ENABLE_DELETE.key())));
            } else {
                executionBuilder.setDeletable(true);
            }

            if (sink.containsKey(DorisSinkOptions.SINK_LABEL_PREFIX.key())) {
                executionBuilder.setLabelPrefix(String.format(
                        "%s-%s_%s",
                        sink.get(DorisSinkOptions.SINK_LABEL_PREFIX.key()),
                        getSinkSchemaName(table),
                        getSinkTableName(table)));
            } else {
                // flink-cdc-pipeline-connector-doris 3.0.0 以上版本内部已经拼接了 SchemaName + SinkTableName，并且约定 TableLabel 正则表达式如下 --> regex: ^[-_A-Za-z0-9]{1,128}$
                executionBuilder.setLabelPrefix("dinky");
            }

            if (sink.containsKey(DorisSinkOptions.SINK_MAX_RETRIES.key())) {
                executionBuilder.setMaxRetries(Integer.valueOf(sink.get(DorisSinkOptions.SINK_MAX_RETRIES.key())));
            }

            executionBuilder.setStreamLoadProp(properties).setDeletable(true);

            JsonDebeziumSchemaSerializer.Builder jsonDebeziumSchemaSerializerBuilder = JsonDebeziumSchemaSerializer.builder();

            // use new schema change
            if (sink.containsKey(DorisSinkOptions.SINK_USE_NEW_SCHEMA_CHANGE.key())) {
                jsonDebeziumSchemaSerializerBuilder.setNewSchemaChange(Boolean.valueOf(sink.get(DorisSinkOptions.SINK_USE_NEW_SCHEMA_CHANGE.key())));
            }

            DorisSink.Builder<String> builder = DorisSink.builder();
            builder.setDorisReadOptions(DorisReadOptions.builder().build())
                    .setDorisExecutionOptions(executionBuilder.build())
                    .setDorisOptions(dorisOptions)
                    .setSerializer(jsonDebeziumSchemaSerializerBuilder
                            .setDorisOptions(dorisOptions)
                            .build());

            process.getSideOutput(v)
                    .rebalance()
                    .sinkTo(builder.build())
                    .name(String.format(
                            "Doris Schema Evolution Sink(table=[%s.%s])",
                            getSinkSchemaName(table), getSinkTableName(table)));
        });
        return dataStreamSource;
    }

    @Override
    protected Properties getProperties() {
        Properties properties = new Properties();
        Map<String, String> sink = config.getSink();
        for (Map.Entry<String, String> entry : sink.entrySet()) {
            if (Asserts.isNotNullString(entry.getKey())
                    && entry.getKey().startsWith("sink.properties")
                    && Asserts.isNotNullString(entry.getValue())) {
                properties.setProperty(entry.getKey().replace("sink.properties.", ""), entry.getValue());
            }
        }
        return properties;
    }
}
