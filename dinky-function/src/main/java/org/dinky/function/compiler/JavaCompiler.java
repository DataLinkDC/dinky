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

import org.dinky.function.constant.PathConstant;
import org.dinky.function.data.model.UDF;

import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.table.catalog.FunctionLanguage;

import lombok.extern.slf4j.Slf4j;

/**
 * java 编译
 */
@Slf4j
public class JavaCompiler implements FunctionCompiler {

    /**
     * 函数代码在线动态编译
     *
     * @param udf       udf
     * @param conf      flink-conf
     * @param taskId 任务id
     * @return 是否成功
     */
    @Override
    public synchronized boolean compiler(UDF udf, ReadableConfig conf, Integer taskId) {

        // TODO 改为ProcessStep注释
        log.info("Compiling java code, class: {}", udf.getClassName());
        CustomStringJavaCompiler compiler = new CustomStringJavaCompiler(udf.getCode());
        boolean res = compiler.compilerToTmpPath(PathConstant.getUdfCompilerPath(FunctionLanguage.JAVA));
        String className = compiler.getFullClassName();
        if (res) {
            log.info("class compiled successfully:{}", className);
            log.info("compiler take time：{}", compiler.getCompilerTakeTime());
            return true;
        } else {
            log.error("class compilation failed:{}", className);
            log.error(compiler.getCompilerMessage());
            return false;
        }
    }
}
