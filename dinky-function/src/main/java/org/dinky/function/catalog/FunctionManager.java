/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.dlink.function.catalog;

import com.dlink.function.constant.FlinkFunctionConstant;
import com.dlink.function.udf.GetKey;
import com.dlink.function.udtaf.RowsToMap;
import com.dlink.function.udtaf.Top2;

import java.util.HashMap;
import java.util.Map;

/**
 * FunctionManager
 *
 * @author wenmo
 * @since 2021/6/14 21:19
 */
@Deprecated
public class FunctionManager {

    private static Map<String, UDFunction> functions = new HashMap<String, UDFunction>() {

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

    public static Map<String, UDFunction> getUsedFunctions(String statement) {
        Map<String, UDFunction> map = new HashMap<>();
        String sql = statement.toLowerCase();
        for (Map.Entry<String, UDFunction> entry : functions.entrySet()) {
            if (sql.contains(entry.getKey().toLowerCase())) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }
}
