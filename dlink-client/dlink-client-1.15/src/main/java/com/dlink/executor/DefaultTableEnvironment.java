package com.dlink.executor;

import org.apache.flink.table.api.CompiledPlan;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.PlanReference;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.catalog.Catalog;
import org.apache.flink.table.expressions.Expression;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.table.functions.UserDefinedFunction;
import org.apache.flink.table.module.Module;
import org.apache.flink.table.module.ModuleEntry;
import org.apache.flink.table.types.AbstractDataType;

import java.util.Optional;

/**
 *
 */
public interface DefaultTableEnvironment extends TableEnvironment, TableEnvironmentInstance {
    @Override
    default Table fromValues(org.apache.flink.table.expressions.Expression... expressions) {
        return getTableEnvironment().fromValues(expressions);
    }

    @Override
    default Table fromValues(AbstractDataType<?> abstractDataType, Expression... expressions) {
        return getTableEnvironment().fromValues(abstractDataType, expressions);
    }

    @Override
    default Table fromValues(Iterable<?> iterable) {
        return getTableEnvironment().fromValues(iterable);
    }

    @Override
    default Table fromValues(AbstractDataType<?> abstractDataType, Iterable<?> iterable) {
        return getTableEnvironment().fromValues(abstractDataType, iterable);
    }

    @Override
    default void registerCatalog(String s, Catalog catalog) {
        getTableEnvironment().registerCatalog(s, catalog);
    }

    @Override
    default Optional<Catalog> getCatalog(String s) {
        return getTableEnvironment().getCatalog(s);
    }

    @Override
    default void loadModule(String s, Module module) {
        getTableEnvironment().loadModule(s, module);
    }

    @Override
    default void useModules(String... strings) {
        getTableEnvironment().useModules(strings);
    }

    @Override
    default void unloadModule(String s) {
        getTableEnvironment().unloadModule(s);
    }

    @Override
    default void registerFunction(String s, ScalarFunction scalarFunction) {
        getTableEnvironment().registerFunction(s, scalarFunction);
    }

    @Override
    default void createTemporarySystemFunction(String s, Class<? extends UserDefinedFunction> aClass) {
        getTableEnvironment().createTemporarySystemFunction(s, aClass);
    }

    @Override
    default void createTemporarySystemFunction(String s, UserDefinedFunction userDefinedFunction) {
        getTableEnvironment().createTemporarySystemFunction(s, userDefinedFunction);
    }

    @Override
    default boolean dropTemporarySystemFunction(String s) {
        return getTableEnvironment().dropTemporarySystemFunction(s);
    }

    @Override
    default void createFunction(String s, Class<? extends UserDefinedFunction> aClass) {
        getTableEnvironment().createFunction(s, aClass);
    }

    @Override
    default void createFunction(String s, Class<? extends UserDefinedFunction> aClass, boolean b) {
        getTableEnvironment().createFunction(s, aClass, b);
    }

    @Override
    default boolean dropFunction(String s) {
        return getTableEnvironment().dropFunction(s);
    }

    @Override
    default void createTemporaryFunction(String s, Class<? extends UserDefinedFunction> aClass) {
        getTableEnvironment().createTemporaryFunction(s, aClass);
    }

    @Override
    default void createTemporaryFunction(String s, UserDefinedFunction userDefinedFunction) {
        getTableEnvironment().createTemporaryFunction(s, userDefinedFunction);
    }

    @Override
    default boolean dropTemporaryFunction(String s) {
        return getTableEnvironment().dropTemporaryFunction(s);
    }

    @Override
    default void createTemporaryTable(String s, TableDescriptor tableDescriptor) {
        getTableEnvironment().createTemporaryTable(s, tableDescriptor);
    }

    @Override
    default void createTable(String s, TableDescriptor tableDescriptor) {
        getTableEnvironment().createTable(s, tableDescriptor);
    }

    @Override
    default void registerTable(String s, Table table) {
        getTableEnvironment().registerTable(s, table);
    }

    @Override
    default void createTemporaryView(String s, Table table) {
        getTableEnvironment().createTemporaryView(s, table);
    }

    @Override
    default Table scan(String... strings) {
        return getTableEnvironment().scan(strings);
    }

    @Override
    default Table from(String s) {
        return getTableEnvironment().from(s);
    }

    @Override
    default Table from(TableDescriptor tableDescriptor) {
        return getTableEnvironment().from(tableDescriptor);
    }

    @Override
    default String[] listCatalogs() {
        return getTableEnvironment().listCatalogs();
    }

    @Override
    default String[] listModules() {
        return getTableEnvironment().listModules();
    }

    @Override
    default ModuleEntry[] listFullModules() {
        return getTableEnvironment().listFullModules();
    }

    @Override
    default String[] listDatabases() {
        return getTableEnvironment().listDatabases();
    }

    @Override
    default String[] listTables() {
        return getTableEnvironment().listTables();
    }

    @Override
    default String[] listTables(String s, String s1) {
        return getTableEnvironment().listTables();
    }

    @Override
    default String[] listViews() {
        return getTableEnvironment().listViews();
    }

    @Override
    default String[] listTemporaryTables() {
        return getTableEnvironment().listTemporaryTables();
    }

    @Override
    default String[] listTemporaryViews() {
        return getTableEnvironment().listTemporaryViews();
    }

    @Override
    default String[] listUserDefinedFunctions() {
        return getTableEnvironment().listUserDefinedFunctions();
    }

    @Override
    default String[] listFunctions() {
        return getTableEnvironment().listFunctions();
    }

    @Override
    default boolean dropTemporaryTable(String s) {
        return getTableEnvironment().dropTemporaryTable(s);
    }

    @Override
    default boolean dropTemporaryView(String s) {
        return getTableEnvironment().dropTemporaryView(s);
    }

    @Override
    default String explainSql(String s, ExplainDetail... explainDetails) {
        return getTableEnvironment().explainSql(s);
    }

    @Override
    default String[] getCompletionHints(String s, int i) {
        return getTableEnvironment().getCompletionHints(s, i);
    }

    @Override
    default Table sqlQuery(String s) {
        return getTableEnvironment().sqlQuery(s);
    }

    @Override
    default TableResult executeSql(String s) {
        return getTableEnvironment().executeSql(s);
    }

    @Override
    default String getCurrentCatalog() {
        return getTableEnvironment().getCurrentCatalog();
    }

    @Override
    default void useCatalog(String s) {
        getTableEnvironment().useCatalog(s);
    }

    @Override
    default String getCurrentDatabase() {
        return getTableEnvironment().getCurrentDatabase();
    }

    @Override
    default void useDatabase(String s) {
        getTableEnvironment().useDatabase(s);
    }

    @Override
    default TableConfig getConfig() {
        return getTableEnvironment().getConfig();
    }

    @Override
    default StatementSet createStatementSet() {
        return getTableEnvironment().createStatementSet();
    }

    @Override
    default CompiledPlan loadPlan(PlanReference planReference) throws TableException {
        return getTableEnvironment().loadPlan(planReference);
    }

    @Override
    default CompiledPlan compilePlanSql(String s) throws TableException {
        return getTableEnvironment().compilePlanSql(s);
    }
}
