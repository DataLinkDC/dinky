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

package com.dlink.function.compiler;

import com.dlink.assertion.Asserts;
import com.dlink.function.data.model.UDF;
import com.dlink.function.exception.UDFCompilerException;

import org.apache.flink.configuration.ReadableConfig;

import java.util.List;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;

/**
 * @author ZackYoung
 * @since 0.6.8
 */
public interface FunctionCompiler {

    /**
     * 函数代码在线动态编译
     *
     * @param udf  udf
     * @param conf flink-conf
     * @param missionId 任务id
     * @return 是否成功
     */
    boolean compiler(UDF udf, ReadableConfig conf, Integer missionId);

    /**
     * 编译
     * @param udf udf实例
     * @param conf flink-conf
     * @param missionId 任务id
     * @return 编译状态
     */
    static boolean getCompiler(UDF udf, ReadableConfig conf, Integer missionId) {
        Asserts.checkNull(udf, "udf为空");
        Asserts.checkNull(udf.getCode(), "udf 代码为空");
        boolean success;
        switch (udf.getFunctionLanguage()) {
            case JAVA:
                success = Singleton.get(JavaCompiler.class).compiler(udf, conf, missionId);
                break;
            case SCALA:
                success = Singleton.get(ScalaCompiler.class).compiler(udf, conf, missionId);
                break;
            case PYTHON:
                success = Singleton.get(PythonFunction.class).compiler(udf, conf, missionId);
                break;
            default:
                throw UDFCompilerException.notSupportedException(udf.getFunctionLanguage().name());
        }
        return success;
    }

    /**
     * 编译
     * @param udfList udf、实例列表
     * @param conf flink-conf
     * @param missionId 任务id
     */
    static void getCompiler(List<UDF> udfList, ReadableConfig conf, Integer missionId) {
        for (UDF udf : udfList) {
            if (!getCompiler(udf, conf, missionId)) {
                throw new UDFCompilerException(StrUtil.format("codeLanguage:{} , className:{} 编译失败",
                        udf.getFunctionLanguage(), udf.getClassName()));
            }
        }
    }
}
