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

import org.dinky.data.exception.BusException;
import org.dinky.function.data.model.UDF;
import org.dinky.function.data.model.UDFPath;

import java.util.ArrayList;
import java.util.List;

/** @since 0.6.8 */
public interface FunctionPackage {

    /**
     * 打包
     *
     * @param udfList udf列表
     * @param taskId 任务id
     * @return 文件绝对路径
     */
    String[] pack(List<UDF> udfList, Integer taskId);

    String pack(UDF udf, Integer taskId);

    /**
     * 打包
     *
     * @param udfList udf 列表
     * @param taskId 任务id
     * @return 打包结果
     */
    static UDFPath bale(List<UDF> udfList, Integer taskId) {
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
                .jarPaths(new JVMPackage().pack(jvmList, taskId))
                .pyPaths(new PythonFunction().pack(pythonList, taskId))
                .build();
    }
    /**
     * 打包
     *
     * @param udf    udf 列表
     * @param taskId 任务id
     * @return 打包结果
     */
    static String bale(UDF udf, Integer taskId) {
        switch (udf.getFunctionLanguage()) {
            case JAVA:
            case SCALA:
                return new JVMPackage().pack(udf, taskId);
            case PYTHON:
                return new PythonFunction().pack(udf, taskId);
            default:
                throw new BusException("");
        }
    }
}
