package org.dinky.operations;

import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.internal.TableResultInternal;
import org.apache.flink.table.operations.ExecutableOperation;
import org.apache.flink.table.operations.Operation;
import org.dinky.executor.CustomTableEnvironment;
import org.dinky.trans.ExtendOperation;

import java.util.Optional;

public class DinkyOperationExecutor {
    private final ExecutableOperation.Context context;

    private final TableEnvironment tableEnvironment;

    public DinkyOperationExecutor(TableEnvironment tableEnvironment, ExecutableOperation.Context context) {
        this.tableEnvironment = tableEnvironment;
        this.context = context;

    }

    public Optional<TableResultInternal> executeOperation(Operation operation) {
        ExtendOperation extendOperation = (ExtendOperation) operation;
        return Optional.of((TableResultInternal)extendOperation.execute((CustomTableEnvironment)tableEnvironment).get());
    }
}
