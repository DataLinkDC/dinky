package com.dlink.trans.ddl;

import com.dlink.executor.Executor;
import com.dlink.trans.AbstractOperation;
import com.dlink.trans.Operation;
import org.apache.flink.table.api.TableResult;

/**
 * ShowFragmentsOperation
 *
 * @author wenmo
 * @since 2022/2/17 16:31
 **/
public class ShowFragmentsOperation extends AbstractOperation implements Operation {

    private String KEY_WORD = "SHOW FRAGMENTS";

    public ShowFragmentsOperation() {
    }

    public ShowFragmentsOperation(String statement) {
        super(statement);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public Operation create(String statement) {
        return new ShowFragmentsOperation(statement);
    }

    @Override
    public TableResult build(Executor executor) {
        return executor.getSqlManager().getSqlFragments();
    }
}
