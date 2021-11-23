package com.dlink.executor.custom;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.catalog.FunctionCatalog;
import org.apache.flink.table.catalog.GenericInMemoryCatalog;
import org.apache.flink.table.delegation.Executor;
import org.apache.flink.table.delegation.ExecutorFactory;
import org.apache.flink.table.delegation.Planner;
import org.apache.flink.table.delegation.PlannerFactory;
import org.apache.flink.table.factories.ComponentFactoryService;
import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.table.functions.TableAggregateFunction;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.table.functions.UserDefinedFunctionHelper;
import org.apache.flink.table.module.ModuleManager;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 定制TableEnvironmentImpl
 *
 * @author wenmo
 * @since 2021/6/7 22:06
 **/
public class CustomTableEnvironmentImpl extends TableEnvironmentImpl {

    protected CustomTableEnvironmentImpl(CatalogManager catalogManager, ModuleManager moduleManager, TableConfig tableConfig, Executor executor, FunctionCatalog functionCatalog, Planner planner, boolean isStreamingMode, ClassLoader userClassLoader) {
        super(catalogManager, moduleManager, tableConfig, executor, functionCatalog, planner, isStreamingMode, userClassLoader);
    }

    public static CustomTableEnvironmentImpl create(StreamExecutionEnvironment executionEnvironment) {
        return create(executionEnvironment, EnvironmentSettings.newInstance().build());
    }

    static CustomTableEnvironmentImpl create(StreamExecutionEnvironment executionEnvironment, EnvironmentSettings settings) {
        return create(executionEnvironment, settings, new TableConfig());
    }

    public static CustomTableEnvironmentImpl create(StreamExecutionEnvironment executionEnvironment, EnvironmentSettings settings, TableConfig tableConfig) {
        if (!settings.isStreamingMode()) {
            throw new TableException("StreamTableEnvironment can not run in batch mode for now, please use TableEnvironment.");
        } else {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            ModuleManager moduleManager = new ModuleManager();
            CatalogManager catalogManager = CatalogManager.newBuilder().classLoader(classLoader).config(tableConfig.getConfiguration()).defaultCatalog(settings.getBuiltInCatalogName(), new GenericInMemoryCatalog(settings.getBuiltInCatalogName(), settings.getBuiltInDatabaseName())).executionConfig(executionEnvironment.getConfig()).build();
            FunctionCatalog functionCatalog = new FunctionCatalog(tableConfig, catalogManager, moduleManager);
            Map<String, String> executorProperties = settings.toExecutorProperties();
            Executor executor = lookupExecutor(executorProperties, executionEnvironment);
            Map<String, String> plannerProperties = settings.toPlannerProperties();
            Planner planner = (ComponentFactoryService.find(PlannerFactory.class, plannerProperties)).create(plannerProperties, executor, tableConfig, functionCatalog, catalogManager);
            return new CustomTableEnvironmentImpl(catalogManager, moduleManager, tableConfig, executor, functionCatalog, planner, settings.isStreamingMode(), classLoader);
        }
    }

    private static Executor lookupExecutor(Map<String, String> executorProperties, StreamExecutionEnvironment executionEnvironment) {
        try {
            ExecutorFactory executorFactory = ComponentFactoryService.find(ExecutorFactory.class, executorProperties);
            Method createMethod = executorFactory.getClass().getMethod("create", Map.class, StreamExecutionEnvironment.class);
            return (Executor) createMethod.invoke(executorFactory, executorProperties, executionEnvironment);
        } catch (Exception var4) {
            throw new TableException("Could not instantiate the executor. Make sure a planner module is on the classpath", var4);
        }
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
