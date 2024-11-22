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
import org.dinky.cdc.convert.DataTypeConverter;
import org.dinky.cdc.utils.FlinkStatementUtil;
import org.dinky.data.model.Column;
import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;
import org.dinky.utils.JsonUtils;
import org.dinky.utils.LogUtil;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.utils.TypeConversions;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractSqlSinkBuilder extends AbstractSinkBuilder implements Serializable {

    protected AbstractSqlSinkBuilder() {}

    protected AbstractSqlSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @SuppressWarnings("rawtypes")
    private FlatMapFunction<Map, Row> sinkRowFunction(
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
        if (Asserts.isNull(value)) {
            return;
        }
        Row row = Row.withPositions(rowKind, columnNameList.size());
        for (int i = 0; i < columnNameList.size(); i++) {
            row.setField(
                    i,
                    DataTypeConverter.convertToRow(
                            value.get(columnNameList.get(i)), columnTypeList.get(i), sinkTimeZone));
        }
        out.collect(row);
    }

    @SuppressWarnings("rawtypes")
    private DataStream<Row> buildRow(
            DataStream<Map> filterOperator,
            List<String> columnNameList,
            List<LogicalType> columnTypeList,
            String schemaTableName) {
        TypeInformation<?>[] typeInformation = TypeConversions.fromDataTypeToLegacyInfo(
                TypeConversions.fromLogicalToDataType(columnTypeList.toArray(new LogicalType[0])));

        return filterOperator
                .flatMap(
                        sinkRowFunction(columnNameList, columnTypeList, schemaTableName),
                        new RowTypeInfo(typeInformation, columnNameList.toArray(new String[0])))
                .name("FlatMapRow");
    }

    @SuppressWarnings("rawtypes")
    private void addTableSinkForTags(
            Map<Table, OutputTag<Map>> tagMap, SingleOutputStreamOperator<Map> processOperator) {
        tagMap.forEach((table, tag) -> {
            final String schemaTableName = table.getSchemaTableName();
            try {
                processOperator.forward();
                DataStream<Map> filterOperator =
                        processOperator.getSideOutput(tag).forward();
                final List<String> columnNameList = new ArrayList<>();
                final List<LogicalType> columnTypeList = new ArrayList<>();
                buildColumn(columnNameList, columnTypeList, table.getColumns());
                DataStream<Row> rowDataDataStream = buildRow(
                                filterOperator, columnNameList, columnTypeList, schemaTableName)
                        .forward();
                logger.info("Build {} flatMap successful...", schemaTableName);
                logger.info("Start build {} sink...", schemaTableName);

                addTableSink(rowDataDataStream, table);
            } catch (Exception e) {
                logger.error("Build {} cdc sync failed...", schemaTableName);
                logger.error(LogUtil.getError(e));
            }
        });
    }

    @SuppressWarnings("rawtypes")
    private SingleOutputStreamOperator<Map> shunt(
            SingleOutputStreamOperator<Map> mapOperator,
            Map<Table, OutputTag<Map>> tagMap,
            Map<String, Table> tableMap) {
        final String schemaFieldName = config.getSchemaFieldName();
        final Map<String, String> configSplit = config.getSplit();
        return mapOperator
                .process(new ProcessFunction<Map, Map>() {
                    @Override
                    public void processElement(Map map, ProcessFunction<Map, Map>.Context ctx, Collector<Map> out) {
                        LinkedHashMap source = (LinkedHashMap) map.get("source");
                        try {
                            String tableName = getMergedTableName(source, schemaFieldName, configSplit);
                            OutputTag<Map> outputTag = tagMap.get(tableMap.get(tableName));
                            ctx.output(outputTag, map);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                            out.collect(map);
                        }
                    }
                })
                .name("Shunt");
    }

    protected abstract void addTableSink(DataStream<Row> rowDataDataStream, Table table);

    @SuppressWarnings("rawtypes")
    protected void buildPipeline(DataStreamSource<String> dataStreamSource, List<Schema> schemaList) {
        executeCatalogStatement();

        SingleOutputStreamOperator<Map> mapOperator = deserialize(dataStreamSource);
        logger.info("Build deserialize successful...");

        final Map<Table, OutputTag<Map>> tagMap = new LinkedHashMap<>();
        final Map<String, Table> tableMap = new LinkedHashMap<>();
        for (Schema schema : schemaList) {
            for (Table table : schema.getTables()) {
                OutputTag<Map> outputTag = new OutputTag<Map>(getSinkTableName(table)) {};
                tagMap.put(table, outputTag);
                tableMap.put(table.getSchemaTableName(), table);
            }
        }

        partitionByTableAndPrimarykey(mapOperator, tableMap);
        logger.info("Build partitionBy successful...");
        SingleOutputStreamOperator<Map> singleOutputStreamOperator = shunt(mapOperator, tagMap, tableMap);
        logger.info("Build shunt successful...");
        addTableSinkForTags(tagMap, singleOutputStreamOperator);
        logger.info("Build sink successful...");
        List<Transformation<?>> trans = customTableEnvironment.getPlanner().translate(modifyOperations);
        for (Transformation<?> item : trans) {
            env.addOperator(item);
        }
        logger.info("A total of {} table cdc sync were build successful...", trans.size());
    }

    protected void executeCatalogStatement() {}

    /**
     * replace view name middle to under line for flink use view name
     * @param viewName view name
     * @return view name
     */
    public static String replaceViewNameMiddleLineToUnderLine(String viewName) {
        if (!viewName.isEmpty() && viewName.contains("-")) {
            logger.warn("the view name [{}] contains '-', replace '-' to '_' for flink use view name", viewName);
            return viewName.replaceAll("-", "_");
        }
        return viewName;
    }

    protected List<Operation> createInsertOperations(Table table, String viewName, String tableName) {
        String cdcSqlInsert = FlinkStatementUtil.getCDCInsertSql(table, tableName, viewName, config);
        logger.info(cdcSqlInsert);

        List<Operation> operations = customTableEnvironment.getParser().parse(cdcSqlInsert);
        logger.info("Create {} FlinkSQL insert into successful...", tableName);
        if (operations.isEmpty()) {
            return operations;
        }

        Operation operation = operations.get(0);
        if (operation instanceof ModifyOperation) {
            modifyOperations.add((ModifyOperation) operation);
        }
        return operations;
    }

    protected List<String> getPKList(Table table) {
        if (Asserts.isNullCollection(table.getColumns())) {
            return new ArrayList<>();
        }

        return table.getColumns().stream()
                .filter(Column::isKeyFlag)
                .map(Column::getName)
                .collect(Collectors.toList());
    }
}
