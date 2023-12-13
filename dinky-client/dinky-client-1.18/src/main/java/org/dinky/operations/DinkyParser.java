package org.dinky.operations;

import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.planner.parse.ExtendedParseStrategy;
import org.dinky.parser.DinkyExtendedParser;

import java.util.Optional;

public class DinkyParser extends DinkyExtendedParser {
    private final TableEnvironment tableEnvironment;

    public DinkyParser(TableEnvironment tableEnvironment) {
        this.tableEnvironment = tableEnvironment;
    }

    @Override
    public Optional<Operation> parse(String statement) {
        for (ExtendedParseStrategy strategy : PARSE_STRATEGIES) {
            if (strategy.match(statement)) {
                return Optional.of(new DinkyExecutableOperation(this.tableEnvironment, strategy.convert(statement)));
            }
        }
        return Optional.empty();
    }

}
