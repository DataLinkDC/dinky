package org.dinky.executor;

import org.apache.flink.table.api.internal.TableResultInternal;
import org.apache.flink.table.delegation.ExtendedOperationExecutor;
import org.apache.flink.table.operations.Operation;

import java.util.Optional;

public class ExtendedOperationExecutorWrapper implements ExtendedOperationExecutor {

    private ExtendedOperationExecutor extendedOperationExecutor;

    public ExtendedOperationExecutorWrapper(ExtendedOperationExecutor extendedOperationExecutor) {
        this.extendedOperationExecutor = extendedOperationExecutor;
    }

    @Override
    public Optional<TableResultInternal> executeOperation(Operation operation) {
        return extendedOperationExecutor.executeOperation(operation);
    }
}
