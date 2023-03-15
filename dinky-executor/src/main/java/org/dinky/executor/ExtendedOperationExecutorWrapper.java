package org.dinky.executor;

import org.apache.flink.table.api.internal.TableResultInternal;
import org.apache.flink.table.delegation.ExtendedOperationExecutor;
import org.apache.flink.table.operations.Operation;
import org.dinky.trans.ddl.RemoveJarOperation;

import java.util.Optional;

public class ExtendedOperationExecutorWrapper implements ExtendedOperationExecutor {

    private ExtendedOperationExecutor extendedOperationExecutor;

    public ExtendedOperationExecutorWrapper(ExtendedOperationExecutor extendedOperationExecutor) {
        this.extendedOperationExecutor = extendedOperationExecutor;
    }


    @Override
    public Optional<TableResultInternal> executeOperation(Operation operation) {
        if (operation instanceof RemoveJarOperation) {
            return executeAddJarOperation((RemoveJarOperation) operation);
        } else {
            return extendedOperationExecutor.executeOperation(operation);
        }
    }

    public Optional<TableResultInternal> executeAddJarOperation(RemoveJarOperation option) {
        return Optional.of(TableResultInternal.TABLE_RESULT_OK);
    }

}
