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

package com.dlink.cdc.doris;

import com.dlink.cdc.SinkBuilder;
import com.dlink.model.Column;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.utils.JSONUtil;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.TimestampData;
import org.apache.flink.table.types.logical.BigIntType;
import org.apache.flink.table.types.logical.BooleanType;
import org.apache.flink.table.types.logical.CharType;
import org.apache.flink.table.types.logical.DateType;
import org.apache.flink.table.types.logical.DecimalType;
import org.apache.flink.table.types.logical.FloatType;
import org.apache.flink.table.types.logical.IntType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.TimestampType;
import org.apache.flink.table.types.logical.TinyIntType;
import org.apache.flink.table.types.logical.VarCharType;
import org.apache.flink.types.RowKind;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BillyXing
 * @since 2022/11/10 19:37
 */
public class DorisExtendSinkBuilder extends DorisSinkBuilder implements Serializable {

    private static final String KEY_WORD = "datastream-doris-ext";
    private static final long serialVersionUID = 8430362249137471854L;

    protected static final Logger logger = LoggerFactory.getLogger(DorisSinkBuilder.class);

    private Map<String, AdditionalColumnEntry<String, String>> additionalColumnConfigList = null;

    public DorisExtendSinkBuilder() {
    }

    public DorisExtendSinkBuilder(FlinkCDCConfig config) {
        super(config);
        additionalColumnConfigList = buildAdditionalColumnsConfig();
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public SinkBuilder create(FlinkCDCConfig config) {
        return new DorisExtendSinkBuilder(config);
    }

    protected Object buildRowDataValues(Map value, Map rowData, String columnName, LogicalType columnType,
                                        Map<String, AdditionalColumnEntry<String, String>> aColumnConfigList,
                                        ZoneId opTimeZone) {
        if (aColumnConfigList != null && aColumnConfigList.size() > 0
                && aColumnConfigList.containsKey(columnName)) {
            AdditionalColumnEntry<String, String> col = aColumnConfigList.get(columnName);
            if (col != null) {
                if ("META".equals(col.getKey())) {
                    switch (col.getValue()) {
                        case "op_ts":
                            Object opVal = ((Map) value.get("source")).get("ts_ms");
                            if (opVal instanceof Integer) {
                                return TimestampData
                                        .fromLocalDateTime(Instant.ofEpochMilli(((Integer) opVal).longValue())
                                                .atZone(opTimeZone).toLocalDateTime());
                            } else if (opVal instanceof Long) {
                                return TimestampData.fromLocalDateTime(
                                        Instant.ofEpochMilli((long) opVal).atZone(opTimeZone).toLocalDateTime());
                            } else {
                                return TimestampData.fromLocalDateTime(
                                        Instant.parse(value.toString()).atZone(opTimeZone).toLocalDateTime());
                            }
                        case "database_name":
                            return convertValue(((Map) value.get("source")).get("db"), columnType);
                        case "table_name":
                            return convertValue(((Map) value.get("source")).get("table"), columnType);
                        case "schema_name":
                            return convertValue(((Map) value.get("source")).get("schema"), columnType);
                        default:
                            logger.warn("Unsupported meta field:" + col.getValue());
                            return null;
                    }
                } else {
                    return convertValue(col.getValue(), columnType);
                }
            }
        }
        return convertValue(rowData.get(columnName), columnType);
    }

    @Override
    protected DataStream<RowData> buildRowData(
                                               SingleOutputStreamOperator<Map> filterOperator,
                                               List<String> columnNameList,
                                               List<LogicalType> columnTypeList,
                                               String schemaTableName) {
        final Map<String, AdditionalColumnEntry<String, String>> aColumnConfigList = this.additionalColumnConfigList;
        final ZoneId opTimeZone = this.getSinkTimeZone();
        logger.info("sinkTimeZone:" + this.getSinkTimeZone().toString());
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
                                                buildRowDataValues(value, idata, columnNameList.get(i),
                                                        columnTypeList.get(i), aColumnConfigList, opTimeZone));
                                    }
                                    out.collect(igenericRowData);
                                    break;
                                case "d":
                                    GenericRowData dgenericRowData = new GenericRowData(columnNameList.size());
                                    dgenericRowData.setRowKind(RowKind.DELETE);
                                    Map ddata = (Map) value.get("before");
                                    for (int i = 0; i < columnNameList.size(); i++) {
                                        dgenericRowData.setField(i,
                                                buildRowDataValues(value, ddata, columnNameList.get(i),
                                                        columnTypeList.get(i), aColumnConfigList, opTimeZone));
                                    }
                                    out.collect(dgenericRowData);
                                    break;
                                case "u":
                                    GenericRowData ubgenericRowData = new GenericRowData(columnNameList.size());
                                    ubgenericRowData.setRowKind(RowKind.UPDATE_BEFORE);
                                    Map ubdata = (Map) value.get("before");
                                    for (int i = 0; i < columnNameList.size(); i++) {
                                        ubgenericRowData.setField(i,
                                                buildRowDataValues(value, ubdata, columnNameList.get(i),
                                                        columnTypeList.get(i), aColumnConfigList, opTimeZone));
                                    }
                                    out.collect(ubgenericRowData);
                                    GenericRowData uagenericRowData = new GenericRowData(columnNameList.size());
                                    uagenericRowData.setRowKind(RowKind.UPDATE_AFTER);
                                    Map uadata = (Map) value.get("after");
                                    for (int i = 0; i < columnNameList.size(); i++) {
                                        uagenericRowData.setField(i,
                                                buildRowDataValues(value, uadata, columnNameList.get(i),
                                                        columnTypeList.get(i), aColumnConfigList, opTimeZone));
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

    @Override
    protected void buildColumn(List<String> columnNameList, List<LogicalType> columnTypeList, List<Column> columns) {
        for (Column column : columns) {
            columnNameList.add(column.getName());
            columnTypeList.add(getLogicalType(column));
        }
        if (this.additionalColumnConfigList != null && this.additionalColumnConfigList.size() > 0) {
            logger.info("Start add additional column");
            for (Map.Entry col : this.additionalColumnConfigList.entrySet()) {
                String colName = (String) col.getKey();
                AdditionalColumnEntry<String, String> kv = (AdditionalColumnEntry<String, String>) col.getValue();
                logger.info("col: { name: " + colName + ", type:" + kv.getKey() + ", val: " + kv.getValue() + "}");

                switch (kv.getKey()) {
                    case "META":
                        switch (kv.getValue().toLowerCase()) {
                            case "op_ts":
                                columnNameList.add(colName);
                                columnTypeList.add(new TimestampType());
                                break;
                            case "database_name":
                            case "table_name":
                            case "schema_name":
                                columnNameList.add(colName);
                                columnTypeList.add(new VarCharType());
                                break;
                            default:
                                logger.warn("Unsupported meta field:" + kv.getValue());
                        }
                        break;
                    case "BOOLEAN":
                        columnNameList.add(colName);
                        columnTypeList.add(new BooleanType());
                        break;
                    case "INT":
                        columnNameList.add(colName);
                        columnTypeList.add(new IntType());
                        break;
                    case "TINYINT":
                        columnNameList.add(colName);
                        columnTypeList.add(new TinyIntType());
                        break;
                    case "BIGINT":
                        columnNameList.add(colName);
                        columnTypeList.add(new BigIntType());
                        break;
                    case "DECIMAL":
                        columnNameList.add(colName);
                        columnTypeList.add(new DecimalType());
                        break;
                    case "FLOAT":
                        columnNameList.add(colName);
                        columnTypeList.add(new FloatType());
                        break;
                    case "DATE":
                        columnNameList.add(colName);
                        columnTypeList.add(new DateType());
                        break;
                    case "TIMESTAMP":
                        columnNameList.add(colName);
                        columnTypeList.add(new TimestampType());
                        break;
                    case "CHAR":
                        columnNameList.add(colName);
                        columnTypeList.add(new CharType());
                        break;
                    case "VARCHAR":
                    case "STRING":
                        columnNameList.add(colName);
                        columnTypeList.add(new VarCharType());
                        break;
                    default:
                        logger.warn("Unsupported additional column type:" + kv.getKey());
                        break;
                }
            }
            logger.info("Additional column added complete");
        }

    }

    protected Map<String, AdditionalColumnEntry<String, String>> buildAdditionalColumnsConfig() {
        if (!config.getSink().containsKey(DorisExtendSinkOptions.AdditionalColumns.key())) {
            return null;
        }

        String additionalColumnConfig = config.getSink().get(DorisExtendSinkOptions.AdditionalColumns.key());
        if (additionalColumnConfig == null || additionalColumnConfig.length() == 0) {
            return null;
        }

        Map<String, AdditionalColumnEntry<String, String>> cfg = new HashMap<>();
        logger.info("AdditionalColumns: " + additionalColumnConfig);
        String[] cols = additionalColumnConfig.split(",");

        for (String col : cols) {
            String[] kv = col.split(":");
            if (kv.length != 2) {
                logger.warn("additional-columns format invalid. col=" + col);
                return null;
            }

            String[] strs = kv[1].split("@");
            if (strs.length != 2) {
                logger.warn("additional-columns format invalid. val=" + kv[1]);
                return null;
            }

            AdditionalColumnEntry<String, String> item =
                    AdditionalColumnEntry.of(strs[0].trim().toUpperCase(), strs[1]);
            cfg.put(kv[0].trim(), item);
        }
        return cfg;
    }
}
