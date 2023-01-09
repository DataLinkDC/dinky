package com.dlink.interceptor;

import org.apache.flink.table.api.TableResult;

import com.dlink.assertion.Asserts;
import com.dlink.executor.Executor;
import com.dlink.trans.Operation;
import com.dlink.trans.Operations;
import com.dlink.utils.SqlUtil;

/**
 * FlinkInterceptor
 *
 * @author wenmo
 * @since 2021/6/11 22:17
 */
public class FlinkInterceptor {

    public static String pretreatStatement(Executor executor, String statement) {
        statement = SqlUtil.removeNote(statement);
        if (executor.isUseSqlFragment()) {
            statement = executor.getSqlManager().parseVariable(statement);
        }
        return statement.trim();
    }

    // return false to continue with executeSql
    public static FlinkInterceptorResult build(Executor executor, String statement) {
        boolean noExecute = false;
        TableResult tableResult = null;
        Operation operation = Operations.buildOperation(statement);
        if (Asserts.isNotNull(operation)) {
            tableResult = operation.build(executor);
            noExecute = operation.noExecute();
        }
        return FlinkInterceptorResult.build(noExecute, tableResult);
    }
}
