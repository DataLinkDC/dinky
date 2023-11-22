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

package org.dinky.cdc.doris;

import org.dinky.cdc.SinkBuilder;
import org.dinky.data.model.Column;
import org.dinky.data.model.FlinkCDCConfig;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
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

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class DorisExtendSinkBuilder extends DorisSinkBuilder implements Serializable {

    public static final String KEY_WORD = "datastream-doris-ext";
    private static final long serialVersionUID = 8430362249137471854L;

    protected static final Logger logger = LoggerFactory.getLogger(DorisExtendSinkBuilder.class);

    private Map<String, AdditionalColumnEntry<String, String>> additionalColumnConfigList = null;

    public DorisExtendSinkBuilder() {}

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

    @SuppressWarnings("rawtypes")
    @Override
    protected Object buildRowDataValues(Map value, RowKind rowKind, String columnName, LogicalType columnType) {
        if (additionalColumnConfigList == null) {
            return convertValue(getOriginRowData(rowKind, value).get(columnName), columnType);
        }

        AdditionalColumnEntry<String, String> col = additionalColumnConfigList.get(columnName);
        if (col == null) {
            return convertValue(getOriginRowData(rowKind, value).get(columnName), columnType);
        }

        if (!"META".equals(col.getKey())) {
            return convertValue(col.getValue(), columnType);
        }

        Map source = (Map) value.get("source");
        switch (col.getValue()) {
            case "op_ts":
                Object opVal = source.get("ts_ms");
                if (opVal instanceof Integer) {
                    return TimestampData.fromLocalDateTime(Instant.ofEpochMilli(((Integer) opVal).longValue())
                            .atZone(this.getSinkTimeZone())
                            .toLocalDateTime());
                }

                if (opVal instanceof Long) {
                    return TimestampData.fromLocalDateTime(Instant.ofEpochMilli((long) opVal)
                            .atZone(this.getSinkTimeZone())
                            .toLocalDateTime());
                }

                return TimestampData.fromLocalDateTime(Instant.parse(value.toString())
                        .atZone(this.getSinkTimeZone())
                        .toLocalDateTime());
            case "database_name":
                return convertValue(source.get("db"), columnType);
            case "table_name":
                return convertValue(source.get("table"), columnType);
            case "schema_name":
                return convertValue(source.get("schema"), columnType);
            default:
                logger.warn("Unsupported meta field: {}", col.getValue());
                return null;
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected DataStream<RowData> buildRowData(
            SingleOutputStreamOperator<Map> filterOperator,
            List<String> columnNameList,
            List<LogicalType> columnTypeList,
            String schemaTableName) {
        logger.info("sinkTimeZone:{}", this.getSinkTimeZone());
        return filterOperator.flatMap(sinkRowDataFunction(columnNameList, columnTypeList, schemaTableName));
    }

    @Override
    protected void buildColumn(List<String> columnNameList, List<LogicalType> columnTypeList, List<Column> columns) {
        for (Column column : columns) {
            columnNameList.add(column.getName());
            columnTypeList.add(getLogicalType(column));
        }
        if (this.additionalColumnConfigList != null && this.additionalColumnConfigList.size() > 0) {
            logger.info("Start add additional column");
            this.additionalColumnConfigList.forEach((key, value) -> {
                logger.info("col: { name: {}, type:{}, val: {}}", key, value.getKey(), value.getValue());

                switch (value.getKey()) {
                    case "META":
                        switch (value.getValue().toLowerCase()) {
                            case "op_ts":
                                columnNameList.add(key);
                                columnTypeList.add(new TimestampType());
                                break;
                            case "database_name":
                            case "table_name":
                            case "schema_name":
                                columnNameList.add(key);
                                columnTypeList.add(new VarCharType());
                                break;
                            default:
                                logger.warn("Unsupported meta field:{}", value.getValue());
                        }
                        break;
                    case "BOOLEAN":
                        columnNameList.add(key);
                        columnTypeList.add(new BooleanType());
                        break;
                    case "INT":
                        columnNameList.add(key);
                        columnTypeList.add(new IntType());
                        break;
                    case "TINYINT":
                        columnNameList.add(key);
                        columnTypeList.add(new TinyIntType());
                        break;
                    case "BIGINT":
                        columnNameList.add(key);
                        columnTypeList.add(new BigIntType());
                        break;
                    case "DECIMAL":
                        columnNameList.add(key);
                        columnTypeList.add(new DecimalType());
                        break;
                    case "FLOAT":
                        columnNameList.add(key);
                        columnTypeList.add(new FloatType());
                        break;
                    case "DATE":
                        columnNameList.add(key);
                        columnTypeList.add(new DateType());
                        break;
                    case "TIMESTAMP":
                        columnNameList.add(key);
                        columnTypeList.add(new TimestampType());
                        break;
                    case "CHAR":
                        columnNameList.add(key);
                        columnTypeList.add(new CharType());
                        break;
                    case "VARCHAR":
                    case "STRING":
                        columnNameList.add(key);
                        columnTypeList.add(new VarCharType());
                        break;
                    default:
                        logger.warn("Unsupported additional column type:{}", value.getKey());
                        break;
                }
            });
            logger.info("Additional column added complete");
        }
    }

    protected Map<String, AdditionalColumnEntry<String, String>> buildAdditionalColumnsConfig() {
        String additionalColumnConfig = config.getSink().get(DorisExtendSinkOptions.AdditionalColumns.key());
        if (Strings.isNullOrEmpty(additionalColumnConfig)) {
            return null;
        }

        logger.info("AdditionalColumns: {}", additionalColumnConfig);

        Map<String, AdditionalColumnEntry<String, String>> cfg = new HashMap<>();
        String[] cols = additionalColumnConfig.split(",");
        for (String col : cols) {
            String[] kv = col.split(":");
            if (kv.length != 2) {
                logger.warn("additional-columns format invalid. col={}", col);
                return null;
            }

            String[] strs = kv[1].split("@");
            if (strs.length != 2) {
                logger.warn("additional-columns format invalid. val={}", kv[1]);
                return null;
            }

            AdditionalColumnEntry<String, String> item =
                    AdditionalColumnEntry.of(strs[0].trim().toUpperCase(), strs[1]);
            cfg.put(kv[0].trim(), item);
        }
        return cfg;
    }
}
