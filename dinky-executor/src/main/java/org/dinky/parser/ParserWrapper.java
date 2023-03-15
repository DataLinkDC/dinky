package org.dinky.parser;

import org.apache.flink.table.catalog.UnresolvedIdentifier;
import org.apache.flink.table.delegation.Parser;
import org.apache.flink.table.expressions.ResolvedExpression;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.planner.parse.ExtendedParseStrategy;
import org.apache.flink.table.planner.parse.ExtendedParser;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.RowType;
import org.dinky.trans.RemoveJarOperationParseStrategy;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ParserWrapper implements Parser {

    private Parser parser;
    private DinkyExtendedParser DINKY_EXTENDED_PARSER = DinkyExtendedParser.INSTANCE;

    public ParserWrapper(Parser parser) {
        this.parser = parser;
    }

    @Override
    public List<Operation> parse(String statement) {
        Optional<Operation> command = DINKY_EXTENDED_PARSER.parse(statement);
        if (command.isPresent()) {
            return Collections.singletonList(command.get());
        }

        return parser.parse(statement);
    }

    @Override
    public UnresolvedIdentifier parseIdentifier(String identifier) {
        return parser.parseIdentifier(identifier);
    }

    @Override
    public ResolvedExpression parseSqlExpression(String sqlExpression, RowType inputRowType,
                                                 @Nullable LogicalType outputType) {
        return parser.parseSqlExpression(sqlExpression, inputRowType, outputType);
    }

    @Override
    public String[] getCompletionHints(String statement, int position) {
        return parser.getCompletionHints(statement, position);
    }

    public static class DinkyExtendedParser extends ExtendedParser {
        public static final DinkyExtendedParser INSTANCE = new DinkyExtendedParser();

        private static final List<ExtendedParseStrategy> PARSE_STRATEGIES =
                Arrays.asList(RemoveJarOperationParseStrategy.INSTANCE);

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
}
