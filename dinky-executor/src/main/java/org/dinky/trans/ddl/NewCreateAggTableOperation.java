package org.dinky.trans.ddl;

import org.apache.flink.table.api.TableResult;
import org.dinky.executor.Executor;
import org.dinky.trans.AbstractOperation;
import org.dinky.trans.Operation;

public class NewCreateAggTableOperation extends AbstractOperation implements Operation {
    private static final String KEY_WORD = "CREATE AGGTABLENEW";

    public NewCreateAggTableOperation(String statement) {
        super(statement);
    }

    public NewCreateAggTableOperation() {}

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public Operation create(String statement) {
        return null;
    }

    @Override
    public TableResult build(Executor executor) {
        return null;
    }

    public void init() {

    }
}
