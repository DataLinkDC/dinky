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

package org.dinky.cdc.sql;

import org.dinky.assertion.Asserts;
import org.dinky.cdc.AbstractSinkBuilder;
import org.dinky.cdc.CDCBuilder;
import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;
import org.dinky.executor.CustomTableEnvironment;
import org.dinky.utils.JsonUtils;
import org.dinky.utils.LogUtil;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.Partitioner;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.table.types.logical.DecimalType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.utils.TypeConversions;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractSqlSinkBuilder extends AbstractSinkBuilder implements Serializable {
    protected ZoneId sinkTimeZone = ZoneId.of("UTC");

    protected AbstractSqlSinkBuilder() {}

    protected AbstractSqlSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @SuppressWarnings("rawtypes")
    protected FlatMapFunction<Map, Row> sqlSinkRowFunction(
            List<String> columnNameList, List<LogicalType> columnTypeList, String schemaTableName) {
        return (value, out) -> {
            try {
                switch (value.get("op").toString()) {
                    case "r":
                    case "c":
                        rowCollect(columnNameList, columnTypeList, out, RowKind.INSERT, (Map) value.get("after"));
                        break;
                    case "d":
                        rowCollect(columnNameList, columnTypeList, out, RowKind.DELETE, (Map) value.get("before"));
                        break;
                    case "u":
                        rowCollect(
                                columnNameList, columnTypeList, out, RowKind.UPDATE_BEFORE, (Map) value.get("before"));
                        rowCollect(columnNameList, columnTypeList, out, RowKind.UPDATE_AFTER, (Map) value.get("after"));
                        break;
                    default:
                }
            } catch (Exception e) {
                logger.error(
                        "SchemaTable: {} - Row: {} - Exception {}",
                        schemaTableName,
                        JsonUtils.toJsonString(value),
                        e.toString());
                throw e;
            }
        };
    }

    @SuppressWarnings("rawtypes")
    private void rowCollect(
            List<String> columnNameList,
            List<LogicalType> columnTypeList,
            Collector<Row> out,
            RowKind rowKind,
            Map value) {
        Row row = Row.withPositions(rowKind, columnNameList.size());
        for (int i = 0; i < columnNameList.size(); i++) {
            row.setField(i, convertValue(value.get(columnNameList.get(i)), columnTypeList.get(i)));
        }
        out.collect(row);
    }

    @SuppressWarnings("rawtypes")
    protected DataStream<Row> buildRow(
            DataStream<Map> filterOperator,
            List<String> columnNameList,
            List<LogicalType> columnTypeList,
            String schemaTableName) {
        TypeInformation<?>[] typeInformation = TypeConversions.fromDataTypeToLegacyInfo(
                TypeConversions.fromLogicalToDataType(columnTypeList.toArray(new LogicalType[0])));

        return filterOperator.flatMap(
                sqlSinkRowFunction(columnNameList, columnTypeList, schemaTableName),
                new RowTypeInfo(typeInformation, columnNameList.toArray(new String[0])));
    }

    @Override
    protected Optional<Object> convertDecimalType(Object value, LogicalType logicalType) {
        if (logicalType instanceof DecimalType) {
            return Optional.of(new BigDecimal(String.valueOf(value)));
        }
        return Optional.empty();
    }

    @SuppressWarnings("rawtypes")
    protected void addTableSinkForTags(
            CustomTableEnvironment customTableEnvironment,
            Map<Table, OutputTag<Map>> tagMap,
            SingleOutputStreamOperator<Map> processOperator) {
        tagMap.forEach((table, tag) -> {
            final String schemaTableName = table.getSchemaTableName();
            try {
                DataStream<Map> filterOperator = shunt(processOperator, table, tag);
                logger.info("Build {} shunt successful...", schemaTableName);
                List<String> columnNameList = new ArrayList<>();
                List<LogicalType> columnTypeList = new ArrayList<>();
                buildColumn(columnNameList, columnTypeList, table.getColumns());
                DataStream<Row> rowDataDataStream = buildRow(
                                filterOperator, columnNameList, columnTypeList, schemaTableName)
                        .forward();
                logger.info("Build {} flatMap successful...", schemaTableName);
                logger.info("Start build {} sink...", schemaTableName);

                addTableSink(customTableEnvironment, rowDataDataStream, table);
            } catch (Exception e) {
                logger.error("Build {} cdc sync failed...", schemaTableName);
                logger.error(LogUtil.getError(e));
            }
        });
    }

    @SuppressWarnings("rawtypes")
    protected SingleOutputStreamOperator<Map> createMapSingleOutputStreamOperator(
            DataStreamSource<String> dataStreamSource, Map<Table, OutputTag<Map>> tagMap, Map<String, Table> tableMap) {
        final String schemaFieldName = config.getSchemaFieldName();
        SingleOutputStreamOperator<Map> mapOperator =
                dataStreamSource.map(x -> objectMapper.readValue(x, Map.class)).returns(Map.class);
        Map<String, String> split = config.getSplit();
        partitionByTableAndPrimarykey(mapOperator, tableMap);
        return mapOperator.process(new ProcessFunction<Map, Map>() {
            @Override
            public void processElement(Map map, ProcessFunction<Map, Map>.Context ctx, Collector<Map> out) {
                LinkedHashMap source = (LinkedHashMap) map.get("source");
                try {
                    String tableName = createTableName(source, schemaFieldName, split);
                    OutputTag<Map> outputTag = tagMap.get(tableMap.get(tableName));
                    ctx.output(outputTag, map);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    out.collect(map);
                }
            }
        });
    }

    protected abstract void addTableSink(
            CustomTableEnvironment customTableEnvironment, DataStream<Row> rowDataDataStream, Table table);

    /**
     * @param source
     * @param schemaFieldName
     * @param split must keep for flink use.
     * @return
     */
    protected abstract String createTableName(LinkedHashMap source, String schemaFieldName, Map<String, String> split);

    @SuppressWarnings("rawtypes")
    @Override
    public DataStreamSource<String> build(
            CDCBuilder cdcBuilder,
            StreamExecutionEnvironment env,
            CustomTableEnvironment customTableEnvironment,
            DataStreamSource<String> dataStreamSource) {
        final String timeZone = config.getSink().get("timezone");
        config.getSink().remove("timezone");
        if (Asserts.isNotNullString(timeZone)) {
            sinkTimeZone = ZoneId.of(timeZone);
        }

        final List<Schema> schemaList = config.getSchemaList();
        if (Asserts.isNullCollection(schemaList)) {
            return dataStreamSource;
        }

        executeCatalogStatement(customTableEnvironment);

        logger.info("Build deserialize successful...");

        Map<Table, OutputTag<Map>> tagMap = new HashMap<>();
        Map<String, Table> tableMap = new HashMap<>();
        for (Schema schema : schemaList) {
            for (Table table : schema.getTables()) {
                String sinkTableName = getSinkTableName(table);
                OutputTag<Map> outputTag = new OutputTag<Map>(sinkTableName) {};
                tagMap.put(table, outputTag);
                tableMap.put(table.getSchemaTableName(), table);
            }
        }

        SingleOutputStreamOperator<Map> processOperator =
                createMapSingleOutputStreamOperator(dataStreamSource, tagMap, tableMap);
        addTableSinkForTags(customTableEnvironment, tagMap, processOperator);

        List<Transformation<?>> trans = customTableEnvironment.getPlanner().translate(modifyOperations);
        for (Transformation<?> item : trans) {
            env.addOperator(item);
        }
        logger.info("A total of {} table cdc sync were build successful...", trans.size());
        return dataStreamSource;
    }

    protected void partitionByTableAndPrimarykey(
            SingleOutputStreamOperator<Map> mapOperator, Map<String, Table> tableMap) {
        mapOperator.partitionCustom(
                new Partitioner<String>() {
                    @Override
                    public int partition(String key, int numPartitions) {
                        return Math.abs(key.hashCode()) % numPartitions;
                    }
                },
                map -> {
                    LinkedHashMap source = (LinkedHashMap) map.get("source");
                    String tableName = createTableName(source, config.getSchemaFieldName(), config.getSplit());
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
        mapOperator.name("PartitionByPrimarykey");
    }

    protected void executeCatalogStatement(CustomTableEnvironment customTableEnvironment) {}
    ;
}
