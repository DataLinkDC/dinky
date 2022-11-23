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

import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.internal.TableEnvironmentInternal;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.delegation.Parser;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.utils.OperationTreeBuilder;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.table.sources.TableSource;

import java.util.List;

/**
 *
 */
public interface DefaultTableEnvironmentInternal extends TableEnvironmentInternal, TableEnvironmentInstance {

    default TableEnvironmentInternal getTableEnvironmentInternal() {
        return (TableEnvironmentInternal) getTableEnvironment();
    }

    @Override
    default Parser getParser() {
        return getTableEnvironmentInternal().getParser();
    }

    @Override
    default CatalogManager getCatalogManager() {
        return getTableEnvironmentInternal().getCatalogManager();
    }

    @Override
    default OperationTreeBuilder getOperationTreeBuilder() {
        return getTableEnvironmentInternal().getOperationTreeBuilder();
    }

    @Override
    default TableResult executeInternal(List<ModifyOperation> list) {
        return getTableEnvironmentInternal().executeInternal(list);
    }

    @Override
    default TableResult executeInternal(Operation operation) {
        return getTableEnvironmentInternal().executeInternal(operation);
    }

    @Override
    default String explainInternal(List<Operation> list, ExplainDetail... explainDetails) {
        return getTableEnvironmentInternal().explainInternal(list, explainDetails);
    }

    @Override
    default void registerTableSourceInternal(String s, TableSource<?> tableSource) {
        getTableEnvironmentInternal().registerTableSourceInternal(s, tableSource);
    }

    @Override
    default void registerTableSinkInternal(String s, TableSink<?> tableSink) {
        getTableEnvironmentInternal().registerTableSinkInternal(s, tableSink);
    }

    @Override
    default String getJsonPlan(String s) {
        return getTableEnvironmentInternal().getJsonPlan(s);
    }

    @Override
    default String getJsonPlan(List<ModifyOperation> list) {
        return getTableEnvironmentInternal().getJsonPlan(list);
    }

    @Override
    default String explainJsonPlan(String s, ExplainDetail... explainDetails) {
        return getTableEnvironmentInternal().explainJsonPlan(s, explainDetails);
    }

    @Override
    default TableResult executeJsonPlan(String s) {
        return getTableEnvironmentInternal().executeJsonPlan(s);
    }

}
