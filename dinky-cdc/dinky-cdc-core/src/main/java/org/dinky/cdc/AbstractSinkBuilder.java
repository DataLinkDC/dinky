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

package org.dinky.cdc;

import org.dinky.assertion.Asserts;
import org.dinky.cdc.convert.DataTypeConverter;
import org.dinky.data.model.Column;
import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;
import org.dinky.executor.CustomTableEnvironment;
import org.dinky.utils.JsonUtils;
import org.dinky.utils.SplitUtil;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.Partitioner;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.types.RowKind;
import org.apache.flink.util.Collector;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSinkBuilder implements SinkBuilder {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractSinkBuilder.class);

    protected ObjectMapper objectMapper = new ObjectMapper();
    protected FlinkCDCConfig config;
    protected StreamExecutionEnvironment env;
    protected CustomTableEnvironment customTableEnvironment;
    protected List<ModifyOperation> modifyOperations = new ArrayList<>();
    protected ZoneId sinkTimeZone = ZoneId.systemDefault();

    protected AbstractSinkBuilder() {}

    protected AbstractSinkBuilder(FlinkCDCConfig config) {
        this.config = config;
    }

    protected ZoneId getSinkTimeZone() {
        return this.sinkTimeZone;
    }

    protected void init(StreamExecutionEnvironment env, CustomTableEnvironment customTableEnvironment) {
        this.env = env;
        this.customTableEnvironment = customTableEnvironment;
        initSinkTimeZone();
    }

    private void initSinkTimeZone() {
        final String timeZone = config.getSink().get("timezone");
        config.getSink().remove("timezone");
        if (Asserts.isNotNullString(timeZone)) {
            sinkTimeZone = ZoneId.of(timeZone);
            logger.info("Sink timezone is {}", sinkTimeZone);
        }
    }

    protected Properties getProperties() {
        Properties properties = new Properties();
        Map<String, String> sink = config.getSink();
        for (Map.Entry<String, String> entry : sink.entrySet()) {
            if (Asserts.isNotNullString(entry.getKey())
                    && entry.getKey().startsWith("properties")
                    && Asserts.isNotNullString(entry.getValue())) {
                properties.setProperty(entry.getKey().replace("properties.", ""), entry.getValue());
            } else {
                properties.setProperty(entry.getKey(), entry.getValue());
            }
            logger.info("sink config k/v:{}", properties);
        }
        return properties;
    }

    @SuppressWarnings("rawtypes")
    protected SingleOutputStreamOperator<Map> deserialize(SingleOutputStreamOperator<String> dataStreamSource) {
        return dataStreamSource
                .map((MapFunction<String, Map>) value -> objectMapper.readValue(value, Map.class))
                .returns(Map.class)
                .name("Deserializer");
    }

    @SuppressWarnings("rawtypes")
    protected SingleOutputStreamOperator<Map> partitionByTableAndPrimarykey(
            SingleOutputStreamOperator<Map> mapOperator, Map<String, Table> tableMap) {
        final String schemaFieldName = config.getSchemaFieldName();
        final Map<String, String> configSplit = config.getSplit();
        mapOperator.partitionCustom(
                new Partitioner<String>() {
                    @Override
                    public int partition(String key, int numPartitions) {
                        return Math.abs(key.hashCode()) % numPartitions;
                    }
                },
                map -> {
                    LinkedHashMap source = (LinkedHashMap) map.get("source");
                    String tableName = getMergedTableName(source, schemaFieldName, configSplit);
                    Table table = tableMap.get(tableName);
                    List<String> primaryKeys = table.getColumns().stream()
                            .map(column -> {
                                if (column.isKeyFlag()) {
                                    return column.getName();
                                }
                                return "";
                            })
                            .collect(Collectors.toList());

                    return tableName + String.join("_", primaryKeys);
                });
        return mapOperator.name("PartitionByPrimaryKey");
    }

    protected String getMergedTableName(LinkedHashMap source, String schemaFieldName, Map<String, String> split) {
        if (Asserts.isNullMap(split)) {
            return source.get(schemaFieldName).toString() + "."
                    + source.get("table").toString();
        }
        return SplitUtil.getReValue(source.get(schemaFieldName).toString(), split)
                + "."
                + SplitUtil.getReValue(source.get("table").toString(), split);
    }

    @SuppressWarnings("rawtypes")
    protected SingleOutputStreamOperator<Map> shunt(
            SingleOutputStreamOperator<Map> mapOperator, String schemaName, String tableName) {
        final String schemaFieldName = config.getSchemaFieldName();
        return mapOperator
                .filter((FilterFunction<Map>) value -> {
                    LinkedHashMap source = (LinkedHashMap) value.get("source");
                    return tableName.equals(source.get("table").toString())
                            && schemaName.equals(source.get(schemaFieldName).toString());
                })
                .name("Shunt");
    }

    @SuppressWarnings("rawtypes")
    private DataStream<RowData> buildRowData(
            SingleOutputStreamOperator<Map> filterOperator,
            List<String> columnNameList,
            List<LogicalType> columnTypeList,
            String schemaTableName) {
        return filterOperator
                .flatMap(sinkRowDataFunction(columnNameList, columnTypeList, schemaTableName))
                .returns(RowData.class)
                .name("FlatMapRowData");
    }

    @SuppressWarnings("rawtypes")
    private FlatMapFunction<Map, RowData> sinkRowDataFunction(
            List<String> columnNameList, List<LogicalType> columnTypeList, String schemaTableName) {
        return (value, out) -> {
            try {
                switch (value.get("op").toString()) {
                    case "r":
                    case "c":
                        rowDataCollect(columnNameList, columnTypeList, out, RowKind.INSERT, value);
                        break;
                    case "d":
                        rowDataCollect(columnNameList, columnTypeList, out, RowKind.DELETE, value);
                        break;
                    case "u":
                        rowDataCollect(columnNameList, columnTypeList, out, RowKind.UPDATE_BEFORE, value);
                        rowDataCollect(columnNameList, columnTypeList, out, RowKind.UPDATE_AFTER, value);
                        break;
                    default:
                }
            } catch (Exception e) {
                logger.error(
                        "SchemaTable: {} - Row: {} - Exception: {}",
                        schemaTableName,
                        JsonUtils.toJsonString(value),
                        e.toString());
                throw e;
            }
        };
    }

    @SuppressWarnings("rawtypes")
    private void rowDataCollect(
            List<String> columnNameList,
            List<LogicalType> columnTypeList,
            Collector<RowData> out,
            RowKind rowKind,
            Map value) {
        if (Asserts.isNull(value)) {
            return;
        }
        GenericRowData genericRowData = new GenericRowData(rowKind, columnNameList.size());
        for (int i = 0; i < columnNameList.size(); i++) {
            genericRowData.setField(
                    i, buildRowDataValues(value, rowKind, columnNameList.get(i), columnTypeList.get(i)));
        }
        out.collect(genericRowData);
    }

    @SuppressWarnings("rawtypes")
    private Object buildRowDataValues(Map value, RowKind rowKind, String columnName, LogicalType columnType) {
        Map data = getOriginData(rowKind, value);
        return DataTypeConverter.convertToRowData(data.get(columnName), columnType, sinkTimeZone);
    }

    @SuppressWarnings("rawtypes")
    private Map getOriginData(RowKind rowKind, Map value) {
        if (Asserts.isNullMap(value)) {
            return Collections.emptyMap();
        }
        switch (rowKind) {
            case INSERT:
            case UPDATE_AFTER:
                return (Map) value.get("after");
            case UPDATE_BEFORE:
            case DELETE:
                return (Map) value.get("before");
            default:
                logger.warn("Unsupported row kind: {}", rowKind);
        }
        return Collections.emptyMap();
    }

    protected void addSink(
            DataStream<RowData> rowDataDataStream,
            Table table,
            List<String> columnNameList,
            List<LogicalType> columnTypeList) {}

    protected List<Schema> getSortedSchemaList() {
        final List<Schema> schemaList = config.getSchemaList();
        if (Asserts.isNullCollection(schemaList)) {
            throw new IllegalArgumentException("Schema list is empty, please check your configuration and try again.");
        }
        for (Schema schema : schemaList) {
            if (Asserts.isNullCollection(schema.getTables())) {
                // if schema tables is empty, throw exception
                throw new IllegalArgumentException(
                        "Schema tables is empty, please check your configuration or check your database permission and try again.");
            }
            // if schema tables is not empty, sort by table name to keep node sort
            schema.setTables(schema.getTables().stream()
                    .sorted(Comparator.comparing(Table::getName))
                    .collect(Collectors.toList()));
        }
        return schemaList;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void build(
            StreamExecutionEnvironment env,
            CustomTableEnvironment customTableEnvironment,
            DataStreamSource<String> dataStreamSource) {
        init(env, customTableEnvironment);
        buildPipeline(dataStreamSource, getSortedSchemaList());
    }

    protected void buildPipeline(DataStreamSource<String> dataStreamSource, List<Schema> schemaList) {
        SingleOutputStreamOperator<Map> mapOperator = deserialize(dataStreamSource);
        logger.info("Build deserialize successful...");
        final Map<String, Table> tableMap = new LinkedHashMap<>();
        for (Schema schema : schemaList) {
            for (Table table : schema.getTables()) {
                tableMap.put(table.getSchemaTableName(), table);
            }
        }
        partitionByTableAndPrimarykey(mapOperator, tableMap);
        logger.info("Build partitionBy successful...");
        for (Schema schema : schemaList) {
            for (Table table : schema.getTables()) {
                SingleOutputStreamOperator<Map> singleOutputStreamOperator =
                        shunt(mapOperator, table.getSchema(), table.getName());
                logger.info("Build shunt successful...");
                final List<String> columnNameList = new ArrayList<>();
                final List<LogicalType> columnTypeList = new ArrayList<>();
                buildColumn(columnNameList, columnTypeList, table.getColumns());

                DataStream<RowData> rowDataDataStream = buildRowData(
                        singleOutputStreamOperator, columnNameList, columnTypeList, table.getSchemaTableName());
                logger.info("Build flatRowData successful...");
                addSink(rowDataDataStream, table, columnNameList, columnTypeList);
            }
        }
    }

    protected void buildColumn(List<String> columnNameList, List<LogicalType> columnTypeList, List<Column> columns) {
        for (Column column : columns) {
            columnNameList.add(column.getName());
            columnTypeList.add(DataTypeConverter.getLogicalType(column));
        }
    }

    @Override
    public String getSinkSchemaName(Table table) {
        return config.getSink().getOrDefault("sink.db", table.getSchema());
    }

    @Override
    public String getSinkTableName(Table table) {
        String tableName = table.getName();
        Map<String, String> sink = config.getSink();

        // Add table name mapping logic
        String mappingRoute = sink.get(FlinkCDCConfig.TABLE_MAPPING_ROUTES);
        if (mappingRoute != null) {
            Map<String, String> mappingRules = parseMappingRoute(mappingRoute);
            if (mappingRules.containsKey(tableName)) {
                tableName = mappingRules.get(tableName);
            }
        }

        tableName = sink.getOrDefault(FlinkCDCConfig.TABLE_PREFIX, "")
                + tableName
                + sink.getOrDefault(FlinkCDCConfig.TABLE_SUFFIX, "");
        // table.lower and table.upper can not be true at the same time
        if (Boolean.parseBoolean(sink.get(FlinkCDCConfig.TABLE_LOWER))
                && Boolean.parseBoolean(sink.get(FlinkCDCConfig.TABLE_UPPER))) {
            throw new IllegalArgumentException("table.lower and table.upper can not be true at the same time");
        }

        if (Boolean.parseBoolean(sink.get(FlinkCDCConfig.TABLE_UPPER))) {
            tableName = tableName.toUpperCase();
        }

        if (Boolean.parseBoolean(sink.get(FlinkCDCConfig.TABLE_LOWER))) {
            tableName = tableName.toLowerCase();
        }
        // Implement regular expressions to replace table names through
        // sink.table.replace.pattern and table.replace.with
        String replacePattern = sink.get(FlinkCDCConfig.TABLE_REPLACE_PATTERN);
        String replaceWith = sink.get(FlinkCDCConfig.TABLE_REPLACE_WITH);
        if (replacePattern != null && replaceWith != null) {
            Pattern pattern = Pattern.compile(replacePattern);
            Matcher matcher = pattern.matcher(tableName);
            tableName = matcher.replaceAll(replaceWith);
        }

        // add schema
        if (Boolean.parseBoolean(sink.get("table.prefix.schema"))) {
            tableName = table.getSchema() + "_" + tableName;
        }

        return tableName;
    }

    /**
     * Mapping table name Original table name: target table name, multiple table names are implemented through mapping
     * <pre>
     *   k is original table name, v is target table name.
     *   Single table name mapping via k:v format.
     *   Multiple table names are mapped in k:v,k:v format. Note: use commas to separate them.
     * </pre>
     *
     * @param mappingRoute sink.table.mapping-route
     * @return Map<String, String> key is original table name, value is target table name
     */
    private Map<String, String> parseMappingRoute(String mappingRoute) {
        Map<String, String> mappingRules = new HashMap<>();
        String[] mappings = mappingRoute.split(",");
        for (String mapping : mappings) {
            String[] parts = mapping.split(":");
            if (parts.length == 2) {
                mappingRules.put(parts[0], parts[1]);
            }
        }
        return mappingRules;
    }

    protected Map<String, String> getTableTopicMap() {
        String topicMapStr = this.config.getSink().get("table.topic.map");
        Map<String, String> tableTopicMap = new HashMap<>();
        if (topicMapStr != null) {
            String[] topicTabArray = topicMapStr.split(";");
            for (String topicTab : topicTabArray) {
                if (topicTab != null) {
                    String[] topicTable = topicTab.split(":");
                    if (topicTable.length > 1) {
                        String[] tables = topicTable[1].split(",");
                        for (String table : tables) {
                            tableTopicMap.put(table, topicTable[0]);
                        }
                    }
                }
            }
        }

        logger.info("topic map," + tableTopicMap);
        return tableTopicMap;
    }
}
