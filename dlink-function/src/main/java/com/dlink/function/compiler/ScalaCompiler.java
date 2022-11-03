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

import com.dlink.function.data.model.UDF;
import com.dlink.process.context.ProcessContextHolder;
import com.dlink.process.model.ProcessEntity;

import org.apache.flink.configuration.ReadableConfig;

/**
 * scala编译
 *
 * @author ZackYoung
 * @since 0.6.8
 */
public class ScalaCompiler implements FunctionCompiler {

    @Override
    public boolean compiler(UDF udf, ReadableConfig conf, Integer missionId) {
        ProcessEntity process = ProcessContextHolder.getProcess();

        String className = udf.getClassName();
        process.info("正在编译 scala 代码 , class: " + className);
        if (CustomStringScalaCompiler.getInterpreter(missionId).compileString(udf.getCode())) {
            process.info("scala class编译成功:" + className);
            return true;
        } else {
            process.error("scala class编译失败:" + className);
            return false;
        }
    }
}
