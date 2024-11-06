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

package org.dinky.executor;

import org.dinky.data.exception.DinkyException;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.operations.CustomNewParserImpl;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.ExecutionOptions;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.runtime.jobgraph.jsonplan.JsonPlanGenerator;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.JSONGenerator;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.ExplainFormat;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.config.TableConfigOptions;
import org.apache.flink.table.catalog.Catalog;
import org.apache.flink.table.catalog.CatalogDescriptor;
import org.apache.flink.table.catalog.ContextResolvedTable;
import org.apache.flink.table.catalog.ObjectIdentifier;
import org.apache.flink.table.catalog.ResolvedCatalogTable;
import org.apache.flink.table.catalog.StagedTable;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.sink.SinkStagingContext;
import org.apache.flink.table.connector.sink.abilities.SupportsStaging;
import org.apache.flink.table.execution.StagingSinkJobStatusHook;
import org.apache.flink.table.factories.TableFactoryUtil;
import org.apache.flink.table.module.Module;
import org.apache.flink.table.module.ModuleManager;
import org.apache.flink.table.operations.CreateTableASOperation;
import org.apache.flink.table.operations.ExplainOperation;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.QueryOperation;
import org.apache.flink.table.operations.ReplaceTableAsOperation;
import org.apache.flink.table.operations.ddl.CreateTableOperation;
import org.apache.flink.table.operations.utils.ExecutableOperationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * CustomTableEnvironmentImpl
 *
 * @since 2022/05/08
 */
public class CustomTableEnvironmentImpl extends AbstractCustomTableEnvironment {

    private static final Logger log = LoggerFactory.getLogger(CustomTableEnvironmentImpl.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    private List<ModifyOperation> modifyOperations = new ArrayList<>();

    public CustomTableEnvironmentImpl(StreamTableEnvironment streamTableEnvironment) {
        super(streamTableEnvironment);
        injectParser(new CustomNewParserImpl(this, getPlanner().getParser()));
    }

    public static CustomTableEnvironmentImpl create(
            StreamExecutionEnvironment executionEnvironment, ClassLoader classLoader) {
        return create(
                executionEnvironment,
                EnvironmentSettings.newInstance().withClassLoader(classLoader).build());
    }

    public static CustomTableEnvironmentImpl createBatch(
            StreamExecutionEnvironment executionEnvironment, ClassLoader classLoader) {
        return create(
                executionEnvironment,
                EnvironmentSettings.newInstance()
                        .withClassLoader(classLoader)
                        .inBatchMode()
                        .build());
    }

    public static CustomTableEnvironmentImpl create(
            StreamExecutionEnvironment executionEnvironment, EnvironmentSettings settings) {
        StreamTableEnvironment streamTableEnvironment = StreamTableEnvironment.create(executionEnvironment, settings);

        return new CustomTableEnvironmentImpl(streamTableEnvironment);
    }

    public List<ModifyOperation> getModifyOperations() {
        return modifyOperations;
    }

    public void addModifyOperations(ModifyOperation modifyOperation) {
        if (modifyOperation instanceof CreateTableASOperation) {
            modifyOperations.add(getModifyOperation((CreateTableASOperation) modifyOperation));
        } else if (modifyOperation instanceof ReplaceTableAsOperation) {
            modifyOperations.add(getModifyOperation((ReplaceTableAsOperation) modifyOperation));
        } else {
            modifyOperations.add(modifyOperation);
        }
    }

    private ModifyOperation getModifyOperation(CreateTableASOperation ctasOperation) {
        CreateTableOperation createTableOperation = ctasOperation.getCreateTableOperation();
        ObjectIdentifier tableIdentifier = createTableOperation.getTableIdentifier();
        Catalog catalog = getCatalogManager().getCatalogOrThrowException(tableIdentifier.getCatalogName());
        ResolvedCatalogTable catalogTable =
                getCatalogManager().resolveCatalogTable(createTableOperation.getCatalogTable());
        Optional<DynamicTableSink> stagingDynamicTableSink =
                getSupportsStagingDynamicTableSink(createTableOperation, catalog, catalogTable);
        if (stagingDynamicTableSink.isPresent()) {
            // use atomic ctas
            DynamicTableSink dynamicTableSink = stagingDynamicTableSink.get();
            SupportsStaging.StagingPurpose stagingPurpose = createTableOperation.isIgnoreIfExists()
                    ? SupportsStaging.StagingPurpose.CREATE_TABLE_AS_IF_NOT_EXISTS
                    : SupportsStaging.StagingPurpose.CREATE_TABLE_AS;
            StagedTable stagedTable =
                    ((SupportsStaging) dynamicTableSink).applyStaging(new SinkStagingContext(stagingPurpose));
            StagingSinkJobStatusHook stagingSinkJobStatusHook = new StagingSinkJobStatusHook(stagedTable);
            return ctasOperation.toStagedSinkModifyOperation(tableIdentifier, catalogTable, catalog, dynamicTableSink);
        }
        // use non-atomic ctas, create table first
        executeInternal(createTableOperation);
        return ctasOperation.toSinkModifyOperation(getCatalogManager());
    }

    private ModifyOperation getModifyOperation(ReplaceTableAsOperation rtasOperation) {
        CreateTableOperation createTableOperation = rtasOperation.getCreateTableOperation();
        ObjectIdentifier tableIdentifier = createTableOperation.getTableIdentifier();
        // First check if the replacedTable exists
        Optional<ContextResolvedTable> replacedTable = getCatalogManager().getTable(tableIdentifier);
        if (!rtasOperation.isCreateOrReplace() && !replacedTable.isPresent()) {
            throw new TableException(String.format(
                    "The table %s to be replaced doesn't exist. "
                            + "You can try to use CREATE TABLE AS statement or "
                            + "CREATE OR REPLACE TABLE AS statement.",
                    tableIdentifier));
        }
        Catalog catalog = getCatalogManager().getCatalogOrThrowException(tableIdentifier.getCatalogName());
        ResolvedCatalogTable catalogTable =
                getCatalogManager().resolveCatalogTable(createTableOperation.getCatalogTable());
        Optional<DynamicTableSink> stagingDynamicTableSink =
                getSupportsStagingDynamicTableSink(createTableOperation, catalog, catalogTable);
        if (stagingDynamicTableSink.isPresent()) {
            // use atomic rtas
            DynamicTableSink dynamicTableSink = stagingDynamicTableSink.get();
            SupportsStaging.StagingPurpose stagingPurpose = rtasOperation.isCreateOrReplace()
                    ? SupportsStaging.StagingPurpose.CREATE_OR_REPLACE_TABLE_AS
                    : SupportsStaging.StagingPurpose.REPLACE_TABLE_AS;

            StagedTable stagedTable =
                    ((SupportsStaging) dynamicTableSink).applyStaging(new SinkStagingContext(stagingPurpose));
            StagingSinkJobStatusHook stagingSinkJobStatusHook = new StagingSinkJobStatusHook(stagedTable);
            return rtasOperation.toStagedSinkModifyOperation(tableIdentifier, catalogTable, catalog, dynamicTableSink);
        }
        // non-atomic rtas drop table first if exists, then create
        if (replacedTable.isPresent()) {
            getCatalogManager().dropTable(tableIdentifier, false);
        }
        executeInternal(createTableOperation);
        return rtasOperation.toSinkModifyOperation(getCatalogManager());
    }

    private Optional<DynamicTableSink> getSupportsStagingDynamicTableSink(
            CreateTableOperation createTableOperation, Catalog catalog, ResolvedCatalogTable catalogTable) {
        TableConfig tableConfig = getTableEnvironment().getConfig();
        boolean isStreamingMode = true;
        RuntimeExecutionMode runtimeExecutionMode =
                getStreamExecutionEnvironment().getConfiguration().get(ExecutionOptions.RUNTIME_MODE);
        if (RuntimeExecutionMode.BATCH.equals(runtimeExecutionMode)) {
            isStreamingMode = false;
        }
        if (tableConfig.get(TableConfigOptions.TABLE_RTAS_CTAS_ATOMICITY_ENABLED)) {
            if (!TableFactoryUtil.isLegacyConnectorOptions(
                    catalog,
                    tableConfig,
                    isStreamingMode,
                    createTableOperation.getTableIdentifier(),
                    catalogTable,
                    createTableOperation.isTemporary())) {
                try {
                    DynamicTableSink dynamicTableSink = ExecutableOperationUtils.createDynamicTableSink(
                            catalog,
                            () -> (new ModuleManager()).getFactory((Module::getTableSinkFactory)),
                            createTableOperation.getTableIdentifier(),
                            catalogTable,
                            Collections.emptyMap(),
                            tableConfig,
                            getUserClassLoader(),
                            createTableOperation.isTemporary());
                    if (dynamicTableSink instanceof SupportsStaging) {
                        return Optional.of(dynamicTableSink);
                    }
                } catch (Exception e) {
                    throw new TableException(
                            String.format(
                                    "Fail to create DynamicTableSink for the table %s, "
                                            + "maybe the table does not support atomicity of CTAS/RTAS, "
                                            + "please set %s to false and try again.",
                                    createTableOperation.getTableIdentifier(),
                                    TableConfigOptions.TABLE_RTAS_CTAS_ATOMICITY_ENABLED.key()),
                            e);
                }
            }
        }
        return Optional.empty();
    }

    public void addOperator(Transformation<?> transformation) {
        getStreamExecutionEnvironment().addOperator(transformation);
    }

    public void clearModifyOperations() {
        modifyOperations.clear();
    }

    @Override
    public ObjectNode getStreamGraph(String statement) {
        List<Operation> operations = super.getParser().parse(statement);
        if (operations.size() != 1) {
            throw new TableException("Unsupported SQL query! explainSql() only accepts a single SQL query.");
        }

        List<ModifyOperation> modifyOperations = operations.stream()
                .filter(ModifyOperation.class::isInstance)
                .map(ModifyOperation.class::cast)
                .collect(Collectors.toList());

        StreamGraph streamGraph = transOperatoinsToStreamGraph(modifyOperations);
        JSONGenerator jsonGenerator = new JSONGenerator(streamGraph);
        try {
            return (ObjectNode) mapper.readTree(jsonGenerator.getJSON());
        } catch (JsonProcessingException e) {
            log.error("read streamGraph configure error: ", e);
            return mapper.createObjectNode();
        }
    }

    private StreamGraph transOperatoinsToStreamGraph(List<ModifyOperation> modifyOperations) {
        List<Transformation<?>> trans = getPlanner().translate(modifyOperations);
        final StreamExecutionEnvironment environment = getStreamExecutionEnvironment();
        trans.forEach(environment::addOperator);

        StreamGraph streamGraph = environment.getStreamGraph();
        final Configuration configuration = getConfig().getConfiguration();
        if (configuration.containsKey(PipelineOptions.NAME.key())) {
            streamGraph.setJobName(configuration.getString(PipelineOptions.NAME));
        }
        return streamGraph;
    }

    public List<Transformation<?>> transOperatoinsToTransformation(List<ModifyOperation> modifyOperations) {
        return getPlanner().translate(modifyOperations);
    }

    @Override
    public JobPlanInfo getJobPlanInfo(List<String> statements) {
        return new JobPlanInfo(JsonPlanGenerator.generatePlan(getJobGraphFromInserts(statements)));
    }

    @Override
    public StreamGraph getStreamGraphFromInserts(List<String> statements) {
        List<ModifyOperation> modifyOperations = new ArrayList<>();
        statements.stream().map(statement -> getParser().parse(statement)).forEach(operations -> {
            if (operations.size() != 1) {
                throw new TableException("Only single statement is supported.");
            }
            Operation operation = operations.get(0);
            if (operation instanceof ModifyOperation) {
                modifyOperations.add((ModifyOperation) operation);
            } else {
                throw new TableException("Only insert statement is supported now.");
            }
        });

        return transOperatoinsToStreamGraph(modifyOperations);
    }

    public Operation getOperationFromStatement(String statement) {
        List<Operation> operations = getParser().parse(statement);
        if (operations.isEmpty()) {
            throw new TableException("No statement is parsed.");
        }
        if (operations.size() > 1) {
            throw new TableException("Only single statement is supported.");
        }
        return operations.get(0);
    }

    public ModifyOperation getModifyOperationFromInsert(String statement) {
        List<Operation> operations = getParser().parse(statement);
        if (operations.isEmpty()) {
            throw new TableException("No statement is parsed.");
        }
        if (operations.size() > 1) {
            throw new TableException("Only single statement is supported.");
        }
        Operation operation = operations.get(0);
        if (operation instanceof ModifyOperation) {
            return (ModifyOperation) operation;
        } else if (operation instanceof QueryOperation) {
            log.info("Select statement is skipped.");
            return null;
        } else {
            throw new TableException("Only insert or select statement is supported now.");
        }
    }

    public StreamGraph getStreamGraph() {
        return transOperatoinsToStreamGraph(modifyOperations);
    }

    public StreamGraph getStreamGraphFromModifyOperations(List<ModifyOperation> modifyOperations) {
        return transOperatoinsToStreamGraph(modifyOperations);
    }

    @Override
    public void createCatalog(String catalogName, CatalogDescriptor catalogDescriptor) {
        getCatalogManager().createCatalog(catalogName, catalogDescriptor);
    }

    public SqlExplainResult explainSqlRecord(String statement, ExplainDetail... extraDetails) {
        List<Operation> operations = getParser().parse(statement);
        if (operations.size() != 1) {
            throw new DinkyException("Unsupported SQL explain! explainSql() only accepts a single SQL.");
        }
        SqlExplainResult record = new SqlExplainResult();
        if (operations.isEmpty()) {
            throw new DinkyException("No statement is explained.");
        }
        record.setParseTrue(true);
        Operation operation = operations.get(0);
        if (operation instanceof ModifyOperation) {
            if (operation instanceof ReplaceTableAsOperation) {
                record.setExplain(operation.asSummaryString());
                record.setType("RTAS");
            } else if (operation instanceof CreateTableASOperation) {
                record.setExplain(operation.asSummaryString());
                record.setType("CTAS");
            } else {
                record.setExplain(getPlanner().explain(operations, ExplainFormat.TEXT, extraDetails));
                record.setType("DML");
            }
        } else if (operation instanceof ExplainOperation) {
            record.setExplain(operation.asSummaryString());
            record.setType("Explain");
        } else if (operation instanceof QueryOperation) {
            record.setExplain(getPlanner().explain(operations, ExplainFormat.TEXT, extraDetails));
            record.setType("DQL");
        } else {
            record.setExplain(operation.asSummaryString());
            record.setType("DDL");
        }
        record.setExplainTrue(true);
        return record;
    }

    public SqlExplainResult explainModifyOperations(
            List<ModifyOperation> modifyOperations, ExplainDetail... extraDetails) {
        SqlExplainResult record = new SqlExplainResult();
        if (modifyOperations.isEmpty()) {
            throw new DinkyException("No modify operation is explained.");
        }
        record.setParseTrue(true);
        if (modifyOperations.size() == 1) {
            Operation operation = modifyOperations.get(0);
            if (operation instanceof ReplaceTableAsOperation) {
                record.setExplain(operation.asSummaryString());
                record.setType("RTAS");
            } else if (operation instanceof CreateTableASOperation) {
                record.setExplain(operation.asSummaryString());
                record.setType("CTAS");
            } else {
                record.setExplain(
                        getPlanner().explain(new ArrayList<>(modifyOperations), ExplainFormat.TEXT, extraDetails));
                record.setType("DML");
            }
        } else {
            record.setExplain(
                    getPlanner().explain(new ArrayList<>(modifyOperations), ExplainFormat.TEXT, extraDetails));
            record.setType("Statement Set");
        }
        record.setExplainTrue(true);
        return record;
    }
}
