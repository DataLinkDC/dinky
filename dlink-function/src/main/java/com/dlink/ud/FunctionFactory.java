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

package com.dlink.ud;

import com.dlink.executor.Executor;
import com.dlink.ud.compiler.FunctionCompiler;
import com.dlink.ud.compiler.FunctionPackage;
import com.dlink.ud.data.model.UDF;
import com.dlink.ud.data.model.UDFPath;

import java.util.List;

/**
 * @author ZackYoung
 * @since 0.6.8
 */
public class FunctionFactory {

    /**
     * udf编译 & 打包 初始化
     * @param udfClassList udf列表
     * @param missionId 当前任务id
     * @param executor flink执行器
     * @return 打包过后的路径
     */
    public static UDFPath initUDF(List<UDF> udfClassList, Integer missionId, Executor executor) {

        // 编译
        FunctionCompiler.getCompiler(udfClassList, executor.getTableConfig().getConfiguration(), missionId);

        // 打包
        return FunctionPackage.bale(udfClassList, missionId);
    }
}
