package com.dlink.trans.ddl;

import com.dlink.assertion.Asserts;
import com.dlink.executor.Executor;
import com.dlink.parser.SingleSqlParserFactory;
import com.dlink.trans.AbstractOperation;
import com.dlink.trans.Operation;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.table.api.TableResult;

import java.util.List;
import java.util.Map;

/**
 * ShowFragmentOperation
 *
 * @author wenmo
 * @since 2022/2/17 17:08
 **/
public class ShowFragmentOperation extends AbstractOperation implements Operation {
    private String KEY_WORD = "SHOW FRAGMENT ";

    public ShowFragmentOperation() {
    }

    public ShowFragmentOperation(String statement) {
        super(statement);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public Operation create(String statement) {
        return new ShowFragmentOperation(statement);
    }

    @Override
    public TableResult build(Executor executor) {
        Map<String, List<String>> map = SingleSqlParserFactory.generateParser(statement);
        if (Asserts.isNotNullMap(map)) {
            if (map.containsKey("FRAGMENT")) {
                return executor.getSqlManager().getSqlFragmentResult(StringUtils.join(map.get("FRAGMENT"), ""));
            }
        }
        return executor.getSqlManager().getSqlFragmentResult(null);
    }
}
