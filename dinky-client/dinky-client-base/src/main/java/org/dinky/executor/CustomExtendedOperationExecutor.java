package org.dinky.executor;

import org.apache.flink.table.api.internal.TableResultInternal;
import org.apache.flink.table.operations.Operation;

import java.util.Optional;

public interface CustomExtendedOperationExecutor {
    Optional<TableResultInternal> executeOperation(Operation operation);
}
