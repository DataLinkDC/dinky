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

import com.dlink.function.constant.PathConstant;
import com.dlink.function.data.model.UDF;
import com.dlink.process.context.ProcessContextHolder;
import com.dlink.process.model.ProcessEntity;

import org.apache.flink.configuration.ReadableConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * java 编译
 *
 * @author ZackYoung
 * @since 0.6.8
 */
@Slf4j
public class JavaCompiler implements FunctionCompiler {

    /**
     * 函数代码在线动态编译
     *
     * @param udf       udf
     * @param conf      flink-conf
     * @param missionId 任务id
     * @return 是否成功
     */
    @Override
    public boolean compiler(UDF udf, ReadableConfig conf, Integer missionId) {
        ProcessEntity process = ProcessContextHolder.getProcess();
        process.info("正在编译 java 代码 , class: " + udf.getClassName());

        CustomStringJavaCompiler compiler = new CustomStringJavaCompiler(udf.getCode());
        boolean res = compiler.compilerToTmpPath(PathConstant.getUdfCompilerJavaPath(missionId));
        String className = compiler.getFullClassName();
        if (res) {
            process.info("class编译成功:" + className);
            process.info("compilerTakeTime：" + compiler.getCompilerTakeTime());
            return true;
        } else {
            log.error("class编译失败:{}", className);
            process.error("class编译失败:" + className);
            process.error(compiler.getCompilerMessage());
            return false;
        }
    }
}
