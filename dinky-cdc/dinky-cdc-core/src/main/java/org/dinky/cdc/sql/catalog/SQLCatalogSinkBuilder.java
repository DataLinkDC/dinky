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

package org.dinky.cdc.sql.catalog;

import org.dinky.cdc.SinkBuilder;
import org.dinky.cdc.sql.AbstractSqlSinkBuilder;
import org.dinky.cdc.utils.FlinkStatementUtil;
import org.dinky.data.flink.table.FlinkTableObjectIdentifier;
import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.data.model.Table;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.catalog.Column;
import org.apache.flink.table.catalog.ResolvedSchema;
import org.apache.flink.table.catalog.UniqueConstraint;
import org.apache.flink.table.types.AtomicDataType;
import org.apache.flink.types.Row;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SQLCatalogSinkBuilder extends AbstractSqlSinkBuilder implements Serializable {

    public static final String KEY_WORD = "sql-catalog";

    public SQLCatalogSinkBuilder() {}

    private SQLCatalogSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public void addTableSink(DataStream<Row> rowDataDataStream, Table table) {

        String catalogName = config.getSink().get("catalog.name");
        String sinkSchemaName = getSinkSchemaName(table);
        String tableName = getSinkTableName(table);
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

        createInsertOperations(
                table,
                FlinkTableObjectIdentifier.of(viewName),
                FlinkTableObjectIdentifier.of(catalogName, sinkSchemaName, tableName));
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public SinkBuilder create(FlinkCDCConfig config) {
        return new SQLCatalogSinkBuilder(config);
    }

    protected void executeCatalogStatement() {
        customTableEnvironment.executeSql(FlinkStatementUtil.getCreateCatalogStatement(config));
        logger.info("Build catalog successful...");
    }
}
