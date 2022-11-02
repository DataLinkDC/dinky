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
import com.dlink.function.data.model.UDFPath;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZackYoung
 * @since 0.6.8
 */
public interface FunctionPackage {

    /**
     * 打包
     *
     * @param udfList   udf列表
     * @param missionId 任务id
     * @return 文件绝对路径
     */
    String[] pack(List<UDF> udfList, Integer missionId);

    /**
     * 打包
     *
     * @param udfList   udf 列表
     * @param missionId 任务id
     * @return 打包结果
     */
    static UDFPath bale(List<UDF> udfList, Integer missionId) {
        List<UDF> jvmList = new ArrayList<>();
        List<UDF> pythonList = new ArrayList<>();
        for (UDF udf : udfList) {
            switch (udf.getFunctionLanguage()) {
                default:
                case JAVA:
                case SCALA:
                    jvmList.add(udf);
                    break;
                case PYTHON:
                    pythonList.add(udf);
            }
        }
        return UDFPath.builder()
                .jarPaths(new JVMPackage().pack(jvmList, missionId))
                .pyPaths(new PythonFunction().pack(pythonList, missionId))
                .build();
    }
}
