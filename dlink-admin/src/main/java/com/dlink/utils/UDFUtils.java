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

import com.dlink.function.data.model.UDF;
import com.dlink.function.util.UDFUtil;
import com.dlink.model.Task;
import com.dlink.process.context.ProcessContextHolder;
import com.dlink.process.model.ProcessEntity;
import com.dlink.service.TaskService;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.table.catalog.FunctionLanguage;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.extra.spring.SpringUtil;

/**
 * @author ZackYoung
 * @since 0.6.8
 */
public class UDFUtils extends UDFUtil {

    private static final String FUNCTION_SQL_REGEX = "create\\s+.*function\\s+(.*)\\s+as\\s+'(.*)'(\\s+language (.*))?;";

    public static List<UDF> getUDF(String statement) {
        ProcessEntity process = ProcessContextHolder.getProcess();
        process.info("Parse UDF class name:");
        Pattern pattern = Pattern.compile(FUNCTION_SQL_REGEX, Pattern.CASE_INSENSITIVE);
        List<String> udfSqlList = ReUtil.findAllGroup0(pattern, statement);
        List<UDF> udfList = udfSqlList.stream().map(sql -> {
            List<String> groups = ReUtil.getAllGroups(pattern, sql);
            String udfName = groups.get(1);
            String className = groups.get(2);
            Task task = SpringUtil.getBean(TaskService.class).getUDFByClassName(className);
            String code = task.getStatement();
            return UDF.builder()
                    .name(udfName)
                    .className(className)
                    .code(code)
                    .functionLanguage(FunctionLanguage.valueOf(task.getDialect().toUpperCase()))
                    .build();
        }).collect(Collectors.toList());
        List<String> classNameList = udfList.stream().map(UDF::getClassName).collect(Collectors.toList());
        if (classNameList.size() > 0) {
            process.info(StringUtils.join(classNameList, ","));
        }
        process.info(CharSequenceUtil.format("A total of {} UDF have been Parsed.", classNameList.size()));
        return udfList;
    }
}
