package org.dinky.trans;

import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.planner.parse.AbstractRegexParseStrategy;
import org.dinky.trans.ddl.NewCreateAggTableOperation;

import java.util.regex.Pattern;

public class CreateAggTableOperationNewParseStrategy extends AbstractRegexParseStrategy {

    private static final String PATTERN_STR = "(CREATE NEWAGGTABLE)\\s+(.*)";
    private static final Pattern ADD_JAR_PATTERN =
            Pattern.compile(PATTERN_STR, Pattern.CASE_INSENSITIVE);

    public static final CreateAggTableOperationNewParseStrategy INSTANCE = new CreateAggTableOperationNewParseStrategy();

    protected CreateAggTableOperationNewParseStrategy() {
        super(ADD_JAR_PATTERN);
    }

    @Override
    public Operation convert(String statement) {
        return new NewCreateAggTableOperation(statement);
    }

    @Override
    public String[] getHints() {
        return new String[]{"CREATE AGGTABLENEW"};
    }
}
