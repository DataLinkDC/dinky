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

package com.dlink.cdc.sql;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.AbstractSinkBuilder;
import com.dlink.cdc.CDCBuilder;
import com.dlink.cdc.SinkBuilder;
import com.dlink.executor.CustomTableEnvironment;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import com.dlink.utils.FlinkBaseUtil;
import com.dlink.utils.JSONUtil;
import com.dlink.utils.LogUtil;
import com.dlink.utils.SplitUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.table.api.ValidationException;
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
import org.apache.flink.types.RowKind;
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
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.DatatypeConverter;

/**
 * SQLSinkBuilder
 *
 * @author wenmo
 * @since 2022/4/25 23:02
 */
public class SQLSinkBuilder extends AbstractSinkBuilder implements Serializable {

    private static final String KEY_WORD = "sql";
    private static final long serialVersionUID = -3699685106324048226L;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    private ZoneId sinkTimeZone = ZoneId.of("UTC");

    public SQLSinkBuilder() {
    }

    private SQLSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public void addSink(StreamExecutionEnvironment env, DataStream<RowData> rowDataDataStream, Table table, List<String> columnNameList, List<LogicalType> columnTypeList) {
    }

    private DataStream<Row> buildRow(
        DataStream<Map> filterOperator,
        List<String> columnNameList,
        List<LogicalType> columnTypeList,
        String schemaTableName) {
        final String[] columnNames = columnNameList.toArray(new String[columnNameList.size()]);
        final LogicalType[] columnTypes = columnTypeList.toArray(new LogicalType[columnTypeList.size()]);
        TypeInformation<?>[] typeInformations = TypeConversions.fromDataTypeToLegacyInfo(TypeConversions.fromLogicalToDataType(columnTypes));
        RowTypeInfo rowTypeInfo = new RowTypeInfo(typeInformations, columnNames);
        return filterOperator
            .flatMap(new FlatMapFunction<Map, Row>() {
                @Override
                public void flatMap(Map value, Collector<Row> out) throws Exception {
                    try {
                        switch (value.get("op").toString()) {
                            case "r":
                            case "c":
                                Row irow = Row.withPositions(RowKind.INSERT, columnNameList.size());
                                Map idata = (Map) value.get("after");
                                for (int i = 0; i < columnNameList.size(); i++) {
                                    irow.setField(i, convertValue(idata.get(columnNameList.get(i)), columnTypeList.get(i)));
                                }
                                out.collect(irow);
                                break;
                            case "d":
                                Row drow = Row.withPositions(RowKind.DELETE, columnNameList.size());
                                Map ddata = (Map) value.get("before");
                                for (int i = 0; i < columnNameList.size(); i++) {
                                    drow.setField(i, convertValue(ddata.get(columnNameList.get(i)), columnTypeList.get(i)));
                                }
                                out.collect(drow);
                                break;
                            case "u":
                                Row ubrow = Row.withPositions(RowKind.UPDATE_BEFORE, columnNameList.size());
                                Map ubdata = (Map) value.get("before");
                                for (int i = 0; i < columnNameList.size(); i++) {
                                    ubrow.setField(i, convertValue(ubdata.get(columnNameList.get(i)), columnTypeList.get(i)));
                                }
                                out.collect(ubrow);
                                Row uarow = Row.withPositions(RowKind.UPDATE_AFTER, columnNameList.size());
                                Map uadata = (Map) value.get("after");
                                for (int i = 0; i < columnNameList.size(); i++) {
                                    uarow.setField(i, convertValue(uadata.get(columnNameList.get(i)), columnTypeList.get(i)));
                                }
                                out.collect(uarow);
                                break;
                            default:
                        }
                    } catch (Exception e) {
                        logger.error("SchameTable: {} - Row: {} - Exception:", schemaTableName, JSONUtil.toJsonString(value),e);
                        throw e;
                    }
                }
            }, rowTypeInfo);
    }

    private void addTableSink(
        int indexSink,
        CustomTableEnvironment customTableEnvironment,
        DataStream<Row> rowDataDataStream,
        Table table,
        List<String> columnNameList) {
        String sinkSchemaName = getSinkSchemaName(table);
        String tableName = getSinkTableName(table);
        String sinkTableName = tableName + "_" + indexSink;
        String pkList = StringUtils.join(getPKList(table), ".");
        String viewName = "VIEW_" + table.getSchemaTableNameWithUnderline();
        try {
            customTableEnvironment.createTemporaryView(viewName, rowDataDataStream, StringUtils.join(columnNameList, ","));
            logger.info("Create " + viewName + " temporaryView successful...");
        } catch (ValidationException exception) {
            if (!exception.getMessage().contains("already exists")) {
                logger.error(exception.getMessage(), exception);
            }
        }
        String flinkDDL = FlinkBaseUtil.getFlinkDDL(table, "" + sinkTableName, config, sinkSchemaName, tableName, pkList);
        logger.info(flinkDDL);
        customTableEnvironment.executeSql(flinkDDL);
        logger.info("Create " + sinkTableName + " FlinkSQL DDL successful...");
        String cdcSqlInsert = FlinkBaseUtil.getCDCSqlInsert(table, sinkTableName, viewName, config);
        logger.info(cdcSqlInsert);
        List<Operation> operations = customTableEnvironment.getParser().parse(cdcSqlInsert);
        logger.info("Create " + sinkTableName + " FlinkSQL insert into successful...");
        try {
            if (operations.size() > 0) {
                Operation operation = operations.get(0);
                if (operation instanceof ModifyOperation) {
                    modifyOperations.add((ModifyOperation) operation);
                }
            }
        } catch (Exception e) {
            logger.error("Translate to plan occur exception: {}", e);
            throw e;
        }
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
                    OutputTag<Map> outputTag = new OutputTag<Map>(sinkTableName) {
                    };
                    tagMap.put(table, outputTag);
                    tableMap.put(table.getSchemaTableName(), table);
                }
            }
            final String schemaFieldName = config.getSchemaFieldName();
            ObjectMapper objectMapper = new ObjectMapper();
            SingleOutputStreamOperator<Map> mapOperator = dataStreamSource.map(x -> objectMapper.readValue(x, Map.class)).returns(Map.class);
            SingleOutputStreamOperator<Map> processOperator = mapOperator.process(new ProcessFunction<Map, Map>() {
                @Override
                public void processElement(Map map, ProcessFunction<Map, Map>.Context ctx, Collector<Map> out) throws Exception {
                    LinkedHashMap source = (LinkedHashMap) map.get("source");
                    try {
                        String tableName = SplitUtil.getReValue(source.get(schemaFieldName).toString(), splitConfMap) + "." + SplitUtil.getReValue(source.get("table").toString(), splitConfMap);
                        Table table = tableMap.get(tableName);
                        OutputTag<Map> outputTag = tagMap.get(table);
                        Optional.ofNullable(outputTag).orElseThrow(() -> new RuntimeException("data outPutTag is not exists!table name is  " + tableName));
                        ctx.output(outputTag, map);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        out.collect(map);
                    }
                }
            });
            final int indexSink = atomicInteger.getAndAdd(1);
            tagMap.forEach((table, tag) -> {
                final String schemaTableName = table.getSchemaTableName();
                try {
                    DataStream<Map> filterOperator = shunt(processOperator, table, tag);
                    logger.info("Build " + schemaTableName + " shunt successful...");
                    List<String> columnNameList = new ArrayList<>();
                    List<LogicalType> columnTypeList = new ArrayList<>();
                    buildColumn(columnNameList, columnTypeList, table.getColumns());
                    DataStream<Row> rowDataDataStream = buildRow(filterOperator, columnNameList, columnTypeList, schemaTableName).rebalance();
                    logger.info("Build " + schemaTableName + " flatMap successful...");
                    logger.info("Start build " + schemaTableName + " sink...");
                    addTableSink(indexSink, customTableEnvironment, rowDataDataStream, table, columnNameList);
                } catch (Exception e) {
                    logger.error("Build " + schemaTableName + " cdc sync failed...");
                    logger.error(LogUtil.getError(e));
                }
            });
            List<Transformation<?>> trans = customTableEnvironment.getPlanner().translate(modifyOperations);
            for (Transformation<?> item : trans) {
                env.addOperator(item);
            }
            logger.info("A total of " + trans.size() + " table cdc sync were build successfull...");
        }
        return dataStreamSource;
    }

    @Override
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
        } else if (logicalType instanceof TimestampType) {
            if (value instanceof Integer) {
                return Instant.ofEpochMilli(((Integer) value).longValue()).atZone(sinkTimeZone).toLocalDateTime();
            } else if (value instanceof Long) {
                return Instant.ofEpochMilli((long) value).atZone(sinkTimeZone).toLocalDateTime();
            } else {
                return Instant.parse(value.toString()).atZone(sinkTimeZone).toLocalDateTime();
            }
        } else if (logicalType instanceof DecimalType) {
            return new BigDecimal(value.toString());
        } else if (logicalType instanceof FloatType) {
            if (value instanceof Float) {
                return value;
            } else if (value instanceof Double) {
                return ((Double) value).floatValue();
            } else {
                return Float.parseFloat(value.toString());
            }
        } else if (logicalType instanceof BigIntType) {
            if (value instanceof Integer) {
                return ((Integer) value).longValue();
            } else {
                return value;
            }
        } else if (logicalType instanceof VarBinaryType) {
            // VARBINARY AND BINARY is converted to String with encoding base64 in FlinkCDC.
            if (value instanceof String) {
                return DatatypeConverter.parseBase64Binary(value.toString());
            } else {
                return value;
            }
        } else {
            return value;
        }
    }
}
