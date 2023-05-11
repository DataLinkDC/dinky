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

package com.zdpx.coder.json.origin;

import org.apache.flink.api.common.RuntimeExecutionMode;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.zdpx.coder.json.ResultType;
import com.zdpx.jackson.ResultTypeDeserializer;
import com.zdpx.jackson.RuntimeExecutionModeDeserializer;

/** */
public class EnvironmentNode {
    /** flink 运行模式,(流式/批处理) */
    @JsonDeserialize(using = RuntimeExecutionModeDeserializer.class)
    private RuntimeExecutionMode mode;
    /** 并发数量 */
    private int parallelism;
    /** 环境名称 */
    private String name;
    /** 生成结果类型sql/java */
    @JsonDeserialize(using = ResultTypeDeserializer.class)
    private ResultType resultType;

    public RuntimeExecutionMode getMode() {
        return mode;
    }

    // region getter/setter
    public void setMode(RuntimeExecutionMode mode) {
        this.mode = mode;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }
    // endregion
}
