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

package org.dinky.function.pool;

import org.dinky.function.data.model.UDF;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/** @since 0.7.0 */
@Slf4j
public class UdfCodePool {

    /** udf code pool key -> class name value -> udf */
    private static final Map<String, UDF> CODE_POOL = new ConcurrentHashMap<>();

    private static final Map<String, String> GIT_POOL = new ConcurrentHashMap<>();

    public static void registerPool(List<UDF> udfList) {
        CODE_POOL.clear();
        CODE_POOL.putAll(udfList.stream().collect(Collectors.toMap(UDF::getClassName, udf -> udf)));
    }

    public static void updateGitPool(Map<String, String> newPool) {
        GIT_POOL.clear();
        GIT_POOL.putAll(newPool);
    }

    public static void addOrUpdate(UDF udf) {
        CODE_POOL.put(udf.getClassName(), udf);
    }

    public static void remove(String className) {
        CODE_POOL.remove(className);
    }

    public static UDF getUDF(String className) {
        UDF udf = CODE_POOL.get(className);
        if (udf == null) {
            String error = StrUtil.format("class: {} is not exists!，maybe for add jar", className);
            log.warn(error);
        }
        return udf;
    }

    public static String getGitPackage(String className) {
        return GIT_POOL.get(className);
    }

    // return CODE_POOL
    public static Map<String, UDF> getUdfCodePool() {
        return CODE_POOL;
    }

    // return GIT_POOL
    public static Map<String, String> getGitPool() {
        return GIT_POOL;
    }
}
