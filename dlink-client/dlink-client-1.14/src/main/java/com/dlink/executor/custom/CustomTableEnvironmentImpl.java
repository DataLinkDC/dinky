package com.dlink.executor.custom;

import com.dlink.result.SqlExplainResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.JSONGenerator;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.catalog.FunctionCatalog;
import org.apache.flink.table.catalog.GenericInMemoryCatalog;
import org.apache.flink.table.delegation.Executor;
import org.apache.flink.table.delegation.ExecutorFactory;
import org.apache.flink.table.delegation.Planner;
import org.apache.flink.table.factories.FactoryUtil;
import org.apache.flink.table.factories.PlannerFactoryUtil;
import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.table.functions.TableAggregateFunction;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.table.functions.UserDefinedFunctionHelper;
import org.apache.flink.table.module.ModuleManager;
import org.apache.flink.table.operations.ExplainOperation;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.QueryOperation;
import org.apache.flink.table.planner.delegation.DefaultExecutor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 定制TableEnvironmentImpl
 *
 * @author wenmo
 * @since 2021/10/22 10:02
 **/
public class CustomTableEnvironmentImpl extends TableEnvironmentImpl {

    private SqlManager sqlManager;
    private boolean useSqlFragment = true;

    protected CustomTableEnvironmentImpl(CatalogManager catalogManager, SqlManager sqlManager, ModuleManager moduleManager, TableConfig tableConfig, Executor executor, FunctionCatalog functionCatalog, Planner planner, boolean isStreamingMode, ClassLoader userClassLoader) {
        super(catalogManager, moduleManager, tableConfig, executor, functionCatalog, planner, isStreamingMode, userClassLoader);
        this.sqlManager = sqlManager;
    }

    public CustomTableEnvironmentImpl(
            CatalogManager catalogManager,
            ModuleManager moduleManager,
            SqlManager sqlManager,
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
        this.sqlManager = sqlManager;
    }

    public static CustomTableEnvironmentImpl create(StreamExecutionEnvironment executionEnvironment){
        return create(executionEnvironment,EnvironmentSettings.newInstance().build(),TableConfig.getDefault());
    }

    public static CustomTableEnvironmentImpl create(
            StreamExecutionEnvironment executionEnvironment,
            EnvironmentSettings settings,
            TableConfig tableConfig) {

        // temporary solution until FLINK-15635 is fixed
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        final ModuleManager moduleManager = new ModuleManager();

        final SqlManager sqlManager = new SqlManager();

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
                sqlManager,
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

    public void useSqlFragment() {
        this.useSqlFragment = true;
    }

    public void unUseSqlFragment() {
        this.useSqlFragment = false;
    }

    @Override
    public String explainSql(String statement, ExplainDetail... extraDetails) {
        if(useSqlFragment) {
            statement = sqlManager.parseVariable(statement);
            if (statement.length() == 0) {
                return "This is a sql fragment.";
            }
        }
        if (checkShowFragments(statement)) {
            return "'SHOW FRAGMENTS' can't be explained.";
        } else {
            return super.explainSql(statement, extraDetails);
        }
    }

    public ObjectNode getStreamGraph(String statement) {
        if(useSqlFragment) {
            statement = sqlManager.parseVariable(statement);
            if (statement.length() == 0) {
                throw new TableException("This is a sql fragment.");
            }
        }
        if (checkShowFragments(statement)) {
            throw new TableException("'SHOW FRAGMENTS' can't be explained.");
        }
        List<Operation> operations = super.getParser().parse(statement);
        if (operations.size() != 1) {
            throw new TableException("Unsupported SQL query! explainSql() only accepts a single SQL query.");
        } else {
            List<ModifyOperation> modifyOperations = new ArrayList<>();
            for (int i = 0; i < operations.size(); i++) {
                if(operations.get(i) instanceof ModifyOperation){
                    modifyOperations.add((ModifyOperation)operations.get(i));
                }
            }
            List<Transformation<?>> trans = super.planner.translate(modifyOperations);
            if(execEnv instanceof DefaultExecutor){
                StreamGraph streamGraph = ((DefaultExecutor) execEnv).getExecutionEnvironment().generateStreamGraph(trans);
                JSONGenerator jsonGenerator = new JSONGenerator(streamGraph);
                String json = jsonGenerator.getJSON();
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode objectNode =mapper.createObjectNode();
                try {
                    objectNode = (ObjectNode) mapper.readTree(json);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }finally {
                    return objectNode;
                }
            }else{
                throw new TableException("Unsupported SQL query! explainSql() need a single SQL to query.");
            }
        }
    }

    public SqlExplainResult explainSqlRecord(String statement, ExplainDetail... extraDetails) {
        SqlExplainResult record = new SqlExplainResult();
        if(useSqlFragment) {
            String orignSql = statement;
            statement = sqlManager.parseVariable(statement);
            if (statement.length() == 0) {
                record.setParseTrue(true);
                record.setType("Sql Fragment");
                record.setExplain(orignSql);
                record.setExplainTrue(true);
                return record;
            }
        }
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
                i=i-1;
            }
        }
        record.setExplainTrue(true);
        if(operationlist.size()==0){
            //record.setExplain("DDL语句不进行解释。");
            return record;
        }
        record.setExplain(planner.explain(operationlist, extraDetails));
        return record;
    }

    @Override
    public String[] getCompletionHints(String statement, int position) {
        if(useSqlFragment) {
            statement = sqlManager.parseVariable(statement);
            if (statement.length() == 0) {
                return new String[0];
            }
        }
        return super.getCompletionHints(statement, position);
    }

    @Override
    public Table sqlQuery(String query) {
        if(useSqlFragment) {
            query = sqlManager.parseVariable(query);
            if (query.length() == 0) {
                throw new TableException("Unsupported SQL query! The SQL query parsed is null.If it's a sql fragment, and please use executeSql().");
            }
            if (checkShowFragments(query)) {
                return sqlManager.getSqlFragmentsTable(this);
            } else {
                return super.sqlQuery(query);
            }
        }else {
            return super.sqlQuery(query);
        }
    }

    @Override
    public TableResult executeSql(String statement) {
        if(useSqlFragment) {
            statement = sqlManager.parseVariable(statement);
            if (statement.length() == 0) {
                return CustomTableResultImpl.TABLE_RESULT_OK;
            }
            if (checkShowFragments(statement)) {
                return sqlManager.getSqlFragments();
            } else {
                return super.executeSql(statement);
            }
        }else{
            return super.executeSql(statement);
        }
    }

    @Override
    public void sqlUpdate(String stmt) {
        if(useSqlFragment) {
            stmt = sqlManager.parseVariable(stmt);
            if (stmt.length() == 0) {
                throw new TableException("Unsupported SQL update! The SQL update parsed is null.If it's a sql fragment, and please use executeSql().");
            }
        }
        super.sqlUpdate(stmt);
    }

    public boolean checkShowFragments(String sql){
        return sqlManager.checkShowFragments(sql);
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
}
