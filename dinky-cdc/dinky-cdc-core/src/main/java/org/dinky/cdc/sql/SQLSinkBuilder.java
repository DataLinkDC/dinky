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
import org.dinky.cdc.SinkBuilder;
import org.dinky.cdc.utils.FlinkStatementUtil;
import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;
import org.dinky.executor.CustomTableEnvironment;
import org.dinky.utils.LogUtil;
import org.dinky.utils.SplitUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.types.logical.BigIntType;
import org.apache.flink.table.types.logical.DateType;
import org.apache.flink.table.types.logical.DecimalType;
import org.apache.flink.table.types.logical.FloatType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.TimestampType;
import org.apache.flink.table.types.logical.VarBinaryType;
import org.apache.flink.table.types.utils.TypeConversions;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

public class SQLSinkBuilder extends AbstractSqlSinkBuilder implements Serializable {

    public static final String KEY_WORD = "sql";
    private static final long serialVersionUID = -3699685106324048226L;
    private ZoneId sinkTimeZone = ZoneId.of("UTC");

    public SQLSinkBuilder() {}

    private SQLSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    private String addSourceTableView(
            CustomTableEnvironment customTableEnvironment,
            DataStream<Row> rowDataDataStream,
            Table table,
            List<String> columnNameList) {
        String viewName = "VIEW_" + table.getSchemaTableNameWithUnderline(); // 上游表
        customTableEnvironment.createTemporaryView(viewName, rowDataDataStream, columnNameList);
        logger.info("Create {} temporaryView successful...", viewName);
        return viewName;
    }

    private void addTableSinks(
            CustomTableEnvironment customTableEnvironment, Table table, String viewName) {

        String sinkSchemaName = getSinkSchemaName(table); // 下游库名称
        String sinkTableName = getSinkTableName(table); // 下游表名称

        // 这个地方要根据下游表的数量进行生成
        if (config.getSinks() != null && config.getSinks().size() > 0) {
            boolean onlySingleSink = config.getSinks().size() == 1;
            int index = 0;
            for (Map<String, String> sink : config.getSinks()) {
                String tableName = sinkTableName;
                if (!onlySingleSink) {
                    tableName = tableName + "_" + index++;
                }
                config.setSink(sink);
                addSinkInsert(
                        customTableEnvironment,
                        table,
                        viewName,
                        tableName,
                        sinkSchemaName,
                        sinkTableName);
            }
        } else {
            addSinkInsert(
                    customTableEnvironment,
                    table,
                    viewName,
                    sinkTableName,
                    sinkSchemaName,
                    sinkTableName);
        }
    }

    private List<Operation> addSinkInsert(
            CustomTableEnvironment customTableEnvironment,
            Table table,
            String viewName,
            String tableName,
            String sinkSchemaName,
            String sinkTableName) {
        String pkList = StringUtils.join(getPKList(table), ".");

        String flinkDDL =
                FlinkStatementUtil.getFlinkDDL(
                        table, tableName, config, sinkSchemaName, sinkTableName, pkList);
        logger.info(flinkDDL);
        customTableEnvironment.executeSql(flinkDDL);
        logger.info("Create " + tableName + " FlinkSQL DDL successful...");

        String cdcSqlInsert = FlinkStatementUtil.getCDCInsertSql(table, tableName, viewName);
        logger.info(cdcSqlInsert);

        List<Operation> operations = customTableEnvironment.getParser().parse(cdcSqlInsert);
        logger.info("Create " + tableName + " FlinkSQL insert into successful...");
        try {
            if (!operations.isEmpty()) {
                Operation operation = operations.get(0);
                if (operation instanceof ModifyOperation) {
                    modifyOperations.add((ModifyOperation) operation);
                }
            }
        } catch (Exception e) {
            logger.error("Translate to plan occur exception: {}", e.toString());
            throw e;
        }
        return operations;
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public SinkBuilder create(FlinkCDCConfig config) {
        return new SQLSinkBuilder(config);
    }

    @Override
    public DataStreamSource build(
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
        if (Asserts.isNotNullCollection(schemaList)) {

            logger.info("Build deserialize successful...");
            Map<Table, OutputTag<Map>> tagMap = new HashMap<>();
            Map<String, Table> tableMap = new HashMap<>();
            Map<String, String> splitConfMap = config.getSplit();

            for (Schema schema : schemaList) {
                for (Table table : schema.getTables()) {
                    String sinkTableName = getSinkTableName(table);
                    OutputTag<Map> outputTag = new OutputTag<Map>(sinkTableName) {};
                    tagMap.put(table, outputTag);
                    tableMap.put(table.getSchemaTableName(), table);
                }
            }
            final String schemaFieldName = config.getSchemaFieldName();
            ObjectMapper objectMapper = new ObjectMapper();
            SingleOutputStreamOperator<Map> mapOperator =
                    dataStreamSource
                            .map(x -> objectMapper.readValue(x, Map.class))
                            .returns(Map.class);

            SingleOutputStreamOperator<Map> processOperator =
                    mapOperator.process(
                            new ProcessFunction<Map, Map>() {

                                @Override
                                public void processElement(
                                        Map map,
                                        ProcessFunction<Map, Map>.Context ctx,
                                        Collector<Map> out)
                                        throws Exception {
                                    LinkedHashMap source = (LinkedHashMap) map.get("source");
                                    try {
                                        String tableName =
                                                SplitUtil.getReValue(
                                                                source.get(schemaFieldName)
                                                                        .toString(),
                                                                splitConfMap)
                                                        + "."
                                                        + SplitUtil.getReValue(
                                                                source.get("table").toString(),
                                                                splitConfMap);
                                        Table table = tableMap.get(tableName);
                                        OutputTag<Map> outputTag = tagMap.get(table);
                                        Optional.ofNullable(outputTag)
                                                .orElseThrow(
                                                        () ->
                                                                new RuntimeException(
                                                                        "data outPutTag is not exists!table name is  "
                                                                                + tableName));
                                        ctx.output(outputTag, map);
                                    } catch (Exception e) {
                                        logger.error(e.getMessage(), e);
                                        out.collect(map);
                                    }
                                }
                            });
            tagMap.forEach(
                    (table, tag) -> {
                        final String schemaTableName = table.getSchemaTableName();
                        try {
                            DataStream<Map> filterOperator = shunt(processOperator, table, tag);
                            logger.info("Build {} shunt successful...", schemaTableName);
                            List<String> columnNameList = new ArrayList<>();
                            List<LogicalType> columnTypeList = new ArrayList<>();
                            buildColumn(columnNameList, columnTypeList, table.getColumns());
                            DataStream<Row> rowDataDataStream =
                                    buildRow(
                                                    filterOperator,
                                                    columnNameList,
                                                    columnTypeList,
                                                    schemaTableName)
                                            .rebalance();
                            logger.info("Build " + schemaTableName + " flatMap successful...");
                            logger.info("Start build " + schemaTableName + " sink...");
                            String viewName =
                                    addSourceTableView(
                                            customTableEnvironment,
                                            rowDataDataStream,
                                            table,
                                            columnNameList);
                            addTableSinks(customTableEnvironment, table, viewName);
                        } catch (Exception e) {
                            logger.error("Build " + schemaTableName + " cdc sync failed...");
                            logger.error(LogUtil.getError(e));
                        }
                    });

            List<Transformation<?>> trans =
                    customTableEnvironment.getPlanner().translate(modifyOperations);
            for (Transformation<?> item : trans) {
                env.addOperator(item);
            }
            logger.info("A total of " + trans.size() + " table cdc sync were build successfull...");
        }
        return dataStreamSource;
    }

    protected Object convertValue(Object value, LogicalType logicalType) {
        if (value == null) {
            return null;
        }

        if (logicalType instanceof DateType) {
            if (value instanceof Integer) {
                return LocalDate.ofEpochDay((Integer) value);
            } else if (value instanceof Long) {
                return Instant.ofEpochMilli((long) value).atZone(sinkTimeZone).toLocalDate();
            } else {
                return Instant.parse(value.toString()).atZone(sinkTimeZone).toLocalDate();
            }
        }

        if (logicalType instanceof TimestampType) {
            if (value instanceof Integer) {
                return Instant.ofEpochMilli(((Integer) value).longValue())
                        .atZone(sinkTimeZone)
                        .toLocalDateTime();
            }

            if (value instanceof String) {
                return Instant.parse((String) value).atZone(sinkTimeZone).toLocalDateTime();
            }

            TimestampType logicalType1 = (TimestampType) logicalType;
            // 转换为毫秒
            if (logicalType1.getPrecision() == 3) {
                return Instant.ofEpochMilli((long) value)
                        .atZone(sinkTimeZone)
                        .toLocalDateTime();
            }

            if (logicalType1.getPrecision() > 3) {
                return Instant.ofEpochMilli(
                                ((long) value)
                                        / (long) Math.pow(10, logicalType1.getPrecision() - 3))
                        .atZone(sinkTimeZone)
                        .toLocalDateTime();
            }
            return Instant.ofEpochSecond(((long) value)).atZone(sinkTimeZone).toLocalDateTime();
        }

        if (logicalType instanceof DecimalType) {
            return new BigDecimal(String.valueOf(value));
        }

        if (logicalType instanceof FloatType) {
            if (value instanceof Float) {
                return value;
            }

            if (value instanceof Double) {
                return ((Double) value).floatValue();
            }
            return Float.parseFloat(value.toString());
        }

        if (logicalType instanceof BigIntType) {
            if (value instanceof Integer) {
                return ((Integer) value).longValue();
            } else {
                return value;
            }
        }

        if (logicalType instanceof VarBinaryType) {
            // VARBINARY AND BINARY is converted to String with encoding base64 in FlinkCDC.
            if (value instanceof String) {
                return DatatypeConverter.parseBase64Binary((String) value);
            }

            return value;
        }

        return value;
    }
}
