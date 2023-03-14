package org.dinky.executor;
import org.apache.flink.table.catalog.UnresolvedIdentifier;
import org.apache.flink.table.delegation.Parser;
import org.apache.flink.table.expressions.ResolvedExpression;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.RowType;

import javax.annotation.Nullable;
import java.util.List;

public class ParserWrapper implements Parser {

    private Parser parser;

    public ParserWrapper(Parser parser) {
        this.parser = parser;
    }

    @Override
    public List<Operation> parse(String statement) {
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
}
