package com.dlink.cdc.kafka;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.logical.LogicalType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.AbstractSinkBuilder;
import com.dlink.cdc.CDCBuilder;
import com.dlink.cdc.SinkBuilder;
import com.dlink.executor.CustomTableEnvironment;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.model.Schema;
import com.dlink.model.Table;

/**
 * MysqlCDCBuilder
 *
 * @author wenmo
 * @since 2022/4/12 21:29
 **/
public class KafkaSinkBuilder extends AbstractSinkBuilder implements SinkBuilder {

    private final static String KEY_WORD = "kafka";

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
        if (Asserts.isNotNullString(config.getSink().get("topic"))) {
            dataStreamSource.sinkTo(KafkaSink.<String>builder()
                .setBootstrapServers(config.getSink().get("brokers"))
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                    .setTopic(config.getSink().get("topic"))
                    .setValueSerializationSchema(new SimpleStringSchema())
                    .build()
                )
                .build());
        } else {
            final List<Schema> schemaList = config.getSchemaList();
            final String schemaFieldName = config.getSchemaFieldName();
            if (Asserts.isNotNullCollection(schemaList)) {
                SingleOutputStreamOperator<Map> mapOperator = dataStreamSource.map(new MapFunction<String, Map>() {
                    @Override
                    public Map map(String value) throws Exception {
                        ObjectMapper objectMapper = new ObjectMapper();
                        return objectMapper.readValue(value, Map.class);
                    }
                });
                for (Schema schema : schemaList) {
                    for (Table table : schema.getTables()) {
                        final String tableName = table.getName();
                        final String schemaName = table.getSchema();
                        SingleOutputStreamOperator<Map> filterOperator = mapOperator.filter(new FilterFunction<Map>() {
                            @Override
                            public boolean filter(Map value) throws Exception {
                                LinkedHashMap source = (LinkedHashMap) value.get("source");
                                return tableName.equals(source.get("table").toString())
                                    && schemaName.equals(source.get(schemaFieldName).toString());
                            }
                        });
                        SingleOutputStreamOperator<String> stringOperator = filterOperator.map(new MapFunction<Map, String>() {
                            @Override
                            public String map(Map value) throws Exception {
                                ObjectMapper objectMapper = new ObjectMapper();
                                return objectMapper.writeValueAsString(value);
                            }
                        });
                        stringOperator.sinkTo(KafkaSink.<String>builder()
                            .setBootstrapServers(config.getSink().get("brokers"))
                            .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                                .setTopic(getSinkTableName(table))
                                .setValueSerializationSchema(new SimpleStringSchema())
                                .build()
                            )
                            .build());
                    }
                }
            }
        }
        return dataStreamSource;
    }
}
