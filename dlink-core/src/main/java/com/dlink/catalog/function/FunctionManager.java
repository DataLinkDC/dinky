package com.dlink.catalog.function;

import com.dlink.constant.FlinkFunctionConstant;
import com.dlink.ud.udf.GetKey;
import com.dlink.ud.udtaf.RowsToMap;
import com.dlink.ud.udtaf.Top2;
import org.apache.flink.table.functions.FunctionDefinition;
import org.apache.flink.table.functions.UserDefinedFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * FunctionManager
 *
 * @author wenmo
 * @since 2021/6/14 21:19
 */
public class FunctionManager {

    private static Map<String,UDFunction> functions = new HashMap<String,UDFunction>(){
        {
            put(FlinkFunctionConstant.GET_KEY,
                    new UDFunction(FlinkFunctionConstant.GET_KEY,
                            UDFunction.UDFunctionType.Scalar,
                            new GetKey()));
            put(FlinkFunctionConstant.TO_MAP,
                    new UDFunction(FlinkFunctionConstant.TO_MAP,
                            UDFunction.UDFunctionType.TableAggregate,
                            new RowsToMap()));
            put(FlinkFunctionConstant.TOP2,
                    new UDFunction(FlinkFunctionConstant.TOP2,
                            UDFunction.UDFunctionType.TableAggregate,
                            new Top2()));
        }
    };

    public static Map<String,UDFunction> getUsedFunctions(String statement){
        Map<String,UDFunction> map = new HashMap<>();
        String sql = statement.toLowerCase();
        for (Map.Entry<String, UDFunction> entry : functions.entrySet()) {
            if(sql.contains(entry.getKey().toLowerCase())){
                map.put(entry.getKey(),entry.getValue());
            }
        }
        return map;
    }
}
