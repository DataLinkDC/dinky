package com.dlink.cdc.kafka;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.AbstractSinkBuilder;
import com.dlink.cdc.CDCBuilder;
import com.dlink.cdc.SinkBuilder;
import com.dlink.executor.CustomTableEnvironment;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import com.dlink.utils.ObjectConvertUtil;

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

/**
 * @className: com.dlink.cdc.kafka.KafkaSinkSimpleBuilder
 */
public class KafkaSinkJsonBuilder extends AbstractSinkBuilder implements Serializable {

    private static final String KEY_WORD = "datastream-kafka-json";
    private transient ObjectMapper objectMapper;

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
            if (Asserts.isNotNullCollection(schemaList)) {
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
                        String topic = getSinkTableName(table);
                        if (Asserts.isNotNullString(config.getSink().get("topic"))) {
                            topic = config.getSink().get("topic");
                        }
                        List<String> columnNameList = new LinkedList<>();
                        List<LogicalType> columnTypeList = new LinkedList<>();
                        buildColumn(columnNameList, columnTypeList, table.getColumns());
                        SingleOutputStreamOperator<String> stringOperator = filterOperator.process(new ProcessFunction<Map, String>() {
                            @Override
                            public void processElement(Map value, Context context, Collector<String> collector) throws Exception {
                                Map after = null;
                                Map before = null;
                                String tsMs = value.get("ts_ms").toString();
                                try {
                                    switch (value.get("op").toString()) {
                                        case "r":
                                        case "c":
                                            after = (Map) value.get("after");
                                            convertAttr(columnNameList, columnTypeList, after,value.get("op").toString(), 0, schemaName, tableName, tsMs);
                                            break;
                                        case "u":
                                            before = (Map) value.get("before");
                                            convertAttr(columnNameList, columnTypeList, before,value.get("op").toString(), 1, schemaName, tableName, tsMs);

                                            after = (Map) value.get("after");
                                            convertAttr(columnNameList, columnTypeList, after,value.get("op").toString(), 0, schemaName, tableName, tsMs);
                                            break;
                                        case "d":
                                            before = (Map) value.get("before");
                                            convertAttr(columnNameList, columnTypeList, before,value.get("op").toString(), 1,schemaName, tableName, tsMs);
                                            break;
                                        default:
                                    }
                                } catch (Exception e) {
                                    logger.error("SchameTable: {} - Exception:", e);
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
                        });
                        stringOperator.addSink(new FlinkKafkaProducer<String>(config.getSink().get("brokers"),
                                topic,
                                new SimpleStringSchema()));
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("kafka sink error:",ex);
        }
        return dataStreamSource;
    }

    private void initializeObjectMapper() {
        this.objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // Hack time module to allow 'Z' at the end of string (i.e. javascript json's)
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
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
    protected Object convertValue(Object value, LogicalType logicalType) {
        return ObjectConvertUtil.convertValue(value,logicalType);
    }

    private void convertAttr(List<String> columnNameList, List<LogicalType> columnTypeList, Map value, String op, int isDeleted,
                             String schemaName, String tableName, String tsMs) {
        for (int i = 0; i < columnNameList.size(); i++) {
            String columnName = columnNameList.get(i);
            Object columnNameValue = value.remove(columnName);
            Object columnNameNewVal = convertValue(columnNameValue, columnTypeList.get(i));
            value.put(columnName, columnNameNewVal);
        }
        value.put("__op", op);
        value.put("is_deleted", Integer.valueOf(isDeleted));
        value.put("db", schemaName);
        value.put("table", tableName);
        value.put("ts_ms", tsMs);
    }
}
