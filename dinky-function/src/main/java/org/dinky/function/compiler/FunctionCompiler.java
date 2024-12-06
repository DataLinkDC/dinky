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

package org.dinky.function.compiler;

import org.dinky.function.data.model.UDF;
import org.dinky.function.exception.UDFCompilerException;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.ReadableConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;

/** @since 0.6.8 */
public interface FunctionCompiler {
    Logger log = LoggerFactory.getLogger(FunctionCompiler.class);

    Set<String> COMPILER_CACHE = new HashSet<>();

    /**
     * 函数代码在线动态编译
     *
     * @param udf udf
     * @param conf flink-conf
     * @param taskId 任务id
     * @return 是否成功
     */
    boolean compiler(UDF udf, ReadableConfig conf, Integer taskId);

    static boolean getCompilerByTask(UDF udf, Map<String, String> conf, Integer taskId) {
        return getCompiler(udf, Configuration.fromMap(conf), taskId);
    }
    /**
     * 编译
     *
     * @param udf udf实例
     * @param conf flink-conf
     * @param taskId 任务id
     * @return 编译状态
     */
    static boolean getCompiler(UDF udf, ReadableConfig conf, Integer taskId) {
        log.info("Compiled UDF: {},; Language: {}", udf.getClassName(), udf.getFunctionLanguage());

        String key = udf.getClassName() + udf.getFunctionLanguage();
        if (COMPILER_CACHE.contains(key)) {
            return true;
        }
        boolean success;
        switch (udf.getFunctionLanguage()) {
            case JAVA:
                success = Singleton.get(JavaCompiler.class).compiler(udf, conf, taskId);
                break;
            case SCALA:
                success = Singleton.get(ScalaCompiler.class).compiler(udf, conf, taskId);
                break;
            case PYTHON:
                success = Singleton.get(PythonFunction.class).compiler(udf, conf, taskId);
                break;
            default:
                throw UDFCompilerException.notSupportedException(
                        udf.getFunctionLanguage().name());
        }
        if (success) {
            COMPILER_CACHE.add(key);
        }
        return success;
    }

    /**
     * 编译
     *
     * @param udfList udf、实例列表
     * @param conf flink-conf
     * @param taskId 任务id
     */
    static void getCompiler(List<UDF> udfList, ReadableConfig conf, Integer taskId) {
        for (UDF udf : udfList) {
            if (!getCompiler(udf, conf, taskId)) {
                throw new UDFCompilerException(StrUtil.format(
                        "codeLanguage:{} , className:{} 编译失败", udf.getFunctionLanguage(), udf.getClassName()));
            }
        }
    }
}
