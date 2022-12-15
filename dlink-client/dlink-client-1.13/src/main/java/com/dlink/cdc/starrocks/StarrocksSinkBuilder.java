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

package com.dlink.cdc.starrocks;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.AbstractSinkBuilder;
import com.dlink.cdc.CDCBuilder;
import com.dlink.cdc.SinkBuilder;
import com.dlink.executor.CustomTableEnvironment;
import com.dlink.model.Column;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import com.dlink.utils.JSONUtil;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.logical.BigIntType;
import org.apache.flink.table.types.logical.DateType;
import org.apache.flink.table.types.logical.DecimalType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.TimestampType;
import org.apache.flink.table.types.logical.VarBinaryType;
import org.apache.flink.table.types.utils.TypeConversions;
import org.apache.flink.types.RowKind;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.starrocks.connector.flink.row.sink.StarRocksTableRowTransformer;
import com.starrocks.connector.flink.table.sink.StarRocksDynamicSinkFunction;
import com.starrocks.connector.flink.table.sink.StarRocksSinkOptions;

/**
 * StarrocksSinkBuilder
 *
 **/
public class StarrocksSinkBuilder extends AbstractSinkBuilder implements SinkBuilder, Serializable {

    public static final String KEY_WORD = "datastream-starrocks";
    private static final long serialVersionUID = 8330362249137431824L;
    private final ZoneId sinkZoneIdUTC = ZoneId.of("UTC");

    public StarrocksSinkBuilder() {
    }

    public StarrocksSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public SinkBuilder create(FlinkCDCConfig config) {
        return new StarrocksSinkBuilder(config);
    }

    public DataStreamSource build(
            CDCBuilder cdcBuilder,
            StreamExecutionEnvironment env,
            CustomTableEnvironment customTableEnvironment,
            DataStreamSource<String> dataStreamSource) {
        final List<Schema> schemaList = config.getSchemaList();
        if (Asserts.isNotNullCollection(schemaList)) {
            SingleOutputStreamOperator<Map> mapOperator = deserialize(dataStreamSource);
            String mergeFlag = config.getSink().get("sink.merge.table");
            if ("true".equalsIgnoreCase(mergeFlag)) {
                // 取第一个table
                Table table = schemaList.get(0).getTables().get(0);
                List<String> columnNameList = new ArrayList<>();
                List<LogicalType> columnTypeList = new ArrayList<>();
                buildColumn(columnNameList, columnTypeList, table.getColumns());
                DataStream<RowData> rowDataDataStream = buildRowData(mapOperator, columnNameList, columnTypeList,
                        table.getSchemaTableName());
                addSink(env, rowDataDataStream, table, columnNameList, columnTypeList);
            } else {
                final String schemaFieldName = config.getSchemaFieldName();
                for (Schema schema : schemaList) {
                    for (Table table : schema.getTables()) {
                        SingleOutputStreamOperator<Map> filterOperator = shunt(mapOperator, table, schemaFieldName);

                        List<String> columnNameList = new ArrayList<>();
                        List<LogicalType> columnTypeList = new ArrayList<>();

                        buildColumn(columnNameList, columnTypeList, table.getColumns());

                        DataStream<RowData> rowDataDataStream = buildRowData(filterOperator, columnNameList,
                                columnTypeList, table.getSchemaTableName());

                        addSink(env, rowDataDataStream, table, columnNameList, columnTypeList);
                    }
                }
            }
        }
        return dataStreamSource;
    }

    @Override
    public void addSink(
            StreamExecutionEnvironment env,
            DataStream<RowData> rowDataDataStream,
            Table table,
            List<String> columnNameList,
            List<LogicalType> columnTypeList) {
        try {
            List<Column> columns = table.getColumns();
            List<String> primaryKeys = new LinkedList<>();
            String[] columnNames = new String[columns.size()];
            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                if (column.isKeyFlag()) {
                    primaryKeys.add(column.getName());
                }
                columnNames[i] = column.getName();
            }
            String[] primaryKeyArrays = primaryKeys.stream().toArray(String[]::new);
            DataType[] dataTypes = new DataType[columnTypeList.size()];
            for (int i = 0; i < columnTypeList.size(); i++) {
                LogicalType logicalType = columnTypeList.get(i);
                String columnName = columnNameList.get(i);
                if (primaryKeys.contains(columnName)) {
                    logicalType = logicalType.copy(false);
                }
                dataTypes[i] = TypeConversions.fromLogicalToDataType(logicalType);
            }
            TableSchema tableSchema = TableSchema.builder().primaryKey(primaryKeyArrays).fields(columnNames, dataTypes)
                    .build();
            Map<String, String> sink = config.getSink();
            StarRocksSinkOptions.Builder builder = StarRocksSinkOptions.builder()
                    .withProperty("jdbc-url", sink.get("jdbc-url"))
                    .withProperty("load-url", sink.get("load-url"))
                    .withProperty("username", sink.get("username"))
                    .withProperty("password", sink.get("password"))
                    .withProperty("table-name", getSinkTableName(table))
                    .withProperty("database-name", getSinkSchemaName(table))
                    .withProperty("sink.properties.format", "json")
                    .withProperty("sink.properties.strip_outer_array", "true")
                    // 设置并行度，多并行度情况下需要考虑如何保证数据有序性
                    .withProperty("sink.parallelism", "1");
            sink.forEach((key, value) -> {
                if (key.startsWith("sink.")) {
                    builder.withProperty(key, value);
                }
            });
            StarRocksDynamicSinkFunction<RowData> starrocksSinkFunction = new StarRocksDynamicSinkFunction<RowData>(
                    builder.build(),
                    tableSchema,
                    new StarRocksTableRowTransformer(TypeInformation.of(RowData.class)));
            rowDataDataStream.addSink(starrocksSinkFunction);
            logger.info("handler connector name:{} sink successful.....", getHandle());
        } catch (Exception ex) {
            logger.error("handler connector name:{} sink ex:", getHandle(), ex);
        }
    }

    @Override
    protected Object convertValue(Object value, LogicalType logicalType) {
        if (value == null) {
            return null;
        }
        if (logicalType instanceof DateType) {
            if (value instanceof Integer) {
                return Instant.ofEpochMilli(((Integer) value).longValue()).atZone(sinkZoneIdUTC).toLocalDate();
            } else {
                return Instant.ofEpochMilli((long) value).atZone(sinkZoneIdUTC).toLocalDate();
            }
        } else if (logicalType instanceof TimestampType) {
            if (value instanceof Integer) {
                return Instant.ofEpochMilli(((Integer) value).longValue()).atZone(sinkZoneIdUTC).toLocalDateTime();
            } else if (value instanceof String) {
                return Instant.parse((String) value).atZone(ZoneId.systemDefault()).toLocalDateTime();
            } else {
                return Instant.ofEpochMilli((long) value).atZone(sinkZoneIdUTC).toLocalDateTime();
            }
        } else if (logicalType instanceof DecimalType) {
            return new BigDecimal((String) value);
        } else if (logicalType instanceof BigIntType) {
            if (value instanceof Integer) {
                return ((Integer) value).longValue();
            } else {
                return value;
            }
        } else if (logicalType instanceof VarBinaryType) {
            // VARBINARY AND BINARY is converted to String with encoding base64 in FlinkCDC.
            if (value instanceof String) {
                return DatatypeConverter.parseBase64Binary((String) value);
            } else {
                return value;
            }
        } else {
            return value;
        }
    }

    protected DataStream<RowData> buildRowData(
            SingleOutputStreamOperator<Map> filterOperator,
            List<String> columnNameList,
            List<LogicalType> columnTypeList,
            String schemaTableName) {
        return filterOperator
                .flatMap(new FlatMapFunction<Map, RowData>() {

                    @Override
                    public void flatMap(Map value, Collector<RowData> out) throws Exception {
                        try {
                            switch (value.get("op").toString()) {
                                case "r":
                                case "c":
                                    GenericRowData igenericRowData = new GenericRowData(columnNameList.size());
                                    igenericRowData.setRowKind(RowKind.INSERT);
                                    Map idata = (Map) value.get("after");
                                    for (int i = 0; i < columnNameList.size(); i++) {
                                        igenericRowData.setField(i,
                                                convertValue(idata.get(columnNameList.get(i)), columnTypeList.get(i)));
                                    }
                                    out.collect(igenericRowData);
                                    break;
                                case "d":
                                    GenericRowData dgenericRowData = new GenericRowData(columnNameList.size());
                                    dgenericRowData.setRowKind(RowKind.DELETE);
                                    Map ddata = (Map) value.get("before");
                                    for (int i = 0; i < columnNameList.size(); i++) {
                                        dgenericRowData.setField(i,
                                                convertValue(ddata.get(columnNameList.get(i)), columnTypeList.get(i)));
                                    }
                                    out.collect(dgenericRowData);
                                    break;
                                case "u":
                                    GenericRowData ubgenericRowData = new GenericRowData(columnNameList.size());
                                    ubgenericRowData.setRowKind(RowKind.UPDATE_BEFORE);
                                    Map ubdata = (Map) value.get("before");
                                    for (int i = 0; i < columnNameList.size(); i++) {
                                        ubgenericRowData.setField(i,
                                                convertValue(ubdata.get(columnNameList.get(i)), columnTypeList.get(i)));
                                    }
                                    out.collect(ubgenericRowData);
                                    GenericRowData uagenericRowData = new GenericRowData(columnNameList.size());
                                    uagenericRowData.setRowKind(RowKind.UPDATE_AFTER);
                                    Map uadata = (Map) value.get("after");
                                    for (int i = 0; i < columnNameList.size(); i++) {
                                        uagenericRowData.setField(i,
                                                convertValue(uadata.get(columnNameList.get(i)), columnTypeList.get(i)));
                                    }
                                    out.collect(uagenericRowData);
                                    break;
                                default:
                            }
                        } catch (Exception e) {
                            logger.error("SchameTable: {} - Row: {} - Exception: {}", schemaTableName,
                                    JSONUtil.toJsonString(value), e);
                            throw e;
                        }
                    }
                });
    }
}
