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

package com.dlink.executor;

import com.dlink.assertion.Asserts;
import com.dlink.model.LineageRel;
import com.dlink.result.SqlExplainResult;
import com.dlink.utils.FlinkStreamProgramWithoutPhysical;
import com.dlink.utils.LineageContext;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.ExecutionOptions;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.jsonplan.JsonPlanGenerator;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.JSONGenerator;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.bridge.internal.AbstractStreamTableEnvironmentImpl;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.catalog.FunctionCatalog;
import org.apache.flink.table.catalog.GenericInMemoryCatalog;
import org.apache.flink.table.delegation.Executor;
import org.apache.flink.table.delegation.ExpressionParser;
import org.apache.flink.table.delegation.Planner;
import org.apache.flink.table.expressions.Expression;
import org.apache.flink.table.factories.PlannerFactoryUtil;
import org.apache.flink.table.module.ModuleManager;
import org.apache.flink.table.operations.DataStreamQueryOperation;
import org.apache.flink.table.operations.ExplainOperation;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.QueryOperation;
import org.apache.flink.table.operations.command.ResetOperation;
import org.apache.flink.table.operations.command.SetOperation;
import org.apache.flink.table.planner.plan.optimize.program.FlinkChainedProgram;
import org.apache.flink.table.typeutils.FieldInfoUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * CustomTableEnvironmentImpl
 *
 * @author wenmo
 * @since 2022/05/08
 **/
public class CustomTableEnvironmentImpl extends AbstractStreamTableEnvironmentImpl
        implements
            CustomTableEnvironment {

    private final FlinkChainedProgram flinkChainedProgram;

    public CustomTableEnvironmentImpl(
                                      CatalogManager catalogManager,
                                      ModuleManager moduleManager,
                                      FunctionCatalog functionCatalog,
                                      TableConfig tableConfig,
                                      StreamExecutionEnvironment executionEnvironment,
                                      Planner planner,
                                      Executor executor,
                                      boolean isStreamingMode,
                                      ClassLoader userClassLoader) {
        super(
                catalogManager,
                moduleManager,
                tableConfig,
                executor,
                functionCatalog,
                planner,
                isStreamingMode,
                userClassLoader,
                executionEnvironment);
        this.flinkChainedProgram =
                FlinkStreamProgramWithoutPhysical.buildProgram((Configuration) executionEnvironment.getConfiguration());
    }

    public static CustomTableEnvironmentImpl create(StreamExecutionEnvironment executionEnvironment) {
        return create(executionEnvironment, EnvironmentSettings.newInstance().build());
    }

    public static CustomTableEnvironmentImpl createBatch(StreamExecutionEnvironment executionEnvironment) {
        Configuration configuration = new Configuration();
        configuration.set(ExecutionOptions.RUNTIME_MODE, RuntimeExecutionMode.BATCH);
        TableConfig tableConfig = new TableConfig();
        tableConfig.addConfiguration(configuration);
        return create(executionEnvironment, EnvironmentSettings.newInstance().inBatchMode().build());
    }

    public static CustomTableEnvironmentImpl create(
                                                    StreamExecutionEnvironment executionEnvironment,
                                                    EnvironmentSettings settings) {

        // temporary solution until FLINK-15635 is fixed
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        final Executor executor = lookupExecutor(classLoader, executionEnvironment);

        final TableConfig tableConfig = TableConfig.getDefault();
        tableConfig.setRootConfiguration(executor.getConfiguration());
        tableConfig.addConfiguration(settings.getConfiguration());

        final ModuleManager moduleManager = new ModuleManager();

        final CatalogManager catalogManager =
                CatalogManager.newBuilder()
                        .classLoader(classLoader)
                        .config(tableConfig)
                        .defaultCatalog(
                                settings.getBuiltInCatalogName(),
                                new GenericInMemoryCatalog(
                                        settings.getBuiltInCatalogName(),
                                        settings.getBuiltInDatabaseName()))
                        .executionConfig(executionEnvironment.getConfig())
                        .build();

        final FunctionCatalog functionCatalog =
                new FunctionCatalog(tableConfig, catalogManager, moduleManager);

        final Planner planner =
                PlannerFactoryUtil.createPlanner(
                        executor, tableConfig, moduleManager, catalogManager, functionCatalog);

        return new CustomTableEnvironmentImpl(
                catalogManager,
                moduleManager,
                functionCatalog,
                tableConfig,
                executionEnvironment,
                planner,
                executor,
                settings.isStreamingMode(),
                classLoader);
    }

    public ObjectNode getStreamGraph(String statement) {
        List<Operation> operations = super.getParser().parse(statement);
        if (operations.size() != 1) {
            throw new TableException("Unsupported SQL query! explainSql() only accepts a single SQL query.");
        } else {
            List<ModifyOperation> modifyOperations = new ArrayList<>();
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i) instanceof ModifyOperation) {
                    modifyOperations.add((ModifyOperation) operations.get(i));
                }
            }
            List<Transformation<?>> trans = super.planner.translate(modifyOperations);
            for (Transformation<?> transformation : trans) {
                executionEnvironment.addOperator(transformation);
            }
            StreamGraph streamGraph = executionEnvironment.getStreamGraph();
            if (tableConfig.getConfiguration().containsKey(PipelineOptions.NAME.key())) {
                streamGraph.setJobName(tableConfig.getConfiguration().getString(PipelineOptions.NAME));
            }
            JSONGenerator jsonGenerator = new JSONGenerator(streamGraph);
            String json = jsonGenerator.getJSON();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode objectNode = mapper.createObjectNode();
            try {
                objectNode = (ObjectNode) mapper.readTree(json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } finally {
                return objectNode;
            }
        }
    }

    @Override
    public JobPlanInfo getJobPlanInfo(List<String> statements) {
        return new JobPlanInfo(JsonPlanGenerator.generatePlan(getJobGraphFromInserts(statements)));
    }

    public StreamGraph getStreamGraphFromInserts(List<String> statements) {
        List<ModifyOperation> modifyOperations = new ArrayList();
        for (String statement : statements) {
            List<Operation> operations = getParser().parse(statement);
            if (operations.size() != 1) {
                throw new TableException("Only single statement is supported.");
            } else {
                Operation operation = operations.get(0);
                if (operation instanceof ModifyOperation) {
                    modifyOperations.add((ModifyOperation) operation);
                } else {
                    throw new TableException("Only insert statement is supported now.");
                }
            }
        }
        List<Transformation<?>> trans = getPlanner().translate(modifyOperations);
        for (Transformation<?> transformation : trans) {
            executionEnvironment.addOperator(transformation);
        }
        StreamGraph streamGraph = executionEnvironment.getStreamGraph();
        if (tableConfig.getConfiguration().containsKey(PipelineOptions.NAME.key())) {
            streamGraph.setJobName(tableConfig.getConfiguration().getString(PipelineOptions.NAME));
        }
        return streamGraph;
    }

    public JobGraph getJobGraphFromInserts(List<String> statements) {
        return getStreamGraphFromInserts(statements).getJobGraph();
    }

    public SqlExplainResult explainSqlRecord(String statement, ExplainDetail... extraDetails) {
        SqlExplainResult record = new SqlExplainResult();
        List<Operation> operations = getParser().parse(statement);
        record.setParseTrue(true);
        if (operations.size() != 1) {
            throw new TableException(
                    "Unsupported SQL query! explainSql() only accepts a single SQL query.");
        }
        Operation operation = operations.get(0);
        if (operation instanceof ModifyOperation) {
            record.setType("Modify DML");
        } else if (operation instanceof ExplainOperation) {
            record.setType("Explain DML");
        } else if (operation instanceof QueryOperation) {
            record.setType("Query DML");
        } else {
            record.setExplain(operation.asSummaryString());
            record.setType("DDL");
        }
        record.setExplainTrue(true);
        if ("DDL".equals(record.getType())) {
            // record.setExplain("DDL语句不进行解释。");
            return record;
        }
        record.setExplain(planner.explain(operations, extraDetails));
        return record;
    }

    public boolean parseAndLoadConfiguration(String statement, StreamExecutionEnvironment environment,
                                             Map<String, Object> setMap) {
        List<Operation> operations = getParser().parse(statement);
        for (Operation operation : operations) {
            if (operation instanceof SetOperation) {
                callSet((SetOperation) operation, environment, setMap);
                return true;
            } else if (operation instanceof ResetOperation) {
                callReset((ResetOperation) operation, environment, setMap);
                return true;
            }
        }
        return false;
    }

    private void callSet(SetOperation setOperation, StreamExecutionEnvironment environment,
                         Map<String, Object> setMap) {
        if (setOperation.getKey().isPresent() && setOperation.getValue().isPresent()) {
            String key = setOperation.getKey().get().trim();
            String value = setOperation.getValue().get().trim();
            if (Asserts.isNullString(key) || Asserts.isNullString(value)) {
                return;
            }
            Map<String, String> confMap = new HashMap<>();
            confMap.put(key, value);
            setMap.put(key, value);
            Configuration configuration = Configuration.fromMap(confMap);
            environment.getConfig().configure(configuration, null);
            getConfig().addConfiguration(configuration);
        }
    }

    private void callReset(ResetOperation resetOperation, StreamExecutionEnvironment environment,
                           Map<String, Object> setMap) {
        if (resetOperation.getKey().isPresent()) {
            String key = resetOperation.getKey().get().trim();
            if (Asserts.isNullString(key)) {
                return;
            }
            Map<String, String> confMap = new HashMap<>();
            confMap.put(key, null);
            setMap.remove(key);
            Configuration configuration = Configuration.fromMap(confMap);
            environment.getConfig().configure(configuration, null);
            getConfig().addConfiguration(configuration);
        } else {
            setMap.clear();
        }
    }

    public <T> Table fromDataStream(DataStream<T> dataStream, Expression... fields) {
        return createTable(asQueryOperation(dataStream, Optional.of(Arrays.asList(fields))));
    }

    public <T> Table fromDataStream(DataStream<T> dataStream, String fields) {
        List<Expression> expressions = ExpressionParser.INSTANCE.parseExpressionList(fields);
        return fromDataStream(dataStream, expressions.toArray(new Expression[0]));
    }

    @Override
    public <T> void createTemporaryView(String path, DataStream<T> dataStream, String fields) {
        createTemporaryView(path, fromDataStream(dataStream, fields));
    }

    @Override
    public List<LineageRel> getLineage(String statement) {
        LineageContext lineageContext = new LineageContext(flinkChainedProgram, this);
        return lineageContext.getLineage(statement);
    }

    @Override
    public <T> void createTemporaryView(
                                        String path, DataStream<T> dataStream, Expression... fields) {
        createTemporaryView(path, fromDataStream(dataStream, fields));
    }

    protected <T> DataStreamQueryOperation<T> asQueryOperation(DataStream<T> dataStream,
                                                               Optional<List<Expression>> fields) {
        TypeInformation<T> streamType = dataStream.getType();
        FieldInfoUtils.TypeInfoSchema typeInfoSchema = (FieldInfoUtils.TypeInfoSchema) fields.map((f) -> {
            FieldInfoUtils.TypeInfoSchema fieldsInfo =
                    FieldInfoUtils.getFieldsInfo(streamType, (Expression[]) f.toArray(new Expression[0]));
            this.validateTimeCharacteristic(fieldsInfo.isRowtimeDefined());
            return fieldsInfo;
        }).orElseGet(() -> {
            return FieldInfoUtils.getFieldsInfo(streamType);
        });
        return new DataStreamQueryOperation(dataStream, typeInfoSchema.getIndices(), typeInfoSchema.toResolvedSchema());
    }
}
