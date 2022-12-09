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

package com.dlink.utils;

import com.dlink.context.DinkyClassLoaderContextHolder;
import com.dlink.exception.BusException;
import com.dlink.function.context.UDFPathContextHolder;
import com.dlink.function.data.model.UDF;
import com.dlink.function.util.UDFUtil;
import com.dlink.model.Task;
import com.dlink.process.context.ProcessContextHolder;
import com.dlink.process.exception.DinkyException;
import com.dlink.process.model.ProcessEntity;
import com.dlink.service.TaskService;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.table.catalog.FunctionLanguage;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;

/**
 * @author ZackYoung
 * @since 0.6.8
 */
public class UDFUtils extends UDFUtil {

    private static final String FUNCTION_SQL_REGEX = "(--\\s+)?create\\s+.*function\\s+(.*)\\s+as\\s+'(.*)'(\\s+language (.*))?;";

    public static List<UDF> getUDF(String statement) {
        ProcessEntity process = ProcessContextHolder.getProcess();
        process.info("Parse UDF class name:");
        Pattern pattern = Pattern.compile(FUNCTION_SQL_REGEX, Pattern.CASE_INSENSITIVE);
        List<String> udfSqlList = ReUtil.findAllGroup0(pattern, statement).stream()
                .filter(x -> !x.contains("--"))
                .filter(x -> !ClassLoaderUtil.isPresent(x))
                .collect(Collectors.toList());

        List<UDF> udfList = udfSqlList.stream().map(sql -> {
            List<String> groups = CollUtil.removeEmpty(ReUtil.getAllGroups(pattern, sql));
            String udfName = groups.get(1);
            String className = groups.get(2);
            if (ClassLoaderUtil.isPresent(className)) {
                // 获取已经加载在java的类，对应的包路径
                try {
                    UDFPathContextHolder.add(
                            DinkyClassLoaderContextHolder.get().loadClass(className).getProtectionDomain()
                                    .getCodeSource().getLocation().getPath());
                } catch (ClassNotFoundException e) {
                    throw new DinkyException(e);
                }

                return null;
            }

            Task task = null;
            try {
                task = SpringUtil.getBean(TaskService.class).getUDFByClassName(className);
            } catch (Exception e) {
                String errMsg = StrUtil.format("class:{} not exists!", className);
                process.error(errMsg);
                throw new BusException(errMsg);
            }
            String code = task.getStatement();
            return UDF.builder()
                    .name(udfName)
                    .className(className)
                    .code(code)
                    .functionLanguage(FunctionLanguage.valueOf(task.getDialect().toUpperCase()))
                    .build();
        }).filter(Objects::nonNull).collect(Collectors.toList());
        List<String> classNameList = udfList.stream().map(UDF::getClassName).collect(Collectors.toList());
        if (classNameList.size() > 0) {
            process.info(StringUtils.join(classNameList, ","));
        }
        process.info(CharSequenceUtil.format("A total of {} UDF have been Parsed.", classNameList.size()));
        return udfList;
    }

}
