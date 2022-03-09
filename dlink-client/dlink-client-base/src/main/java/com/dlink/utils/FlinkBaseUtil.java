package com.dlink.utils;

import com.dlink.constant.FlinkParamConstant;
import org.apache.flink.api.java.utils.ParameterTool;

import java.util.HashMap;
import java.util.Map;

/**
 * FlinkBaseUtil
 *
 * @author wenmo
 * @since 2022/3/9 19:15
 */
public class FlinkBaseUtil {

    public static Map<String,String> getParamsFromArgs(String[] args){
        Map<String,String> params = new HashMap<>();
        ParameterTool parameters = ParameterTool.fromArgs(args);
        params.put(FlinkParamConstant.ID,parameters.get(FlinkParamConstant.ID, null));
        params.put(FlinkParamConstant.DRIVER,parameters.get(FlinkParamConstant.DRIVER, null));
        params.put(FlinkParamConstant.URL,parameters.get(FlinkParamConstant.URL, null));
        params.put(FlinkParamConstant.USERNAME,parameters.get(FlinkParamConstant.USERNAME, null));
        params.put(FlinkParamConstant.PASSWORD,parameters.get(FlinkParamConstant.PASSWORD, null));
        return params;
    }
}
