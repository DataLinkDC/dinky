package com.dlink.executor;

import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.sources.TableSource;

/**
 *
 */
public interface DefaultTableEnvironment extends TableEnvironment, TableEnvironmentInstance {
    @Override
    default Table fromTableSource(TableSource<?> tableSource) {
        return getTableEnvironment().fromTableSource(tableSource);
    }

    @Override
    default void insertInto(Table table, String s, String... strings) {
        getTableEnvironment().insertInto(table, s, strings);
    }

    @Override
    default void insertInto(String s, Table table) {
        getTableEnvironment().insertInto(s, table);
    }

    @Override
    default String explain(Table table) {
        return getTableEnvironment().explain(table);
    }

    @Override
    default String explain(Table table, boolean b) {
        return getTableEnvironment().explain(table, b);
    }

    @Override
    default String explain(boolean b) {
        return getTableEnvironment().explain(b);
    }

    @Override
    default void sqlUpdate(String s) {
        getTableEnvironment().sqlUpdate(s);
    }
}
