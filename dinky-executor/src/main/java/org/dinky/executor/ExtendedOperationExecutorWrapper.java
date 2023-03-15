package org.dinky.executor;

import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.internal.TableResultInternal;
import org.apache.flink.table.delegation.ExtendedOperationExecutor;
import org.apache.flink.table.operations.Operation;
import org.dinky.trans.ddl.AggTable;
import org.dinky.trans.ddl.NewCreateAggTableOperation;

import java.util.List;
import java.util.Optional;

import static org.apache.flink.table.api.Expressions.$;

public class ExtendedOperationExecutorWrapper implements ExtendedOperationExecutor {

    private ExtendedOperationExecutor extendedOperationExecutor;
    private Executor executor;

    public ExtendedOperationExecutorWrapper(ExtendedOperationExecutor extendedOperationExecutor, Executor executor) {
        this.extendedOperationExecutor = extendedOperationExecutor;
        this.executor = executor;
    }


    @Override
    public Optional<TableResultInternal> executeOperation(Operation operation) {
        if (operation instanceof NewCreateAggTableOperation) {
            return executeCreateAggTableOperationNew((NewCreateAggTableOperation) operation);
        } else {
            return extendedOperationExecutor.executeOperation(operation);
        }
    }

    public Optional<TableResultInternal> executeCreateAggTableOperationNew(NewCreateAggTableOperation option) {
        AggTable aggTable = AggTable.build(option.getStatement());
        Table source = executor.getCustomTableEnvironment().sqlQuery("select * from " + aggTable.getTable());
        List<String> wheres = aggTable.getWheres();
        if (wheres != null && wheres.size() > 0) {
            for (String s : wheres) {
                source = source.filter($(s));
            }
        }
        Table sink =
                source.groupBy($(aggTable.getGroupBy()))
                        .flatAggregate($(aggTable.getAggBy()))
                        .select($(aggTable.getColumns()));
        executor.getCustomTableEnvironment().registerTable(aggTable.getName(), sink);
        return null;
    }

}
