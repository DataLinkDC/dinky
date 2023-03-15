package org.dinky.trans;

import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.planner.parse.AbstractRegexParseStrategy;
import org.dinky.trans.ddl.RemoveJarOperation;

import java.util.regex.Pattern;

public class RemoveJarOperationParseStrategy extends AbstractRegexParseStrategy {

    private static final String ADD_JAR = "(remove\\s+jar)\\s+'(.*.jar)'";
    private static final Pattern ADD_JAR_PATTERN =
            Pattern.compile(ADD_JAR, Pattern.CASE_INSENSITIVE);

    public static final RemoveJarOperationParseStrategy INSTANCE = new RemoveJarOperationParseStrategy();

    protected RemoveJarOperationParseStrategy() {
        super(ADD_JAR_PATTERN);
    }

    @Override
    public Operation convert(String statement) {
        return new RemoveJarOperation();
    }

    @Override
    public String[] getHints() {
        return new String[]{"Custom Add Jar"};
    }
}
