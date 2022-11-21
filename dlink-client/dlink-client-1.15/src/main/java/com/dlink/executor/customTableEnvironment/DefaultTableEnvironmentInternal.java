package com.dlink.executor.customTableEnvironment;

import org.apache.flink.table.api.CompiledPlan;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.internal.TableEnvironmentInternal;
import org.apache.flink.table.api.internal.TableResultInternal;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.delegation.InternalPlan;
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
public interface DefaultTableEnvironmentInternal extends TableEnvironmentInternal {

    TableEnvironmentInternal getTableEnvironmentInternal();

    //region TableEnvironmentInternal interface
    default Parser getParser() {
        return getTableEnvironmentInternal().getParser();
    }

    default CatalogManager getCatalogManager() {
        return getTableEnvironmentInternal().getCatalogManager();
    }

    default OperationTreeBuilder getOperationTreeBuilder() {
        return getTableEnvironmentInternal().getOperationTreeBuilder();
    }

    default Table fromTableSource(TableSource<?> tableSource) {
        return getTableEnvironmentInternal().fromTableSource(tableSource);
    }

    default TableResultInternal executeInternal(List<ModifyOperation> list) {
        return getTableEnvironmentInternal().executeInternal(list);
    }

    default TableResultInternal executeInternal(Operation operation) {
        return getTableEnvironmentInternal().executeInternal(operation);
    }

    default String explainInternal(List<Operation> list, ExplainDetail... explainDetails) {
        return getTableEnvironmentInternal().explainInternal(list, explainDetails);
    }

    default void registerTableSourceInternal(String s, TableSource<?> tableSource) {
        getTableEnvironmentInternal().registerTableSourceInternal(s, tableSource);
    }

    default void registerTableSinkInternal(String s, TableSink<?> tableSink) {
        getTableEnvironmentInternal().registerTableSinkInternal(s, tableSink);
    }

    default CompiledPlan compilePlan(List<ModifyOperation> list) {
        return getTableEnvironmentInternal().compilePlan(list);
    }

    default TableResultInternal executePlan(InternalPlan internalPlan) {
        return getTableEnvironmentInternal().executePlan(internalPlan);
    }

    default String explainPlan(InternalPlan internalPlan, ExplainDetail... explainDetails) {
        return getTableEnvironmentInternal().explainPlan(internalPlan, explainDetails);
    }
    //endregion
}
