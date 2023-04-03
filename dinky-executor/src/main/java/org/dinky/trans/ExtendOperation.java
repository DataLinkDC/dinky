package org.dinky.trans;

import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.operations.Operation;
import org.dinky.executor.Executor;

import java.util.Optional;

/**
 *
 */
public interface ExtendOperation extends Operation {
    Optional<? extends TableResult> execute(Executor executor);
}
