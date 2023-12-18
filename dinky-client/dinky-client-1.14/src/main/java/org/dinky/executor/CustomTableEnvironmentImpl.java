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

import org.dinky.assertion.Asserts;
import org.dinky.data.model.LineageRel;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.parser.CustomParserImpl;
import org.dinky.utils.JsonUtils;
import org.dinky.utils.LineageContext;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.ExecutionOptions;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.jsonplan.JsonPlanGenerator;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.JSONGenerator;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.ValidationException;
import org.apache.flink.table.api.bridge.java.internal.StreamTableEnvironmentImpl;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.catalog.FunctionCatalog;
import org.apache.flink.table.catalog.GenericInMemoryCatalog;
import org.apache.flink.table.delegation.Executor;
import org.apache.flink.table.delegation.ExecutorFactory;
import org.apache.flink.table.delegation.Planner;
import org.apache.flink.table.expressions.Expression;
import org.apache.flink.table.expressions.ExpressionParser;
import org.apache.flink.table.factories.FactoryUtil;
import org.apache.flink.table.factories.PlannerFactoryUtil;
import org.apache.flink.table.module.ModuleManager;
import org.apache.flink.table.operations.ExplainOperation;
import org.apache.flink.table.operations.JavaDataStreamQueryOperation;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.QueryOperation;
import org.apache.flink.table.operations.command.ResetOperation;
import org.apache.flink.table.operations.command.SetOperation;
import org.apache.flink.table.planner.delegation.DefaultExecutor;
import org.apache.flink.table.typeutils.FieldInfoUtils;
import org.apache.flink.types.Row;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.URLUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 定制TableEnvironmentImpl
 *
 * @since 2021/10/22 10:02
 */
@Slf4j
public class CustomTableEnvironmentImpl extends AbstractCustomTableEnvironment {

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
        super(new StreamTableEnvironmentImpl(
                catalogManager,
                moduleManager,
                functionCatalog,
                tableConfig,
                executionEnvironment,
                planner,
                executor,
                isStreamingMode,
                userClassLoader));
        Thread.currentThread().setContextClassLoader(userClassLoader);
        this.executor = executor;
        injectParser(new CustomParserImpl(getPlanner().getParser()));
        injectExtendedExecutor(new CustomExtendedOperationExecutorImpl(this));
    }

    public static CustomTableEnvironmentImpl create(
            StreamExecutionEnvironment executionEnvironment, ClassLoader classLoader) {
        return create(
                executionEnvironment, EnvironmentSettings.newInstance().build(), TableConfig.getDefault(), classLoader);
    }

    public static CustomTableEnvironmentImpl createBatch(
            StreamExecutionEnvironment executionEnvironment, ClassLoader classLoader) {
        Configuration configuration = new Configuration();
        configuration.set(ExecutionOptions.RUNTIME_MODE, RuntimeExecutionMode.BATCH);
        TableConfig tableConfig = new TableConfig();
        tableConfig.addConfiguration(configuration);
        return create(executionEnvironment, EnvironmentSettings.inBatchMode(), tableConfig, classLoader);
    }

    public static CustomTableEnvironmentImpl create(
            StreamExecutionEnvironment executionEnvironment,
            EnvironmentSettings settings,
            TableConfig tableConfig,
            ClassLoader classLoader) {

        // temporary solution until FLINK-15635 is fixed
        //        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        final ModuleManager moduleManager = new ModuleManager();

        final CatalogManager catalogManager = CatalogManager.newBuilder()
                .classLoader(classLoader)
                .config(tableConfig.getConfiguration())
                .defaultCatalog(
                        settings.getBuiltInCatalogName(),
                        new GenericInMemoryCatalog(settings.getBuiltInCatalogName(), settings.getBuiltInDatabaseName()))
                .executionConfig(executionEnvironment.getConfig())
                .build();

        final FunctionCatalog functionCatalog = new FunctionCatalog(tableConfig, catalogManager, moduleManager);

        final Executor executor = lookupExecutor(classLoader, settings.getExecutor(), executionEnvironment);

        final Planner planner = PlannerFactoryUtil.createPlanner(
                settings.getPlanner(), executor, tableConfig, catalogManager, functionCatalog);

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

    @Override
    public Configuration getRootConfiguration() {
        Method method =
                ReflectUtil.getMethod(this.getStreamExecutionEnvironment().getClass(), "getConfiguration");
        ReflectUtil.setAccessible(method);
        try {
            Object object = method.invoke(this.getStreamExecutionEnvironment());
            return (Configuration) object;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Executor lookupExecutor(
            ClassLoader classLoader, String executorIdentifier, StreamExecutionEnvironment executionEnvironment) {
        try {
            final ExecutorFactory executorFactory =
                    FactoryUtil.discoverFactory(classLoader, ExecutorFactory.class, executorIdentifier);
            final Method createMethod =
                    executorFactory.getClass().getMethod("create", StreamExecutionEnvironment.class);

            return (Executor) createMethod.invoke(executorFactory, executionEnvironment);
        } catch (Exception e) {
            throw new TableException(
                    "Could not instantiate the executor. Make sure a planner module is on the" + " classpath", e);
        }
    }

    @Override
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
            List<Transformation<?>> trans = getPlanner().translate(modifyOperations);
            if (executor instanceof DefaultExecutor) {
                StreamGraph streamGraph =
                        ((DefaultExecutor) executor).getExecutionEnvironment().generateStreamGraph(trans);
                JSONGenerator jsonGenerator = new JSONGenerator(streamGraph);
                String json = jsonGenerator.getJSON();
                return JsonUtils.parseObject(json);
            } else {
                throw new TableException("Unsupported SQL query! explainSql() need a single SQL to query.");
            }
        }
    }

    @Override
    public void addJar(File... jarPath) {
        Configuration configuration =
                (Configuration) getStreamExecutionEnvironment().getConfiguration();
        List<String> pathList =
                Arrays.stream(URLUtil.getURLs(jarPath)).map(URL::toString).collect(Collectors.toList());
        List<String> jars = configuration.get(PipelineOptions.JARS);
        if (jars != null) {
            CollUtil.addAll(jars, pathList);
        }
        Map<String, Object> flinkConfigurationMap = getFlinkConfigurationMap();
        flinkConfigurationMap.put(PipelineOptions.JARS.key(), jars);
    }

    @Override
    public <T> void addConfiguration(ConfigOption<T> option, T value) {
        Map<String, Object> flinkConfigurationMap = getFlinkConfigurationMap();
        flinkConfigurationMap.put(option.key(), value);
    }

    private Map<String, Object> getFlinkConfigurationMap() {
        Field configuration = null;
        try {
            configuration = StreamExecutionEnvironment.class.getDeclaredField("configuration");
            configuration.setAccessible(true);
            Configuration o = (Configuration) configuration.get(getStreamExecutionEnvironment());
            Field confData = Configuration.class.getDeclaredField("confData");
            confData.setAccessible(true);
            Map<String, Object> temp = (Map<String, Object>) confData.get(o);
            return temp;
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        if (executor instanceof DefaultExecutor) {
            StreamGraph streamGraph =
                    ((DefaultExecutor) executor).getExecutionEnvironment().generateStreamGraph(trans);
            if (getConfig().getConfiguration().containsKey(PipelineOptions.NAME.key())) {
                streamGraph.setJobName(getConfig().getConfiguration().getString(PipelineOptions.NAME));
            }
            return streamGraph;
        } else {
            throw new TableException("Unsupported SQL query! ExecEnv need a ExecutorBase.");
        }
    }

    public JobGraph getJobGraphFromInserts(List<String> statements) {
        return getStreamGraphFromInserts(statements).getJobGraph();
    }

    public SqlExplainResult explainSqlRecord(String statement, ExplainDetail... extraDetails) {
        SqlExplainResult record = new SqlExplainResult();
        List<Operation> operations = getParser().parse(statement);
        record.setParseTrue(true);
        if (operations.size() != 1) {
            throw new TableException("Unsupported SQL query! explainSql() only accepts a single SQL query.");
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
        record.setExplain(getPlanner().explain(operations, extraDetails));
        return record;
    }

    public boolean parseAndLoadConfiguration(String statement, Map<String, Object> setMap) {
        List<Operation> operations = getParser().parse(statement);
        for (Operation operation : operations) {
            if (operation instanceof SetOperation) {
                callSet((SetOperation) operation, getStreamExecutionEnvironment(), setMap);
                return true;
            } else if (operation instanceof ResetOperation) {
                callReset((ResetOperation) operation, getStreamExecutionEnvironment(), setMap);
                return true;
            }
        }
        return false;
    }

    private void callSet(
            SetOperation setOperation, StreamExecutionEnvironment environment, Map<String, Object> setMap) {
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
            environment.getCheckpointConfig().configure(configuration);
            getConfig().addConfiguration(configuration);
        }
    }

    private void callReset(
            ResetOperation resetOperation, StreamExecutionEnvironment environment, Map<String, Object> setMap) {
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
            environment.getCheckpointConfig().configure(configuration);
            getConfig().addConfiguration(configuration);
        } else {
            setMap.clear();
        }
    }

    public <T> Table fromDataStream(DataStream<T> dataStream, String fields) {
        List<Expression> expressions = ExpressionParser.parseExpressionList(fields);
        return fromDataStream(dataStream, expressions.toArray(new Expression[0]));
    }

    @Override
    public <T> void createTemporaryView(String path, DataStream<T> dataStream, String fields) {
        createTemporaryView(path, fromDataStream(dataStream, fields));
    }

    @Override
    public List<LineageRel> getLineage(String statement) {
        LineageContext lineageContext = new LineageContext((TableEnvironmentImpl) streamTableEnvironment);
        return lineageContext.analyzeLineage(statement);
    }

    @Override
    public <T> void createTemporaryView(String s, DataStream<Row> dataStream, List<String> columnNameList) {
        createTemporaryView(s, fromChangelogStream(dataStream));
    }

    @Override
    public <T> void createTemporaryView(String path, DataStream<T> dataStream, Expression... fields) {
        createTemporaryView(path, fromDataStream(dataStream, fields));
    }

    private <T> JavaDataStreamQueryOperation<T> asQueryOperation(
            DataStream<T> dataStream, Optional<List<Expression>> fields) {
        TypeInformation<T> streamType = dataStream.getType();

        // get field names and types for all non-replaced fields
        FieldInfoUtils.TypeInfoSchema typeInfoSchema = fields.map(f -> {
                    FieldInfoUtils.TypeInfoSchema fieldsInfo =
                            FieldInfoUtils.getFieldsInfo(streamType, f.toArray(new Expression[0]));

                    // check if event-time is enabled
                    validateTimeCharacteristic(fieldsInfo.isRowtimeDefined());
                    return fieldsInfo;
                })
                .orElseGet(() -> FieldInfoUtils.getFieldsInfo(streamType));

        return new JavaDataStreamQueryOperation<>(
                dataStream, typeInfoSchema.getIndices(), typeInfoSchema.toResolvedSchema());
    }

    private void validateTimeCharacteristic(boolean isRowtimeDefined) {
        if (isRowtimeDefined
                && getStreamExecutionEnvironment().getStreamTimeCharacteristic() != TimeCharacteristic.EventTime) {
            throw new ValidationException(String.format(
                    "A rowtime attribute requires an EventTime time characteristic in"
                            + " stream environment. But is: %s",
                    getStreamExecutionEnvironment().getStreamTimeCharacteristic()));
        }
    }
}
