package com.dlink.trans.ddl;

import com.dlink.assertion.Asserts;
import com.dlink.executor.Executor;
import com.dlink.parser.SingleSqlParserFactory;
import com.dlink.trans.AbstractOperation;
import com.dlink.trans.Operation;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.api.TableResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SetOperation
 *
 * @author wenmo
 * @since 2021/10/21 19:56
 **/
public class SetOperation extends AbstractOperation implements Operation {

    private String KEY_WORD = "SET";

    public SetOperation() {
    }

    public SetOperation(String statement) {
        super(statement);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public Operation create(String statement) {
        return new SetOperation(statement);
    }

    @Override
    public TableResult build(Executor executor) {
        try {
            if (null != Class.forName("org.apache.log4j.Logger")) {
                executor.parseAndLoadConfiguration(statement);
                return null;
            }
        } catch (ClassNotFoundException e) {
        }
        Map<String, List<String>> map = SingleSqlParserFactory.generateParser(statement);
        if (Asserts.isNotNullMap(map) && map.size() == 2) {
            Map<String, String> confMap = new HashMap<>();
            confMap.put(StringUtils.join(map.get("SET"), "."), StringUtils.join(map.get("="), ","));
            executor.getCustomTableEnvironment().getConfig().addConfiguration(Configuration.fromMap(confMap));
            Configuration configuration = Configuration.fromMap(confMap);
            executor.getExecutionConfig().configure(configuration, null);
            executor.getCustomTableEnvironment().getConfig().addConfiguration(configuration);
        }
        return null;
    }
}
