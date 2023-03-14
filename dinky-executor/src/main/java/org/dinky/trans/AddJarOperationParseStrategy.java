package org.dinky.trans;

import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.planner.parse.AbstractRegexParseStrategy;
import org.dinky.trans.ddl.AddJarOperation;

import java.util.regex.Pattern;

public class AddJarOperationParseStrategy<T> extends AbstractRegexParseStrategy {

    private static final String ADD_JAR = "(add\\s+jar)\\s+'(.*.jar)'";
    private static final Pattern ADD_JAR_PATTERN =
            Pattern.compile(ADD_JAR, Pattern.CASE_INSENSITIVE);

    static final AddJarOperationParseStrategy INSTANCE = new AddJarOperationParseStrategy();

    protected AddJarOperationParseStrategy() {
        super(ADD_JAR_PATTERN);
    }

    @Override
    public Operation convert(String statement) {
        return new AddJarOperation();
    }

    @Override
    public String[] getHints() {
        return new String[]{"Custom Add Jar"};
    }
}
