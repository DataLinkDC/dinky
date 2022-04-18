package com.dlink.executor;

import com.dlink.assertion.Asserts;
import com.dlink.result.SqlExplainResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.ValidationException;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.catalog.FunctionCatalog;
import org.apache.flink.table.catalog.GenericInMemoryCatalog;
import org.apache.flink.table.catalog.ObjectIdentifier;
import org.apache.flink.table.catalog.ResolvedSchema;
import org.apache.flink.table.catalog.SchemaResolver;
import org.apache.flink.table.catalog.SchemaTranslator;
import org.apache.flink.table.catalog.UnresolvedIdentifier;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.delegation.Executor;
import org.apache.flink.table.delegation.ExecutorFactory;
import org.apache.flink.table.delegation.Planner;
import org.apache.flink.table.expressions.ApiExpressionUtils;
import org.apache.flink.table.factories.FactoryUtil;
import org.apache.flink.table.factories.PlannerFactoryUtil;
import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.table.functions.TableAggregateFunction;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.table.functions.UserDefinedFunctionHelper;
import org.apache.flink.table.module.ModuleManager;
import org.apache.flink.table.operations.ExplainOperation;
import org.apache.flink.table.operations.JavaExternalQueryOperation;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.QueryOperation;
import org.apache.flink.table.operations.command.ResetOperation;
import org.apache.flink.table.operations.command.SetOperation;
import org.apache.flink.table.operations.utils.OperationTreeBuilder;
import org.apache.flink.table.planner.delegation.DefaultExecutor;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 定制TableEnvironmentImpl
 *
 * @author wenmo
 * @since 2021/10/22 10:02
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
        return create(executionEnvironment, EnvironmentSettings.newInstance().build(), TableConfig.getDefault());
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
        EnvironmentSettings settings,
        TableConfig tableConfig) {

        // temporary solution until FLINK-15635 is fixed
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        final ModuleManager moduleManager = new ModuleManager();

        final CatalogManager catalogManager =
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

        final FunctionCatalog functionCatalog =
            new FunctionCatalog(tableConfig, catalogManager, moduleManager);

        final Executor executor =
            lookupExecutor(classLoader, settings.getExecutor(), executionEnvironment);

        final Planner planner =
            PlannerFactoryUtil.createPlanner(
                settings.getPlanner(),
                executor,
                tableConfig,
                catalogManager,
                functionCatalog);

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
        ClassLoader classLoader,
        String executorIdentifier,
        StreamExecutionEnvironment executionEnvironment) {
        try {
            final ExecutorFactory executorFactory =
                FactoryUtil.discoverFactory(
                    classLoader, ExecutorFactory.class, executorIdentifier);
            final Method createMethod =
                executorFactory
                    .getClass()
                    .getMethod("create", StreamExecutionEnvironment.class);

            return (Executor) createMethod.invoke(executorFactory, executionEnvironment);
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
            List<Transformation<?>> trans = super.planner.translate(modifyOperations);
            if (execEnv instanceof DefaultExecutor) {
                StreamGraph streamGraph = ((DefaultExecutor) execEnv).getExecutionEnvironment().generateStreamGraph(trans);
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
        if (execEnv instanceof DefaultExecutor) {
            StreamGraph streamGraph = ((DefaultExecutor) execEnv).getExecutionEnvironment().generateStreamGraph(trans);
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

    @Override
    public Table fromChangelogStream(DataStream<Row> dataStream) {
        return fromStreamInternal(dataStream, null, null, ChangelogMode.all());
    }

    @Override
    public <T> void registerDataStream(String name, DataStream<T> dataStream) {
        createTemporaryView(name, dataStream);
    }

    @Override
    public <T> void createTemporaryView(String path, DataStream<T> dataStream) {
        createTemporaryView(
            path, fromStreamInternal(dataStream, null, path, ChangelogMode.insertOnly()));
    }

    private <T> Table fromStreamInternal(
        DataStream<T> dataStream,
        @Nullable Schema schema,
        @Nullable String viewPath,
        ChangelogMode changelogMode) {
        Preconditions.checkNotNull(dataStream, "Data stream must not be null.");
        Preconditions.checkNotNull(changelogMode, "Changelog mode must not be null.");

        if (dataStream.getExecutionEnvironment() != executionEnvironment) {
            throw new ValidationException(
                "The DataStream's StreamExecutionEnvironment must be identical to the one that "
                    + "has been passed to the StreamTableEnvironment during instantiation.");
        }

        final CatalogManager catalogManager = getCatalogManager();
        final SchemaResolver schemaResolver = catalogManager.getSchemaResolver();
        final OperationTreeBuilder operationTreeBuilder = getOperationTreeBuilder();

        final UnresolvedIdentifier unresolvedIdentifier;
        if (viewPath != null) {
            unresolvedIdentifier = getParser().parseIdentifier(viewPath);
        } else {
            unresolvedIdentifier =
                UnresolvedIdentifier.of("Unregistered_DataStream_Source_" + dataStream.getId());
        }
        final ObjectIdentifier objectIdentifier =
            catalogManager.qualifyIdentifier(unresolvedIdentifier);

        final SchemaTranslator.ConsumingResult schemaTranslationResult =
            SchemaTranslator.createConsumingResult(
                catalogManager.getDataTypeFactory(), dataStream.getType(), schema);

        final ResolvedSchema resolvedSchema =
            schemaTranslationResult.getSchema().resolve(schemaResolver);

        final QueryOperation scanOperation =
            new JavaExternalQueryOperation<>(
                objectIdentifier,
                dataStream,
                schemaTranslationResult.getPhysicalDataType(),
                schemaTranslationResult.isTopLevelRecord(),
                changelogMode,
                resolvedSchema);

        final List<String> projections = schemaTranslationResult.getProjections();
        if (projections == null) {
            return createTable(scanOperation);
        }

        final QueryOperation projectOperation =
            operationTreeBuilder.project(
                projections.stream()
                    .map(ApiExpressionUtils::unresolvedRef)
                    .collect(Collectors.toList()),
                scanOperation);

        return createTable(projectOperation);
    }

}
