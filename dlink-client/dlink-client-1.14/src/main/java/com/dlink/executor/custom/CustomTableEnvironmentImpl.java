package com.dlink.executor.custom;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.internal.DlinkTableEnvironmentImpl;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.catalog.FunctionCatalog;
import org.apache.flink.table.catalog.GenericInMemoryCatalog;
import org.apache.flink.table.delegation.Executor;
import org.apache.flink.table.delegation.ExecutorFactory;
import org.apache.flink.table.delegation.Planner;
import org.apache.flink.table.factories.FactoryUtil;
import org.apache.flink.table.factories.PlannerFactoryUtil;
import org.apache.flink.table.module.ModuleManager;

/**
 * 定制TableEnvironmentImpl
 *
 * @author wenmo
 * @since 2021/10/22 10:02
 **/
public class CustomTableEnvironmentImpl extends DlinkTableEnvironmentImpl {

    protected CustomTableEnvironmentImpl(CatalogManager catalogManager, SqlManager sqlManager, ModuleManager moduleManager, TableConfig tableConfig, Executor executor, FunctionCatalog functionCatalog, Planner planner, boolean isStreamingMode, ClassLoader userClassLoader) {
        super(catalogManager,sqlManager, moduleManager, tableConfig, executor, functionCatalog, planner, isStreamingMode, userClassLoader);
    }

    public static CustomTableEnvironmentImpl create(StreamExecutionEnvironment executionEnvironment) {
        return create(executionEnvironment, EnvironmentSettings.newInstance().build());
    }

    public static CustomTableEnvironmentImpl create(StreamExecutionEnvironment executionEnvironment, EnvironmentSettings settings) {
        return create(settings, settings.toConfiguration());
    }

    public static CustomTableEnvironmentImpl create(Configuration configuration) {
        return create(EnvironmentSettings.fromConfiguration(configuration), configuration);
    }

    public static CustomTableEnvironmentImpl create(EnvironmentSettings settings) {
        return create(settings, settings.toConfiguration());
    }

    private static CustomTableEnvironmentImpl create(
            EnvironmentSettings settings, Configuration configuration) {
        // temporary solution until FLINK-15635 is fixed
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        // use configuration to init table config
        final TableConfig tableConfig = new TableConfig();
        tableConfig.addConfiguration(configuration);

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
                        .build();

        final FunctionCatalog functionCatalog =
                new FunctionCatalog(tableConfig, catalogManager, moduleManager);

        final ExecutorFactory executorFactory =
                FactoryUtil.discoverFactory(
                        classLoader, ExecutorFactory.class, settings.getExecutor());
        final Executor executor = executorFactory.create(configuration);

        final Planner planner =
                PlannerFactoryUtil.createPlanner(
                        settings.getPlanner(),
                        executor,
                        tableConfig,
                        catalogManager,
                        functionCatalog);

        return new CustomTableEnvironmentImpl(
                catalogManager,
                sqlManager,
                moduleManager,
                tableConfig,
                executor,
                functionCatalog,
                planner,
                settings.isStreamingMode(),
                classLoader);
    }
}
