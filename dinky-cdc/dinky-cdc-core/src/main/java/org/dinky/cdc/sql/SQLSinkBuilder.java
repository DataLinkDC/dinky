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
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.ValidationException;
import org.apache.flink.table.api.bridge.java.internal.StreamTableEnvironmentImpl;
import org.apache.flink.table.catalog.*;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.expressions.ApiExpressionUtils;
import org.apache.flink.table.operations.ExternalQueryOperation;
import org.apache.flink.table.operations.QueryOperation;
import org.apache.flink.table.operations.utils.OperationTreeBuilder;
import org.apache.flink.util.Preconditions;

import javax.annotation.Nullable;
import java.util.stream.Collectors;
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

//        customTableEnvironment.createTemporaryView(
//                viewName, customTableEnvironment.fromChangelogStream(rowDataDataStream));
//        logger.info("Create {} temporaryView successful...", viewName);
        if(!(customTableEnvironment.getTableEnvironment() instanceof StreamTableEnvironmentImpl)) {
            throw new ValidationException("The DataStream's StreamExecutionEnvironment must be identical to the one that has been passed to the StreamTableEnvironment during instantiation.");
        }
        UnresolvedIdentifier identifier = UnresolvedIdentifier.of(StringUtils.isBlank(table.getCatalog()) ? (StringUtils.isBlank(table.getDriverType()) ? "default_catalog" : table.getDriverType()).toLowerCase() : table.getCatalog(),
                StringUtils.isBlank(table.getSchema()) ? "default_database" : table.getSchema(), table.getName());
        customTableEnvironment.createTemporaryView(viewName,
                fromStreamInternal((StreamTableEnvironmentImpl) customTableEnvironment.getTableEnvironment(), rowDataDataStream, null,
                        identifier , ChangelogMode.insertOnly()));
        logger.info("Create {} temporaryView successful... {} \n", viewName, identifier.asSummaryString());
        return viewName;
    }

    protected <T> org.apache.flink.table.api.Table fromStreamInternal(
            StreamTableEnvironmentImpl environment,
            DataStream<T> dataStream,
            @Nullable Schema schema,
            @Nullable UnresolvedIdentifier unresolvedIdentifier,
            ChangelogMode changelogMode) {
        Preconditions.checkNotNull(dataStream, "Data stream must not be null.");
        Preconditions.checkNotNull(changelogMode, "Changelog mode must not be null.");

        CatalogManager catalogManager = environment.getCatalogManager();
        OperationTreeBuilder operationTreeBuilder = environment.getOperationTreeBuilder();

        SchemaTranslator.ConsumingResult schemaTranslationResult =
                SchemaTranslator.createConsumingResult(
                        catalogManager.getDataTypeFactory(), dataStream.getType(), schema);

        ResolvedCatalogTable resolvedCatalogTable =
                catalogManager.resolveCatalogTable(
                        new ExternalCatalogTable(schemaTranslationResult.getSchema()));

        ContextResolvedTable contextResolvedTable;
        if (unresolvedIdentifier != null) {
            ObjectIdentifier objectIdentifier =
                    catalogManager.qualifyIdentifier(unresolvedIdentifier);
            contextResolvedTable =
                    ContextResolvedTable.temporary(objectIdentifier, resolvedCatalogTable);
        } else {
            contextResolvedTable =
                    ContextResolvedTable.anonymous("datastream_source", resolvedCatalogTable);
        }

        QueryOperation scanOperation =
                new ExternalQueryOperation(
                        contextResolvedTable,
                        dataStream,
                        schemaTranslationResult.getPhysicalDataType(),
                        schemaTranslationResult.isTopLevelRecord(),
                        changelogMode);

        List<String> projections = schemaTranslationResult.getProjections();
        if (projections == null) {
            return environment.createTable(scanOperation);
        }

        final QueryOperation projectOperation =
                operationTreeBuilder.project(
                        projections.stream()
                                .map(ApiExpressionUtils::unresolvedRef)
                                .collect(Collectors.toList()),
                        scanOperation);


        return environment.createTable(projectOperation);
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
        String flinkDDL; boolean isTemporary = customTableEnvironment.getConfig().get(CREATE_TEMPORARY_TABLE);
        if(customTableEnvironment.getConfig().get(DROP_TABLE_IF_EXISTS)){
            flinkDDL = FlinkStatementUtil.getFlinkDropDDL(sinkSchemaName, sinkTableName, isTemporary);
            logger.info(flinkDDL);
            customTableEnvironment.executeSql(flinkDDL);
        }

        String pkList = StringUtils.join(getPKList(table), ".");
        flinkDDL = FlinkStatementUtil.getFlinkDDL(table, tableName, config, sinkSchemaName, sinkTableName, pkList, isTemporary);

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
