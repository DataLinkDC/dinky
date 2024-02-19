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
import org.dinky.cdc.utils.FlinkStatementUtil;
import org.dinky.data.model.Column;
import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;
import org.dinky.executor.CustomTableEnvironment;
import org.dinky.utils.JsonUtils;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.data.DecimalData;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.StringData;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.types.logical.BigIntType;
import org.apache.flink.table.types.logical.BooleanType;
import org.apache.flink.table.types.logical.DateType;
import org.apache.flink.table.types.logical.DecimalType;
import org.apache.flink.table.types.logical.DoubleType;
import org.apache.flink.table.types.logical.FloatType;
import org.apache.flink.table.types.logical.IntType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.SmallIntType;
import org.apache.flink.table.types.logical.TimestampType;
import org.apache.flink.table.types.logical.TinyIntType;
import org.apache.flink.table.types.logical.VarBinaryType;
import org.apache.flink.table.types.logical.VarCharType;
import org.apache.flink.types.RowKind;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public abstract class AbstractSinkBuilder implements SinkBuilder {
    protected ObjectMapper objectMapper = new ObjectMapper();

    protected static final Logger logger = LoggerFactory.getLogger(AbstractSinkBuilder.class);

    protected FlinkCDCConfig config;
    protected List<ModifyOperation> modifyOperations = new ArrayList<>();
    private ZoneId sinkTimeZone = ZoneId.of("UTC");

    protected List<ConvertType> typeConverterList = null;

    protected AbstractSinkBuilder() {
        initTypeConverterList();
    }

    protected AbstractSinkBuilder(FlinkCDCConfig config) {
        this.config = config;
        initTypeConverterList();
    }

    protected void initTypeConverterList() {
        typeConverterList = Lists.newArrayList(
                this::convertVarCharType,
                this::convertDateType,
                this::convertVarBinaryType,
                this::convertBigIntType,
                this::convertFloatType,
                this::convertDecimalType,
                this::convertTimestampType);
    }

    public FlinkCDCConfig getConfig() {
        return config;
    }

    public void setConfig(FlinkCDCConfig config) {
        this.config = config;
    }

    protected Properties getProperties() {
        Properties properties = new Properties();
        Map<String, String> sink = config.getSink();
        for (Map.Entry<String, String> entry : sink.entrySet()) {
            if (Asserts.isNotNullString(entry.getKey())
                    && entry.getKey().startsWith("properties")
                    && Asserts.isNotNullString(entry.getValue())) {
                properties.setProperty(entry.getKey().replace("properties.", ""), entry.getValue());
            }
        }
        return properties;
    }

    protected SingleOutputStreamOperator<Map> deserialize(DataStreamSource<String> dataStreamSource) {
        return dataStreamSource
                .map((MapFunction<String, Map>) value -> objectMapper.readValue(value, Map.class))
                .returns(Map.class);
    }

    protected SingleOutputStreamOperator<Map> shunt(
            SingleOutputStreamOperator<Map> mapOperator, Table table, String schemaFieldName) {
        final String tableName = table.getName();
        final String schemaName = table.getSchema();
        return mapOperator.filter((FilterFunction<Map>) value -> {
            LinkedHashMap source = (LinkedHashMap) value.get("source");
            return tableName.equals(source.get("table").toString())
                    && schemaName.equals(source.get(schemaFieldName).toString());
        });
    }

    protected DataStream<Map> shunt(SingleOutputStreamOperator<Map> processOperator, Table table, OutputTag<Map> tag) {
        processOperator.forward();
        return processOperator.getSideOutput(tag).forward();
    }

    @SuppressWarnings("rawtypes")
    protected DataStream<RowData> buildRowData(
            SingleOutputStreamOperator<Map> filterOperator,
            List<String> columnNameList,
            List<LogicalType> columnTypeList,
            String schemaTableName) {
        return filterOperator
                .flatMap(sinkRowDataFunction(columnNameList, columnTypeList, schemaTableName))
                .returns(RowData.class);
    }

    @SuppressWarnings("rawtypes")
    protected FlatMapFunction<Map, RowData> sinkRowDataFunction(
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
    protected void rowDataCollect(
            List<String> columnNameList,
            List<LogicalType> columnTypeList,
            Collector<RowData> out,
            RowKind rowKind,
            Map value) {
        GenericRowData genericRowData = new GenericRowData(rowKind, columnNameList.size());
        for (int i = 0; i < columnNameList.size(); i++) {
            genericRowData.setField(
                    i, buildRowDataValues(value, rowKind, columnNameList.get(i), columnTypeList.get(i)));
        }
        out.collect(genericRowData);
    }

    @SuppressWarnings("rawtypes")
    protected Object buildRowDataValues(Map value, RowKind rowKind, String columnName, LogicalType columnType) {
        Map data = getOriginRowData(rowKind, value);
        return convertValue(data.get(columnName), columnType);
    }

    @SuppressWarnings("rawtypes")
    protected Map getOriginRowData(RowKind rowKind, Map value) {
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

    public void addSink(
            StreamExecutionEnvironment env,
            DataStream<RowData> rowDataDataStream,
            Table table,
            List<String> columnNameList,
            List<LogicalType> columnTypeList) {}

    protected List<Operation> createInsertOperations(
            CustomTableEnvironment customTableEnvironment, Table table, String viewName, String tableName) {
        String cdcSqlInsert = FlinkStatementUtil.getCDCInsertSql(table, tableName, viewName);
        logger.info(cdcSqlInsert);

        List<Operation> operations = customTableEnvironment.getParser().parse(cdcSqlInsert);
        logger.info("Create {} FlinkSQL insert into successful...", tableName);
        if (operations.isEmpty()) {
            return operations;
        }

        try {
            Operation operation = operations.get(0);
            if (operation instanceof ModifyOperation) {
                modifyOperations.add((ModifyOperation) operation);
            }

        } catch (Exception e) {
            logger.error("Translate to plan occur exception: {}", e.toString());
            throw e;
        }
        return operations;
    }

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
        final String schemaFieldName = config.getSchemaFieldName();

        if (Asserts.isNotNullCollection(schemaList)) {
            SingleOutputStreamOperator<Map> mapOperator = deserialize(dataStreamSource);
            for (Schema schema : schemaList) {
                for (Table table : schema.getTables()) {
                    SingleOutputStreamOperator<Map> filterOperator = shunt(mapOperator, table, schemaFieldName);

                    List<String> columnNameList = new ArrayList<>();
                    List<LogicalType> columnTypeList = new ArrayList<>();

                    buildColumn(columnNameList, columnTypeList, table.getColumns());

                    DataStream<RowData> rowDataDataStream =
                            buildRowData(filterOperator, columnNameList, columnTypeList, table.getSchemaTableName());

                    addSink(env, rowDataDataStream, table, columnNameList, columnTypeList);
                }
            }
        }
        return dataStreamSource;
    }

    protected void buildColumn(List<String> columnNameList, List<LogicalType> columnTypeList, List<Column> columns) {
        for (Column column : columns) {
            columnNameList.add(column.getName());
            columnTypeList.add(getLogicalType(column));
        }
    }

    public LogicalType getLogicalType(Column column) {
        switch (column.getJavaType()) {
            case STRING:
                return new VarCharType();
            case BOOLEAN:
            case JAVA_LANG_BOOLEAN:
                return new BooleanType();
            case BYTE:
            case JAVA_LANG_BYTE:
                return new TinyIntType();
            case SHORT:
            case JAVA_LANG_SHORT:
                return new SmallIntType();
            case LONG:
            case JAVA_LANG_LONG:
                return new BigIntType();
            case FLOAT:
            case JAVA_LANG_FLOAT:
                return new FloatType();
            case DOUBLE:
            case JAVA_LANG_DOUBLE:
                return new DoubleType();
            case DECIMAL:
                if (column.getPrecision() == null || column.getPrecision() == 0) {
                    return new DecimalType(38, column.getScale());
                } else {
                    return new DecimalType(column.getPrecision(), column.getScale());
                }
            case INT:
            case INTEGER:
                return new IntType();
            case DATE:
            case LOCAL_DATE:
                return new DateType();
            case LOCAL_DATETIME:
            case TIMESTAMP:
                if (column.getLength() != null) {
                    return new TimestampType(column.getLength());
                } else {
                    return new TimestampType(3);
                }
            case BYTES:
                return new VarBinaryType(Integer.MAX_VALUE);
            default:
                return new VarCharType();
        }
    }

    protected Object convertValue(Object value, LogicalType logicalType) {
        if (value == null) {
            return null;
        }

        for (ConvertType convertType : typeConverterList) {
            Optional<Object> result = convertType.convert(value, logicalType);
            if (result.isPresent()) {
                return result.get();
            }
        }
        return value;
    }

    protected Optional<Object> convertVarBinaryType(Object value, LogicalType logicalType) {
        if (logicalType instanceof VarBinaryType) {
            // VARBINARY AND BINARY is converted to String with encoding base64 in FlinkCDC.
            if (value instanceof String) {
                return Optional.of(DatatypeConverter.parseBase64Binary(value.toString()));
            }

            return Optional.of(value);
        }
        return Optional.empty();
    }

    protected Optional<Object> convertBigIntType(Object value, LogicalType logicalType) {
        if (logicalType instanceof BigIntType) {
            if (value instanceof Integer) {
                return Optional.of(((Integer) value).longValue());
            }

            return Optional.of(value);
        }
        return Optional.empty();
    }

    protected Optional<Object> convertFloatType(Object value, LogicalType logicalType) {
        if (logicalType instanceof FloatType) {
            if (value instanceof Float) {
                return Optional.of(value);
            }

            if (value instanceof Double) {
                return Optional.of(((Double) value).floatValue());
            }

            return Optional.of(Float.parseFloat(value.toString()));
        }
        return Optional.empty();
    }

    protected Optional<Object> convertDecimalType(Object value, LogicalType logicalType) {
        if (logicalType instanceof DecimalType) {
            final DecimalType decimalType = (DecimalType) logicalType;
            return Optional.ofNullable(DecimalData.fromBigDecimal(
                    new BigDecimal((String) value), decimalType.getPrecision(), decimalType.getScale()));
        }
        return Optional.empty();
    }

    protected Optional<Object> convertTimestampType(Object value, LogicalType logicalType) {
        if (logicalType instanceof TimestampType) {
            if (value instanceof Integer) {
                return Optional.of(Instant.ofEpochMilli(((Integer) value).longValue())
                        .atZone(sinkTimeZone)
                        .toLocalDateTime());
            } else if (value instanceof String) {
                return Optional.of(
                        Instant.parse((String) value).atZone(sinkTimeZone).toLocalDateTime());
            } else {
                TimestampType logicalType1 = (TimestampType) logicalType;
                if (logicalType1.getPrecision() == 3) {
                    return Optional.of(Instant.ofEpochMilli((long) value)
                            .atZone(sinkTimeZone)
                            .toLocalDateTime());
                } else if (logicalType1.getPrecision() > 3) {
                    return Optional.of(
                            Instant.ofEpochMilli(((long) value) / (long) Math.pow(10, logicalType1.getPrecision() - 3))
                                    .atZone(sinkTimeZone)
                                    .toLocalDateTime());
                }
                return Optional.of(Instant.ofEpochSecond(((long) value))
                        .atZone(sinkTimeZone)
                        .toLocalDateTime());
            }
        }
        return Optional.empty();
    }

    protected Optional<Object> convertDateType(Object target, LogicalType logicalType) {
        if (logicalType instanceof DateType) {
            return Optional.of(StringData.fromString(Instant.ofEpochMilli((long) target)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .toString()));
        }
        return Optional.empty();
    }

    protected Optional<Object> convertVarCharType(Object target, LogicalType logicalType) {
        if (logicalType instanceof VarCharType) {
            return Optional.of(StringData.fromString((String) target));
        }
        return Optional.empty();
    }

    @FunctionalInterface
    public interface ConvertType {
        Optional<Object> convert(Object target, LogicalType logicalType);
    }

    @Override
    public String getSinkSchemaName(Table table) {
        return config.getSink().getOrDefault("sink.db", table.getSchema());
    }

    @Override
    public String getSinkTableName(Table table) {
        String tableName = table.getName();
        Map<String, String> sink = config.getSink();
        if (Boolean.parseBoolean(sink.get("table.prefix.schema"))) {
            tableName = table.getSchema() + "_" + tableName;
        }

        tableName = sink.getOrDefault("table.prefix", "") + tableName + sink.getOrDefault("table.suffix", "");

        if (Boolean.parseBoolean(sink.get("table.lower"))) {
            tableName = tableName.toLowerCase();
        }

        if (Boolean.parseBoolean(sink.get("table.upper"))) {
            tableName = tableName.toUpperCase();
        }
        return tableName;
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

    protected ZoneId getSinkTimeZone() {
        return this.sinkTimeZone;
    }
}
