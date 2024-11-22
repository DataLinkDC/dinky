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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.types.Row;

import java.io.Serializable;
import java.util.List;

public class SQLSinkBuilder extends AbstractSqlSinkBuilder implements Serializable {

    public static final String KEY_WORD = "sql";
    private static final long serialVersionUID = -3699685106324048226L;

    public SQLSinkBuilder() {}

    private SQLSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    private String addSourceTableView(DataStream<Row> rowDataDataStream, Table table) {
        // Because the name of the view on Flink is not allowed to have -, it needs to be replaced with - here_
        String viewName = replaceViewNameMiddleLineToUnderLine("VIEW_" + table.getSchemaTableNameWithUnderline());

        customTableEnvironment.createTemporaryView(
                viewName, customTableEnvironment.fromChangelogStream(rowDataDataStream));
        logger.info("Create {} temporaryView successful...", viewName);
        return viewName;
    }

    @Override
    protected void addTableSink(DataStream<Row> rowDataDataStream, Table table) {
        final String viewName = addSourceTableView(rowDataDataStream, table);
        final String sinkSchemaName = getSinkSchemaName(table);
        final String sinkTableName = getSinkTableName(table);

        // Multiple sinks and single sink
        if (CollectionUtils.isEmpty(config.getSinks())) {
            addSinkInsert(table, viewName, sinkTableName, sinkSchemaName, sinkTableName);
        } else {
            for (int index = 0; index < config.getSinks().size(); index++) {
                String tableName = sinkTableName;
                if (config.getSinks().size() != 1) {
                    tableName = sinkTableName + "_" + index;
                }

                config.setSink(config.getSinks().get(index));
                addSinkInsert(table, viewName, tableName, sinkSchemaName, sinkTableName);
            }
        }
    }

    private List<Operation> addSinkInsert(
            Table table, String viewName, String tableName, String sinkSchemaName, String sinkTableName) {
        String pkList = StringUtils.join(getPKList(table), ".");
        String flinkDDL =
                FlinkStatementUtil.getFlinkDDL(table, tableName, config, sinkSchemaName, sinkTableName, pkList);
        logger.info(flinkDDL);
        customTableEnvironment.executeSql(flinkDDL);
        logger.info("Create {} FlinkSQL DDL successful...", tableName);
        return createInsertOperations(table, viewName, tableName);
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
