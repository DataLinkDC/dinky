package com.dlink.interceptor;

import com.dlink.assertion.Asserts;
import com.dlink.catalog.function.FunctionManager;
import com.dlink.catalog.function.UDFunction;
import com.dlink.constant.FlinkFunctionConstant;
import com.dlink.executor.custom.CustomTableEnvironmentImpl;
import com.dlink.trans.Operation;
import com.dlink.trans.Operations;
import com.dlink.ud.udtaf.RowsToMap;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.functions.*;

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

    public static boolean build( CustomTableEnvironmentImpl stEnvironment,String statemnet){
        initFunctions(stEnvironment,statemnet);
        Operation operation = Operations.buildOperation(statemnet);
        if(Asserts.isNotNull(operation)) {
            operation.build(stEnvironment);
            return operation.noExecute();
        }
        return false;
    }

    private static void initFunctions(CustomTableEnvironmentImpl stEnvironment,String statemnet){
        Map<String, UDFunction> usedFunctions = FunctionManager.getUsedFunctions(statemnet);
        String[] udfs = stEnvironment.listUserDefinedFunctions();
        List<String> udflist = Arrays.asList(udfs);
        for (Map.Entry<String, UDFunction> entry : usedFunctions.entrySet()) {
            if(!udflist.contains(entry.getKey())){
                if( entry.getValue().getType()== UDFunction.UDFunctionType.Scalar){
                    stEnvironment.registerFunction(entry.getKey(),
                            (ScalarFunction)entry.getValue().getFunction());
                }else if( entry.getValue().getType()== UDFunction.UDFunctionType.Table){
                    stEnvironment.registerFunction(entry.getKey(),
                            (TableFunction)entry.getValue().getFunction());
                }else if( entry.getValue().getType()== UDFunction.UDFunctionType.Aggregate){
                    stEnvironment.registerFunction(entry.getKey(),
                            (AggregateFunction)entry.getValue().getFunction());
                }else if( entry.getValue().getType()== UDFunction.UDFunctionType.TableAggregate){
                    stEnvironment.registerFunction(entry.getKey(),
                            (TableAggregateFunction)entry.getValue().getFunction());
                }
            }
        }
    }
}
