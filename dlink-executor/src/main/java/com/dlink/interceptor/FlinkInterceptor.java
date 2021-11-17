package com.dlink.interceptor;

import com.dlink.assertion.Asserts;
import com.dlink.catalog.function.FunctionManager;
import com.dlink.catalog.function.UDFunction;
import com.dlink.executor.Executor;
import com.dlink.executor.custom.CustomTableEnvironmentImpl;
import com.dlink.trans.Operation;
import com.dlink.trans.Operations;
import com.dlink.utils.SqlUtil;
import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.table.functions.TableAggregateFunction;
import org.apache.flink.table.functions.TableFunction;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * FlinkInterceptor
 *
 * @author wenmo
 * @since 2021/6/11 22:17
 */
public class FlinkInterceptor {

    public static String pretreatStatement(Executor executor, String statement) {
        statement = SqlUtil.removeNote(statement);
        if(executor.isUseSqlFragment()) {
            statement = executor.getSqlManager().parseVariable(statement);
        }
        initFunctions(executor.getCustomTableEnvironmentImpl(), statement);
        return statement;
    }

    public static boolean build(Executor executor, String statement) {
        Operation operation = Operations.buildOperation(statement);
        if (Asserts.isNotNull(operation)) {
            operation.build(executor.getCustomTableEnvironmentImpl());
            return operation.noExecute();
        }
        return false;
    }

    private static void initFunctions(CustomTableEnvironmentImpl stEnvironment, String statement) {
        Map<String, UDFunction> usedFunctions = FunctionManager.getUsedFunctions(statement);
        String[] udfs = stEnvironment.listUserDefinedFunctions();
        List<String> udflist = Arrays.asList(udfs);
        for (Map.Entry<String, UDFunction> entry : usedFunctions.entrySet()) {
            if (!udflist.contains(entry.getKey())) {
                if (entry.getValue().getType() == UDFunction.UDFunctionType.Scalar) {
                    stEnvironment.registerFunction(entry.getKey(),
                            (ScalarFunction) entry.getValue().getFunction());
                } else if (entry.getValue().getType() == UDFunction.UDFunctionType.Table) {
                    stEnvironment.registerFunction(entry.getKey(),
                            (TableFunction) entry.getValue().getFunction());
                } else if (entry.getValue().getType() == UDFunction.UDFunctionType.Aggregate) {
                    stEnvironment.registerFunction(entry.getKey(),
                            (AggregateFunction) entry.getValue().getFunction());
                } else if (entry.getValue().getType() == UDFunction.UDFunctionType.TableAggregate) {
                    stEnvironment.registerFunction(entry.getKey(),
                            (TableAggregateFunction) entry.getValue().getFunction());
                }
            }
        }
    }

}
