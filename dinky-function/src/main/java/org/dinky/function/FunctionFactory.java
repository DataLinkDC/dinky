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

package org.dinky.function;

import org.dinky.function.compiler.FunctionCompiler;
import org.dinky.function.compiler.FunctionPackage;
import org.dinky.function.data.model.UDF;
import org.dinky.function.data.model.UDFPath;

import org.apache.flink.configuration.Configuration;

import java.util.List;

/** @since 0.6.8 */
public class FunctionFactory {
    /**
     * UDF compilation & packaging initialization(udf编译 & 打包 初始化)
     * @param udfClassList udf列表
     * @param taskId 当前任务id
     * @return 打包过后的路径
     */
    public static UDFPath initUDF(List<UDF> udfClassList, Integer taskId) {
        return initUDF(udfClassList, taskId, new Configuration());
    }

    public static void initUDF(UDF udf, Integer taskId) {
        // 编译
        FunctionCompiler.getCompiler(udf, new Configuration(), taskId);

        // 打包
        FunctionPackage.bale(udf, taskId);
    }

    /**
     * UDF compilation & packaging initialization(udf编译 & 打包 初始化)
     *
     * @param udfClassList udf列表
     * @param taskId 当前任务id
     * @return 打包过后的路径
     */
    public static UDFPath initUDF(List<UDF> udfClassList, Integer taskId, Configuration configuration) {

        // 编译
        FunctionCompiler.getCompiler(udfClassList, configuration, taskId);

        // 打包
        return FunctionPackage.bale(udfClassList, taskId);
    }
}
