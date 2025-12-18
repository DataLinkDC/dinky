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
import org.dinky.data.flink.table.FlinkTableObjectIdentifier;
import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.data.model.Table;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.catalog.Column;
import org.apache.flink.table.catalog.ResolvedSchema;
import org.apache.flink.table.catalog.UniqueConstraint;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.types.AtomicDataType;
import org.apache.flink.types.Row;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SQLSinkBuilder extends AbstractSqlSinkBuilder implements Serializable {

    public static final String KEY_WORD = "sql";
    private static final long serialVersionUID = -3699685106324048226L;

    public SQLSinkBuilder() {}

    private SQLSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    private FlinkTableObjectIdentifier addSourceTableView(DataStream<Row> rowDataDataStream, Table table) {
        // Because the name of the view on Flink is not allowed to have -, it needs to be replaced with - here_
        String viewName = replaceViewNameMiddleLineToUnderLine("VIEW_" + table.getSchemaTableNameWithUnderline());
        final ResolvedSchema resolvedSchema =
                customTableEnvironment.fromChangelogStream(rowDataDataStream).getResolvedSchema();
        List<Column> columns = new ArrayList<>();
        for (Column column : resolvedSchema.getColumns()) {
            columns.add(column.copy(new AtomicDataType(
                    column.getDataType().getLogicalType().copy(false),
                    column.getDataType().getConversionClass())));
        }
        final UniqueConstraint primaryKey = UniqueConstraint.primaryKey(viewName + "_pk", table.getPrimaryKeys());
        final ResolvedSchema sinkSchema = new ResolvedSchema(columns, resolvedSchema.getWatermarkSpecs(), primaryKey);
        final Schema schema = Schema.newBuilder().fromResolvedSchema(sinkSchema).build();
        customTableEnvironment.createTemporaryView(
                viewName, customTableEnvironment.fromChangelogStream(rowDataDataStream, schema));
        logger.info("Create {} temporaryView successful...", viewName);
        return FlinkTableObjectIdentifier.of(viewName);
    }

    @Override
    protected void addTableSink(DataStream<Row> rowDataDataStream, Table table) {
        final FlinkTableObjectIdentifier viewName = addSourceTableView(rowDataDataStream, table);
        final String sinkSchemaName = getSinkSchemaName(table);
        final String sinkTableName = getSinkTableName(table);
        final FlinkTableObjectIdentifier sinkTable = FlinkTableObjectIdentifier.of(sinkTableName);

        // Multiple sinks and single sink
        if (CollectionUtils.isEmpty(config.getSinks())) {
            addSinkInsert(table, viewName, sinkTable, sinkSchemaName, sinkTable);
        } else {
            for (int index = 0; index < config.getSinks().size(); index++) {
                FlinkTableObjectIdentifier newSinkTable = sinkTable;
                if (config.getSinks().size() != 1) {
                    newSinkTable = FlinkTableObjectIdentifier.of(sinkTable + "_" + index);
                }

                config.setSink(config.getSinks().get(index));
                addSinkInsert(table, viewName, newSinkTable, sinkSchemaName, sinkTable);
            }
        }
    }

    private List<Operation> addSinkInsert(
            Table table,
            FlinkTableObjectIdentifier sourceTable,
            FlinkTableObjectIdentifier targetTable,
            String sinkSchemaName,
            FlinkTableObjectIdentifier sinkTable) {
        String pkList = StringUtils.join(getPKList(table), ".");
        String flinkDDL = FlinkStatementUtil.getFlinkDDL(table, targetTable, config, sinkSchemaName, sinkTable, pkList);
        logger.info(flinkDDL);
        customTableEnvironment.executeSql(flinkDDL);
        logger.info("Create {} FlinkSQL DDL successful...", targetTable);
        return createInsertOperations(table, sourceTable, targetTable);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public SinkBuilder create(FlinkCDCConfig config) {
        return new SQLSinkBuilder(config);
    }
}
