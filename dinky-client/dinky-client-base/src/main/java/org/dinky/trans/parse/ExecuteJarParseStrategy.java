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

package org.dinky.trans.parse;

import org.dinky.parser.SqlSegment;
import org.dinky.trans.dml.ExecuteJarOperation;

import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.planner.parse.AbstractRegexParseStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;

public class ExecuteJarParseStrategy extends AbstractRegexParseStrategy {
    private static final String PATTERN_STR = "^EXECUTE\\s+JAR\\s+WITH\\s*\\(.+\\)";
    private static final Pattern PATTERN = Pattern.compile(PATTERN_STR, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    public static final ExecuteJarParseStrategy INSTANCE = new ExecuteJarParseStrategy();

    public ExecuteJarParseStrategy() {
        super(PATTERN);
    }

    public static ExecuteJarOperation.JarSubmitParam getInfo(String statement) {
        statement = statement.replace("\r\n", " ").replace("\n", " ") + " ENDOFSQL";
        SqlSegment sqlSegment = new SqlSegment("with", "(with\\s+\\()(.+)(\\))", "',");
        sqlSegment.parse(statement);
        List<String> bodyPieces = sqlSegment.getBodyPieces();
        Map<String, String> keyValue = getKeyValue(bodyPieces);
        return BeanUtil.toBean(
                keyValue,
                ExecuteJarOperation.JarSubmitParam.class,
                CopyOptions.create().setFieldNameEditor(s -> StrUtil.toCamelCase(s, '-')));
    }

    private static Map<String, String> getKeyValue(List<String> list) {
        Map<String, String> map = new HashMap<>();
        Pattern p = Pattern.compile("'(.*?)'\\s*=\\s*'(.*?)'");
        for (String s : list) {
            Matcher m = p.matcher(s + "'");
            if (m.find()) {
                map.put(m.group(1), m.group(2));
            }
        }
        return map;
    }

    @Override
    public Operation convert(String statement) {
        return new ExecuteJarOperation(statement);
    }

    @Override
    public String[] getHints() {
        return new String[0];
    }
}
