package com.dlink.trans.ddl;

import com.dlink.executor.Executor;
import com.dlink.trans.AbstractOperation;
import com.dlink.trans.Operation;
import org.apache.flink.table.api.Table;

import java.util.List;

/**
 * CreateAggTableOperation
 *
 * @author wenmo
 * @since 2021/6/13 19:24
 */
public class CreateAggTableOperation extends AbstractOperation implements Operation{

    private String KEY_WORD = "CREATE AGGTABLE";

    public CreateAggTableOperation() {
    }

    public CreateAggTableOperation(String statement) {
        super(statement);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public Operation create(String statement) {
        return new CreateAggTableOperation(statement);
    }

    @Override
    public void build(Executor executor) {
        AggTable aggTable = AggTable.build(statement);
        Table source = executor.getCustomTableEnvironmentImpl().sqlQuery("select * from "+ aggTable.getTable());
        List<String> wheres = aggTable.getWheres();
        if(wheres!=null&&wheres.size()>0) {
            for (String s : wheres) {
                source = source.filter(s);
            }
        }
        Table sink = source.groupBy(aggTable.getGroupBy())
                .flatAggregate(aggTable.getAggBy())
                .select(aggTable.getColumns());
        executor.getCustomTableEnvironmentImpl().registerTable(aggTable.getName(), sink);
    }
}
