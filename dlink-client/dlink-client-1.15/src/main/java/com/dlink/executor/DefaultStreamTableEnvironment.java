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

import com.dlink.executor.TableEnvironmentInstance;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.CompiledPlan;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.PlanReference;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamStatementSet;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.Catalog;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.expressions.Expression;
import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.table.functions.TableAggregateFunction;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.table.functions.UserDefinedFunction;
import org.apache.flink.table.module.Module;
import org.apache.flink.table.module.ModuleEntry;
import org.apache.flink.table.types.AbstractDataType;
import org.apache.flink.types.Row;

import java.util.Optional;

/**
 *
 */
public interface DefaultStreamTableEnvironment extends StreamTableEnvironment, TableEnvironmentInstance {

    default StreamTableEnvironment getStreamTableEnvironment() {
        return (StreamTableEnvironment) getTableEnvironment();
    }

    @Override// region StreamTableEnvironment interface
    default <T> void registerFunction(String s, TableFunction<T> tableFunction) {
        getStreamTableEnvironment().registerFunction(s, tableFunction);
    }

    @Override
    default <T, A> void registerFunction(String s, AggregateFunction<T, A> aggregateFunction) {
        getStreamTableEnvironment().registerFunction(s, aggregateFunction);
    }

    @Override
    default <T, A> void registerFunction(String s, TableAggregateFunction<T, A> tableAggregateFunction) {
        getStreamTableEnvironment().registerFunction(s, tableAggregateFunction);
    }

    @Override
    default <T> Table fromDataStream(DataStream<T> dataStream) {
        return getStreamTableEnvironment().fromDataStream(dataStream);
    }

    @Override
    default <T> Table fromDataStream(DataStream<T> dataStream, Schema schema) {
        return getStreamTableEnvironment().fromDataStream(dataStream, schema);
    }

    @Override
    default Table fromChangelogStream(DataStream<Row> dataStream) {
        return getStreamTableEnvironment().fromChangelogStream(dataStream);
    }

    @Override
    default Table fromChangelogStream(DataStream<Row> dataStream, Schema schema) {
        return getStreamTableEnvironment().fromChangelogStream(dataStream, schema);
    }

    @Override
    default Table fromChangelogStream(DataStream<Row> dataStream, Schema schema, ChangelogMode changelogMode) {
        return getStreamTableEnvironment().fromChangelogStream(dataStream, schema, changelogMode);
    }

    @Override
    default <T> void createTemporaryView(String s, DataStream<T> dataStream) {
        getStreamTableEnvironment().createTemporaryView(s, dataStream);
    }

    @Override
    default <T> void createTemporaryView(String s, DataStream<T> dataStream, Schema schema) {
        getStreamTableEnvironment().createTemporaryView(s, dataStream, schema);
    }

    @Override
    default DataStream<Row> toDataStream(Table table) {
        return getStreamTableEnvironment().toDataStream(table);
    }

    @Override
    default <T> DataStream<T> toDataStream(Table table, Class<T> aClass) {
        return getStreamTableEnvironment().toDataStream(table, aClass);
    }

    @Override
    default <T> DataStream<T> toDataStream(Table table, AbstractDataType<?> abstractDataType) {
        return getStreamTableEnvironment().toDataStream(table, abstractDataType);
    }

    @Override
    default DataStream<Row> toChangelogStream(Table table) {
        return getStreamTableEnvironment().toChangelogStream(table);
    }

    @Override
    default DataStream<Row> toChangelogStream(Table table, Schema schema) {
        return getStreamTableEnvironment().toChangelogStream(table, schema);
    }

    @Override
    default DataStream<Row> toChangelogStream(Table table, Schema schema, ChangelogMode changelogMode) {
        return getStreamTableEnvironment().toChangelogStream(table, schema, changelogMode);
    }

    @Override
    default Table fromValues(Expression... expressions) {
        return getStreamTableEnvironment().fromValues(expressions);
    }

    @Override
    default Table fromValues(AbstractDataType<?> abstractDataType, Expression... expressions) {
        return getStreamTableEnvironment().fromValues(abstractDataType, expressions);
    }

    @Override
    default Table fromValues(Iterable<?> iterable) {
        return getStreamTableEnvironment().fromValues(iterable);
    }

    @Override
    default Table fromValues(AbstractDataType<?> abstractDataType, Iterable<?> iterable) {
        return getStreamTableEnvironment().fromValues(abstractDataType, iterable);
    }

    @Override
    default void registerCatalog(String s, Catalog catalog) {
        getStreamTableEnvironment().registerCatalog(s, catalog);
    }

    @Override
    default Optional<Catalog> getCatalog(String s) {
        return getStreamTableEnvironment().getCatalog(s);
    }

    @Override
    default void loadModule(String s, Module module) {
        getStreamTableEnvironment().loadModule(s, module);
    }

    @Override
    default void useModules(String... strings) {
        getStreamTableEnvironment().useModules(strings);
    }

    @Override
    default void unloadModule(String s) {
        getStreamTableEnvironment().unloadModule(s);
    }

    @Override
    default void registerFunction(String s, ScalarFunction scalarFunction) {
        getStreamTableEnvironment().registerFunction(s, scalarFunction);
    }

    @Override
    default void createTemporarySystemFunction(String s, Class<? extends UserDefinedFunction> aClass) {
        getStreamTableEnvironment().createTemporarySystemFunction(s, aClass);
    }

    @Override
    default void createTemporarySystemFunction(String s, UserDefinedFunction userDefinedFunction) {
        getStreamTableEnvironment().createTemporarySystemFunction(s, userDefinedFunction);
    }

    @Override
    default boolean dropTemporarySystemFunction(String s) {
        return getStreamTableEnvironment().dropTemporarySystemFunction(s);
    }

    @Override
    default void createFunction(String s, Class<? extends UserDefinedFunction> aClass) {
        getStreamTableEnvironment().createFunction(s, aClass);
    }

    @Override
    default void createFunction(String s, Class<? extends UserDefinedFunction> aClass, boolean b) {
        getStreamTableEnvironment().createFunction(s, aClass, b);
    }

    @Override
    default boolean dropFunction(String s) {
        return getStreamTableEnvironment().dropFunction(s);
    }

    @Override
    default void createTemporaryFunction(String s, Class<? extends UserDefinedFunction> aClass) {
        getStreamTableEnvironment().createTemporaryFunction(s, aClass);
    }

    @Override
    default void createTemporaryFunction(String s, UserDefinedFunction userDefinedFunction) {
        getStreamTableEnvironment().createTemporaryFunction(s, userDefinedFunction);
    }

    @Override
    default boolean dropTemporaryFunction(String s) {
        return getStreamTableEnvironment().dropTemporaryFunction(s);
    }

    @Override
    default void createTemporaryTable(String s, TableDescriptor tableDescriptor) {
        getStreamTableEnvironment().createTemporaryTable(s, tableDescriptor);
    }

    @Override
    default void createTable(String s, TableDescriptor tableDescriptor) {
        getStreamTableEnvironment().createTable(s, tableDescriptor);
    }

    @Override
    default void registerTable(String s, Table table) {
        getStreamTableEnvironment().registerTable(s, table);
    }

    @Override
    default void createTemporaryView(String s, Table table) {
        getStreamTableEnvironment().createTemporaryView(s, table);
    }

    @Override
    default Table scan(String... strings) {
        return getStreamTableEnvironment().scan(strings);
    }

    @Override
    default Table from(String s) {
        return getStreamTableEnvironment().from(s);
    }

    @Override
    default Table from(TableDescriptor tableDescriptor) {
        return getStreamTableEnvironment().from(tableDescriptor);
    }

    @Override
    default String[] listCatalogs() {
        return getStreamTableEnvironment().listCatalogs();
    }

    @Override
    default String[] listModules() {
        return getStreamTableEnvironment().listModules();
    }

    @Override
    default ModuleEntry[] listFullModules() {
        return getStreamTableEnvironment().listFullModules();
    }

    @Override
    default String[] listDatabases() {
        return getStreamTableEnvironment().listDatabases();
    }

    @Override
    default String[] listTables() {
        return getStreamTableEnvironment().listTables();
    }

    @Override
    default String[] listTables(String s, String s1) {
        return getStreamTableEnvironment().listTables();
    }

    @Override
    default String[] listViews() {
        return getStreamTableEnvironment().listViews();
    }

    @Override
    default String[] listTemporaryTables() {
        return getStreamTableEnvironment().listTemporaryTables();
    }

    @Override
    default String[] listTemporaryViews() {
        return getStreamTableEnvironment().listTemporaryViews();
    }

    @Override
    default String[] listUserDefinedFunctions() {
        return getStreamTableEnvironment().listUserDefinedFunctions();
    }

    @Override
    default String[] listFunctions() {
        return getStreamTableEnvironment().listFunctions();
    }

    @Override
    default boolean dropTemporaryTable(String s) {
        return getStreamTableEnvironment().dropTemporaryTable(s);
    }

    @Override
    default boolean dropTemporaryView(String s) {
        return getStreamTableEnvironment().dropTemporaryView(s);
    }

    @Override
    default String explainSql(String s, ExplainDetail... explainDetails) {
        return getStreamTableEnvironment().explainSql(s);
    }

    @Override
    default String[] getCompletionHints(String s, int i) {
        return getStreamTableEnvironment().getCompletionHints(s, i);
    }

    @Override
    default Table sqlQuery(String s) {
        return getStreamTableEnvironment().sqlQuery(s);
    }

    @Override
    default TableResult executeSql(String s) {
        return getStreamTableEnvironment().executeSql(s);
    }

    @Override
    default String getCurrentCatalog() {
        return getStreamTableEnvironment().getCurrentCatalog();
    }

    @Override
    default void useCatalog(String s) {
        getStreamTableEnvironment().useCatalog(s);
    }

    @Override
    default String getCurrentDatabase() {
        return getStreamTableEnvironment().getCurrentDatabase();
    }

    @Override
    default void useDatabase(String s) {
        getStreamTableEnvironment().useDatabase(s);
    }

    @Override
    default TableConfig getConfig() {
        return getStreamTableEnvironment().getConfig();
    }

    @Override
    default StreamStatementSet createStatementSet() {
        return getStreamTableEnvironment().createStatementSet();
    }

    @Override
    default CompiledPlan loadPlan(PlanReference planReference) throws TableException {
        return getStreamTableEnvironment().loadPlan(planReference);
    }

    @Override
    default CompiledPlan compilePlanSql(String s) throws TableException {
        return getStreamTableEnvironment().compilePlanSql(s);
    }

    @Override
    default <T> Table fromDataStream(DataStream<T> dataStream, String s) {
        return getStreamTableEnvironment().fromDataStream(dataStream, s);
    }

    @Override
    default <T> Table fromDataStream(DataStream<T> dataStream, Expression... expressions) {
        return getStreamTableEnvironment().fromDataStream(dataStream, expressions);
    }

    @Override
    default <T> void registerDataStream(String s, DataStream<T> dataStream) {
        getStreamTableEnvironment().registerDataStream(s, dataStream);
    }

    @Override
    default <T> void registerDataStream(String s, DataStream<T> dataStream, String s1) {
        getStreamTableEnvironment().registerDataStream(s, dataStream, s1);
    }

    @Override
    default <T> void createTemporaryView(String s, DataStream<T> dataStream, String s1) {
        getStreamTableEnvironment().createTemporaryView(s, dataStream, s1);
    }

    @Override
    default <T> void createTemporaryView(String s, DataStream<T> dataStream, Expression... expressions) {
        getStreamTableEnvironment().createTemporaryView(s, dataStream, expressions);
    }

    @Override
    default <T> DataStream<T> toAppendStream(Table table, Class<T> aClass) {
        return getStreamTableEnvironment().toAppendStream(table, aClass);
    }

    @Override
    default <T> DataStream<T> toAppendStream(Table table, TypeInformation<T> typeInformation) {
        return getStreamTableEnvironment().toAppendStream(table, typeInformation);
    }

    @Override
    default <T> DataStream<Tuple2<Boolean, T>> toRetractStream(Table table, Class<T> aClass) {
        return getStreamTableEnvironment().toRetractStream(table, aClass);
    }

    @Override
    default <T> DataStream<Tuple2<Boolean, T>> toRetractStream(Table table, TypeInformation<T> typeInformation) {
        return getStreamTableEnvironment().toRetractStream(table, typeInformation);
    }
    // endregion

}
