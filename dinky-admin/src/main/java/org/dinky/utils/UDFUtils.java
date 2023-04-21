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

package org.dinky.utils;

import org.dinky.constant.FlinkSQLConstant;
import org.dinky.context.DinkyClassLoaderContextHolder;
import org.dinky.context.JarPathContextHolder;
import org.dinky.exception.BusException;
import org.dinky.function.data.model.UDF;
import org.dinky.function.util.UDFUtil;
import org.dinky.model.Task;
import org.dinky.process.context.ProcessContextHolder;
import org.dinky.process.exception.DinkyException;
import org.dinky.process.model.ProcessEntity;
import org.dinky.service.TaskService;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.table.catalog.FunctionLanguage;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;

/** @since 0.6.8 */
public class UDFUtils extends UDFUtil {

    public static UDF taskToUDF(Task task) {
        return UDF.builder()
                .className(task.getSavePointPath())
                .code(task.getStatement())
                .functionLanguage(FunctionLanguage.valueOf(task.getDialect().toUpperCase()))
                .build();
    }
}
