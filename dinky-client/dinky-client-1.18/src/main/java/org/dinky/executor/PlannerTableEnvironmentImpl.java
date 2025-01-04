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

import static org.apache.flink.configuration.ExecutionOptions.RUNTIME_MODE;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.configuration.ExecutionOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.bridge.java.internal.StreamTableEnvironmentImpl;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.catalog.CatalogStore;
import org.apache.flink.table.catalog.CatalogStoreHolder;
import org.apache.flink.table.catalog.FunctionCatalog;
import org.apache.flink.table.catalog.GenericInMemoryCatalog;
import org.apache.flink.table.delegation.Executor;
import org.apache.flink.table.delegation.Planner;
import org.apache.flink.table.factories.CatalogStoreFactory;
import org.apache.flink.table.factories.TableFactoryUtil;
import org.apache.flink.table.module.ModuleManager;
import org.apache.flink.table.planner.delegation.BatchPlanner;
import org.apache.flink.table.planner.delegation.DefaultExecutor;
import org.apache.flink.table.planner.delegation.StreamPlanner;
import org.apache.flink.table.resource.ResourceManager;
import org.apache.flink.util.FlinkUserCodeClassLoaders;
import org.apache.flink.util.MutableURLClassLoader;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PlannerTableEnvironmentImpl is used to load flink-table-planner_2.12 independently without using flink-table-planner-loader.
 *
 */
public class PlannerTableEnvironmentImpl extends CustomTableEnvironmentImpl {

    private static final Logger log = LoggerFactory.getLogger(PlannerTableEnvironmentImpl.class);

    public PlannerTableEnvironmentImpl(StreamTableEnvironment streamTableEnvironment) {
        super(streamTableEnvironment);
    }

    public static PlannerTableEnvironmentImpl create(
            StreamExecutionEnvironment executionEnvironment, ClassLoader classLoader) {
        return createWithPlanner(
                executionEnvironment,
                EnvironmentSettings.newInstance().withClassLoader(classLoader).build());
    }

    public static PlannerTableEnvironmentImpl createBatch(
            StreamExecutionEnvironment executionEnvironment, ClassLoader classLoader) {
        return createWithPlanner(
                executionEnvironment,
                EnvironmentSettings.newInstance()
                        .withClassLoader(classLoader)
                        .inBatchMode()
                        .build());
    }

    public static PlannerTableEnvironmentImpl createWithPlanner(
            StreamExecutionEnvironment executionEnvironment, EnvironmentSettings settings) {
        final MutableURLClassLoader userClassLoader = FlinkUserCodeClassLoaders.create(
                new URL[0], settings.getUserClassLoader(), settings.getConfiguration());
        final Executor executor = new DefaultExecutor(executionEnvironment);
        final TableConfig tableConfig = TableConfig.getDefault();
        tableConfig.setRootConfiguration(executor.getConfiguration());
        tableConfig.addConfiguration(settings.getConfiguration());

        final ResourceManager resourceManager = new ResourceManager(settings.getConfiguration(), userClassLoader);
        final ModuleManager moduleManager = new ModuleManager();

        final CatalogStoreFactory catalogStoreFactory =
                TableFactoryUtil.findAndCreateCatalogStoreFactory(settings.getConfiguration(), userClassLoader);
        final CatalogStoreFactory.Context catalogStoreFactoryContext =
                TableFactoryUtil.buildCatalogStoreFactoryContext(settings.getConfiguration(), userClassLoader);
        catalogStoreFactory.open(catalogStoreFactoryContext);
        final CatalogStore catalogStore = settings.getCatalogStore() != null
                ? settings.getCatalogStore()
                : catalogStoreFactory.createCatalogStore();

        final CatalogManager catalogManager = CatalogManager.newBuilder()
                .classLoader(userClassLoader)
                .config(tableConfig)
                .defaultCatalog(
                        settings.getBuiltInCatalogName(),
                        new GenericInMemoryCatalog(settings.getBuiltInCatalogName(), settings.getBuiltInDatabaseName()))
                .executionConfig(executionEnvironment.getConfig())
                .catalogModificationListeners(TableFactoryUtil.findCatalogModificationListenerList(
                        settings.getConfiguration(), userClassLoader))
                .catalogStoreHolder(CatalogStoreHolder.newBuilder()
                        .classloader(userClassLoader)
                        .config(tableConfig)
                        .catalogStore(catalogStore)
                        .factory(catalogStoreFactory)
                        .build())
                .build();

        final FunctionCatalog functionCatalog =
                new FunctionCatalog(tableConfig, resourceManager, catalogManager, moduleManager);

        Planner planner = null;
        final RuntimeExecutionMode runtimeExecutionMode = tableConfig.get(ExecutionOptions.RUNTIME_MODE);
        switch (runtimeExecutionMode) {
            case STREAMING:
                planner = new StreamPlanner(
                        executor, tableConfig, moduleManager, functionCatalog, catalogManager, userClassLoader);
                break;
            case BATCH:
                planner = new BatchPlanner(
                        executor, tableConfig, moduleManager, functionCatalog, catalogManager, userClassLoader);
                break;
            default:
                throw new TableException(String.format(
                        "Unsupported mode '%s' for '%s'. Only an explicit BATCH or "
                                + "STREAMING mode is supported in Table API.",
                        runtimeExecutionMode, RUNTIME_MODE.key()));
        }

        return new PlannerTableEnvironmentImpl(new StreamTableEnvironmentImpl(
                catalogManager,
                moduleManager,
                resourceManager,
                functionCatalog,
                tableConfig,
                executionEnvironment,
                planner,
                executor,
                settings.isStreamingMode()));
    }
}
