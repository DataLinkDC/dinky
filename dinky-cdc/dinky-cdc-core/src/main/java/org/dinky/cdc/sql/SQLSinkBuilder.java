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

import org.dinky.cdc.SinkBuilder;
import org.dinky.cdc.utils.FlinkStatementUtil;
import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.data.model.Table;
import org.dinky.executor.CustomTableEnvironment;
import org.dinky.utils.SplitUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.types.logical.DateType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.TimestampType;
import org.apache.flink.types.Row;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import cn.hutool.core.collection.CollUtil;

public class SQLSinkBuilder extends AbstractSqlSinkBuilder implements Serializable {

    public static final String KEY_WORD = "sql";
    private static final long serialVersionUID = -3699685106324048226L;

    public SQLSinkBuilder() {}

    private SQLSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    protected void initTypeConverterList() {
        typeConverterList = CollUtil.newArrayList(
                this::convertDateType,
                this::convertTimestampType,
                this::convertFloatType,
                this::convertDecimalType,
                this::convertBigIntType,
                this::convertVarBinaryType);
    }

    private String addSourceTableView(
            CustomTableEnvironment customTableEnvironment, DataStream<Row> rowDataDataStream, Table table) {
        // 上游表名称
        String viewName = "VIEW_" + table.getSchemaTableNameWithUnderline();
        customTableEnvironment.createTemporaryView(
                viewName, customTableEnvironment.fromChangelogStream(rowDataDataStream));
        logger.info("Create {} temporaryView successful...", viewName);
        return viewName;
    }

    @Override
    protected void addTableSink(
            CustomTableEnvironment customTableEnvironment, DataStream<Row> rowDataDataStream, Table table) {
        String viewName = addSourceTableView(customTableEnvironment, rowDataDataStream, table);

        // 下游库名称
        String sinkSchemaName = getSinkSchemaName(table);
        // 下游表名称
        String sinkTableName = getSinkTableName(table);

        // 这个地方要根据下游表的数量进行生成
        if (CollectionUtils.isEmpty(config.getSinks())) {
            addSinkInsert(customTableEnvironment, table, viewName, sinkTableName, sinkSchemaName, sinkTableName);
        } else {
            for (int index = 0; index < config.getSinks().size(); index++) {
                String tableName = sinkTableName;
                if (config.getSinks().size() != 1) {
                    tableName = sinkTableName + "_" + index;
                }

                config.setSink(config.getSinks().get(index));
                addSinkInsert(customTableEnvironment, table, viewName, tableName, sinkSchemaName, sinkTableName);
            }
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
                FlinkStatementUtil.getFlinkDDL(table, tableName, config, sinkSchemaName, sinkTableName, pkList);
        logger.info(flinkDDL);
        customTableEnvironment.executeSql(flinkDDL);
        logger.info("Create {} FlinkSQL DDL successful...", tableName);

        return createInsertOperations(customTableEnvironment, table, viewName, tableName);
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
    protected String createTableName(LinkedHashMap source, String schemaFieldName, Map<String, String> split) {
        return SplitUtil.getReValue(source.get(schemaFieldName).toString(), split)
                + "."
                + SplitUtil.getReValue(source.get("table").toString(), split);
    }

    @Override
    protected Optional<Object> convertDateType(Object value, LogicalType logicalType) {
        if (logicalType instanceof DateType) {
            if (value instanceof Integer) {
                return Optional.of(LocalDate.ofEpochDay((Integer) value));
            }
            if (value instanceof Long) {
                return Optional.of(
                        Instant.ofEpochMilli((long) value).atZone(sinkTimeZone).toLocalDate());
            }
            return Optional.of(
                    Instant.parse(value.toString()).atZone(sinkTimeZone).toLocalDate());
        }
        return Optional.empty();
    }

    @Override
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
}
