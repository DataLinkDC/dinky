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

package org.dinky.context;

import org.dinky.utils.StringUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * The EngineContextHolder
 */
@Slf4j
public class EngineContextHolder {
    private static final Dict ENGINE_CONTEXT = Dict.create();

    /**
     * Get the engine contextload class
     * @param variables the variables
     * @return the class loader variable jexl class
     */
    private static List<String> getClassLoaderVariableJexlClass(String variables) {
        if (StrUtil.isBlank(variables)) {
            log.warn("The variable is empty, please check the configuration.");
            return Collections.emptyList();
        }
        return Arrays.stream(variables.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Load expression variable class to the engine context
     * @param variables the variables
     */
    public static void loadExpressionVariableClass(String variables) {
        getClassLoaderVariableJexlClass(variables).forEach(className -> {
            try {
                String classSimpleName =
                        BeanUtil.getBeanDesc(Class.forName(className)).getSimpleName();
                String snakeCaseClassName = StringUtil.toSnakeCase(true, classSimpleName);
                ENGINE_CONTEXT.set(snakeCaseClassName, Class.forName(className));
                log.info("load class : {}", className);
            } catch (ClassNotFoundException e) {
                log.error(
                        "The class [{}] that needs to be loaded may not be loaded by dinky or there is no jar file of this class under dinky's lib/plugins/extends. Please check, and try again. {}",
                        className,
                        e.getMessage(),
                        e);
            }
        });
    }

    /**
     * Get the engine context
     * @return the engine context
     */
    public static Dict getEngineContext() {
        return ENGINE_CONTEXT;
    }
}
