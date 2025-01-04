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

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.ExecutionOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.catalog.FunctionCatalog;
import org.apache.flink.table.catalog.GenericInMemoryCatalog;
import org.apache.flink.table.delegation.Executor;
import org.apache.flink.table.delegation.Planner;
import org.apache.flink.table.factories.PlannerFactoryUtil;
import org.apache.flink.table.module.ModuleManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PlannerTableEnvironmentImpl is used to load flink-table-planner_2.12 independently without using flink-table-planner-loader.
 *
 */
public class PlannerTableEnvironmentImpl extends CustomTableEnvironmentImpl {

    private static final Logger log = LoggerFactory.getLogger(PlannerTableEnvironmentImpl.class);

    public PlannerTableEnvironmentImpl(
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
                functionCatalog,
                tableConfig,
                executionEnvironment,
                planner,
                executor,
                isStreamingMode,
                userClassLoader);
    }

    public static PlannerTableEnvironmentImpl create(
            StreamExecutionEnvironment executionEnvironment, ClassLoader classLoader) {
        return create(
                executionEnvironment, EnvironmentSettings.newInstance().build(), TableConfig.getDefault(), classLoader);
    }

    public static PlannerTableEnvironmentImpl createBatch(
            StreamExecutionEnvironment executionEnvironment, ClassLoader classLoader) {
        Configuration configuration = new Configuration();
        configuration.set(ExecutionOptions.RUNTIME_MODE, RuntimeExecutionMode.BATCH);
        TableConfig tableConfig = new TableConfig();
        tableConfig.addConfiguration(configuration);
        return create(executionEnvironment, EnvironmentSettings.inBatchMode(), tableConfig, classLoader);
    }

    public static PlannerTableEnvironmentImpl create(
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

        return new PlannerTableEnvironmentImpl(
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
}
