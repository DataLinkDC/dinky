package org.dinky.trans.ddl;

import org.apache.flink.table.api.TableResult;
import org.dinky.executor.Executor;
import org.dinky.trans.AbstractOperation;
import org.dinky.trans.Operation;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WatchTableOperation extends AbstractOperation implements Operation {

    private static final String KEY_WORD = "WATCH";

    public static final String PATTERN_STR = "WATCH (.+)";
    public static final Pattern PATTERN = Pattern.compile(PATTERN_STR, Pattern.CASE_INSENSITIVE);

    public static final String CREATE_SQL_TEMPLATE = "CREATE TABLE print_{0} WITH (''connector'' = ''printnet'', " +
            "''port''=''{2}'', ''hostName'' = ''{1}'', ''sink.parallelism''=''{3}'')\n" +
            "LIKE {0};";
    public static final String INSERT_SQL_TEMPLATE = "insert into print_{0} select * from {0};";

    public WatchTableOperation() {
    }

    public WatchTableOperation(String statement) {
        super(statement);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public Operation create(String statement) {
        return new WatchTableOperation(statement);
    }

    @Override
    public TableResult build(Executor executor) {
        Matcher matcher = PATTERN.matcher(statement);
        matcher.find();
        String tableName = matcher.group(1);

        String printCreateSql = MessageFormat.format(CREATE_SQL_TEMPLATE, tableName, "127.0.0.1", "8888", 1);
        executor.getCustomTableEnvironment().executeSql(printCreateSql);

        String printInsertSql =  MessageFormat.format(INSERT_SQL_TEMPLATE, tableName);
        return executor.getCustomTableEnvironment().executeSql(printInsertSql);
    }
}
