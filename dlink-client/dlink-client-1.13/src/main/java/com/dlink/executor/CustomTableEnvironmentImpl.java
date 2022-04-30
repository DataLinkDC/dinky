package com.dlink.executor;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.dag.Transformation;
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
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.catalog.FunctionCatalog;
import org.apache.flink.table.catalog.GenericInMemoryCatalog;
import org.apache.flink.table.delegation.Executor;
import org.apache.flink.table.delegation.ExecutorFactory;
import org.apache.flink.table.delegation.Planner;
import org.apache.flink.table.delegation.PlannerFactory;
import org.apache.flink.table.expressions.Expression;
import org.apache.flink.table.expressions.ExpressionParser;
import org.apache.flink.table.factories.ComponentFactoryService;
import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.table.functions.TableAggregateFunction;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.table.functions.UserDefinedFunctionHelper;
import org.apache.flink.table.module.ModuleManager;
import org.apache.flink.table.operations.ExplainOperation;
import org.apache.flink.table.operations.JavaDataStreamQueryOperation;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.QueryOperation;
import org.apache.flink.table.operations.command.ResetOperation;
import org.apache.flink.table.operations.command.SetOperation;
import org.apache.flink.table.planner.delegation.ExecutorBase;
import org.apache.flink.table.planner.utils.ExecutorUtils;
import org.apache.flink.table.typeutils.FieldInfoUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.dlink.assertion.Asserts;
import com.dlink.result.SqlExplainResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 定制TableEnvironmentImpl
 *
 * @author wenmo
 * @since 2021/6/7 22:06
 **/
public class CustomTableEnvironmentImpl extends TableEnvironmentImpl implements CustomTableEnvironment {

    private final StreamExecutionEnvironment executionEnvironment;

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
            userClassLoader);
        this.executionEnvironment = executionEnvironment;
    }

    public static CustomTableEnvironmentImpl create(StreamExecutionEnvironment executionEnvironment) {
        return create(executionEnvironment, EnvironmentSettings.newInstance().build());
    }

    public static CustomTableEnvironmentImpl createBatch(StreamExecutionEnvironment executionEnvironment) {
        Configuration configuration = new Configuration();
        configuration.set(ExecutionOptions.RUNTIME_MODE, RuntimeExecutionMode.BATCH);
        TableConfig tableConfig = new TableConfig();
        tableConfig.addConfiguration(configuration);
        return create(executionEnvironment, EnvironmentSettings.newInstance().useBlinkPlanner().inBatchMode().build(), tableConfig);
    }

    public static CustomTableEnvironmentImpl create(
        StreamExecutionEnvironment executionEnvironment,
        EnvironmentSettings settings) {
        return create(executionEnvironment, settings, new TableConfig());
    }

    public static CustomTableEnvironmentImpl create(
        StreamExecutionEnvironment executionEnvironment,
        EnvironmentSettings settings,
        TableConfig tableConfig) {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        ModuleManager moduleManager = new ModuleManager();

        CatalogManager catalogManager =
            CatalogManager.newBuilder()
                .classLoader(classLoader)
                .config(tableConfig.getConfiguration())
                .defaultCatalog(
                    settings.getBuiltInCatalogName(),
                    new GenericInMemoryCatalog(
                        settings.getBuiltInCatalogName(),
                        settings.getBuiltInDatabaseName()))
                .executionConfig(executionEnvironment.getConfig())
                .build();

        FunctionCatalog functionCatalog = new FunctionCatalog(tableConfig, catalogManager, moduleManager);

        Map<String, String> executorProperties = settings.toExecutorProperties();
        Executor executor = lookupExecutor(executorProperties, executionEnvironment);

        Map<String, String> plannerProperties = settings.toPlannerProperties();
        Planner planner =
            ComponentFactoryService.find(PlannerFactory.class, plannerProperties)
                .create(
                    plannerProperties,
                    executor,
                    tableConfig,
                    functionCatalog,
                    catalogManager);

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

    private static Executor lookupExecutor(
        Map<String, String> executorProperties,
        StreamExecutionEnvironment executionEnvironment) {
        try {
            ExecutorFactory executorFactory =
                ComponentFactoryService.find(ExecutorFactory.class, executorProperties);
            Method createMethod =
                executorFactory
                    .getClass()
                    .getMethod("create", Map.class, StreamExecutionEnvironment.class);

            return (Executor)
                createMethod.invoke(executorFactory, executorProperties, executionEnvironment);
        } catch (Exception e) {
            throw new TableException(
                "Could not instantiate the executor. Make sure a planner module is on the classpath",
                e);
        }
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
            List<Transformation<?>> trans = getPlanner().translate(modifyOperations);
            if (execEnv instanceof ExecutorBase) {
                StreamGraph streamGraph = ExecutorUtils.generateStreamGraph(((ExecutorBase) execEnv).getExecutionEnvironment(), trans);
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
            } else {
                throw new TableException("Unsupported SQL query! explainSql() need a single SQL to query.");
            }
        }
    }

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
        if (execEnv instanceof ExecutorBase) {
            StreamGraph streamGraph = ExecutorUtils.generateStreamGraph(((ExecutorBase) execEnv).getExecutionEnvironment(), trans);
            if (tableConfig.getConfiguration().containsKey(PipelineOptions.NAME.key())) {
                streamGraph.setJobName(tableConfig.getConfiguration().getString(PipelineOptions.NAME));
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
            throw new TableException(
                "Unsupported SQL query! explainSql() only accepts a single SQL query.");
        }
        List<Operation> operationlist = new ArrayList<>(operations);
        for (int i = 0; i < operationlist.size(); i++) {
            Operation operation = operationlist.get(i);
            if (operation instanceof ModifyOperation) {
                record.setType("Modify DML");
            } else if (operation instanceof ExplainOperation) {
                record.setType("Explain DML");
            } else if (operation instanceof QueryOperation) {
                record.setType("Query DML");
            } else {
                record.setExplain(operation.asSummaryString());
                operationlist.remove(i);
                record.setType("DDL");
                i = i - 1;
            }
        }
        record.setExplainTrue(true);
        if (operationlist.size() == 0) {
            //record.setExplain("DDL语句不进行解释。");
            return record;
        }
        record.setExplain(planner.explain(operationlist, extraDetails));
        return record;
    }

    public <T> void registerFunction(String name, TableFunction<T> tableFunction) {
        TypeInformation<T> typeInfo = UserDefinedFunctionHelper.getReturnTypeOfTableFunction(tableFunction);
        this.functionCatalog.registerTempSystemTableFunction(name, tableFunction, typeInfo);
    }

    public <T, ACC> void registerFunction(String name, AggregateFunction<T, ACC> aggregateFunction) {
        TypeInformation<T> typeInfo = UserDefinedFunctionHelper.getReturnTypeOfAggregateFunction(aggregateFunction);
        TypeInformation<ACC> accTypeInfo = UserDefinedFunctionHelper.getAccumulatorTypeOfAggregateFunction(aggregateFunction);
        this.functionCatalog.registerTempSystemAggregateFunction(name, aggregateFunction, typeInfo, accTypeInfo);
    }

    public <T, ACC> void registerFunction(String name, TableAggregateFunction<T, ACC> tableAggregateFunction) {
        TypeInformation<T> typeInfo = UserDefinedFunctionHelper.getReturnTypeOfAggregateFunction(tableAggregateFunction);
        TypeInformation<ACC> accTypeInfo = UserDefinedFunctionHelper.getAccumulatorTypeOfAggregateFunction(tableAggregateFunction);
        this.functionCatalog.registerTempSystemAggregateFunction(name, tableAggregateFunction, typeInfo, accTypeInfo);
    }

    public boolean parseAndLoadConfiguration(String statement, StreamExecutionEnvironment environment, Map<String, Object> setMap) {
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

    private void callSet(SetOperation setOperation, StreamExecutionEnvironment environment, Map<String, Object> setMap) {
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

    private void callReset(ResetOperation resetOperation, StreamExecutionEnvironment environment, Map<String, Object> setMap) {
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

    public <T> Table fromDataStream(DataStream<T> dataStream, String fields) {
        List<Expression> expressions = ExpressionParser.parseExpressionList(fields);
        return this.fromDataStream(dataStream, (Expression[]) expressions.toArray(new Expression[0]));
    }

    public <T> Table fromDataStream(DataStream<T> dataStream, Expression... fields) {
        JavaDataStreamQueryOperation<T> queryOperation = this.asQueryOperation(dataStream, Optional.of(Arrays.asList(fields)));
        return this.createTable(queryOperation);
    }

    @Override
    public <T> void createTemporaryView(String path, DataStream<T> dataStream, String fields) {
        this.createTemporaryView(path, this.fromDataStream(dataStream, fields));
    }

    @Override
    public <T> void createTemporaryView(String path, DataStream<T> dataStream, Expression... fields) {
        this.createTemporaryView(path, this.fromDataStream(dataStream, fields));
    }

    private <T> JavaDataStreamQueryOperation<T> asQueryOperation(DataStream<T> dataStream, Optional<List<Expression>> fields) {
        TypeInformation<T> streamType = dataStream.getType();
        FieldInfoUtils.TypeInfoSchema typeInfoSchema = (FieldInfoUtils.TypeInfoSchema) fields.map((f) -> {
            FieldInfoUtils.TypeInfoSchema fieldsInfo = FieldInfoUtils.getFieldsInfo(streamType, (Expression[]) f.toArray(new Expression[0]));
            this.validateTimeCharacteristic(fieldsInfo.isRowtimeDefined());
            return fieldsInfo;
        }).orElseGet(() -> {
            return FieldInfoUtils.getFieldsInfo(streamType);
        });
        return new JavaDataStreamQueryOperation(dataStream, typeInfoSchema.getIndices(), typeInfoSchema.toResolvedSchema());
    }

    private void validateTimeCharacteristic(boolean isRowtimeDefined) {
        if (isRowtimeDefined && this.executionEnvironment.getStreamTimeCharacteristic() != TimeCharacteristic.EventTime) {
            throw new ValidationException(
                String.format("A rowtime attribute requires an EventTime time characteristic in stream environment. But is: %s", this.executionEnvironment.getStreamTimeCharacteristic()));
        }
    }
}
