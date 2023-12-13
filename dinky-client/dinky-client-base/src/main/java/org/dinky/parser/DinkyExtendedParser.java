package org.dinky.parser;

import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.planner.parse.ExtendedParseStrategy;
import org.apache.flink.table.planner.parse.ExtendedParser;
import org.dinky.trans.parse.AddJarSqlParseStrategy;
import org.dinky.trans.parse.CreateAggTableSelectSqlParseStrategy;
import org.dinky.trans.parse.CreateTemporalTableFunctionParseStrategy;
import org.dinky.trans.parse.SetSqlParseStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DinkyExtendedParser extends ExtendedParser {
    public static final DinkyExtendedParser INSTANCE = new DinkyExtendedParser();

    public static final List<ExtendedParseStrategy> PARSE_STRATEGIES = Arrays.asList(
            AddJarSqlParseStrategy.INSTANCE,
            CreateAggTableSelectSqlParseStrategy.INSTANCE,
            SetSqlParseStrategy.INSTANCE,
            CreateTemporalTableFunctionParseStrategy.INSTANCE);

    @Override
    public Optional<Operation> parse(String statement) {
        for (ExtendedParseStrategy strategy : PARSE_STRATEGIES) {
            if (strategy.match(statement)) {
                return Optional.of(strategy.convert(statement));
            }
        }
        return Optional.empty();
    }
}
