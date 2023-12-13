package org.dinky.operations;

import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.internal.TableEnvironmentInternal;
import org.apache.flink.table.api.internal.TableResultInternal;
import org.apache.flink.table.operations.ExecutableOperation;
import org.apache.flink.table.operations.Operation;

public class DinkyExecutableOperation implements ExecutableOperation {

    private final Operation innerOperation;
    private final TableEnvironment tableEnvironment;

    public DinkyExecutableOperation(TableEnvironment tableEnvironment, Operation innerOperation) {
        this.tableEnvironment = tableEnvironment;
        this.innerOperation = innerOperation;
    }

    @Override
    public TableResultInternal execute(Context ctx) {
        DinkyOperationExecutor operationExecutor = new DinkyOperationExecutor(tableEnvironment, ctx);
        return operationExecutor.executeOperation(innerOperation).get();
    }

    public Operation getInnerOperation() {
        return innerOperation;
    }

    @Override
    public String asSummaryString() {
        return innerOperation.asSummaryString();
    }
}