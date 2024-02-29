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

import org.dinky.data.exception.DinkyException;
import org.dinky.trans.ddl.AddFilerOperation;
import org.dinky.utils.URLUtils;

import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.planner.parse.AbstractRegexParseStrategy;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @since 0.7.0
 */
public class AddFileSqlParseStrategy extends AbstractRegexParseStrategy {

    private static final String ADD_FILE = "(add\\s+file)\\s+'(.*)'";
    private static final Pattern ADD_FILE_PATTERN = Pattern.compile(ADD_FILE, Pattern.CASE_INSENSITIVE);
    public static final AddFileSqlParseStrategy INSTANCE = new AddFileSqlParseStrategy();

    public AddFileSqlParseStrategy() {
        super(ADD_FILE_PATTERN);
    }

    public static File[] getInfo(String statement) {
        return getAllFilePath(statement).toArray(new File[0]);
    }

    protected static List<String> patternStatements(String[] statements) {
        return Stream.of(statements)
                .filter(s -> ReUtil.isMatch(ADD_FILE_PATTERN, s))
                .map(x -> ReUtil.findAllGroup0(ADD_FILE_PATTERN, x).get(0))
                .collect(Collectors.toList());
    }

    public static Set<File> getAllFilePath(String... statements) {
        Set<File> fileSet = new HashSet<>();
        patternStatements(statements).stream()
                .map(x -> ReUtil.findAll(ADD_FILE_PATTERN, x, 2).get(0))
                .distinct()
                .forEach(urlPath -> {
                    try {
                        File file = URLUtils.toFile(urlPath);
                        if (file == null || !file.exists()) {
                            throw new DinkyException(StrUtil.format("file : {} not exists!", urlPath));
                        }
                        fileSet.add(file);
                    } catch (Exception e) {
                        throw new DinkyException(StrUtil.format("url:{} request failed!", urlPath), e);
                    }
                });
        return fileSet;
    }

    public static Set<File> getAllFilePath(String statements) {
        return getAllFilePath(new String[] {statements});
    }

    @Override
    public Operation convert(String statement) {
        return new AddFilerOperation(statement);
    }

    @Override
    public String[] getHints() {
        return new String[0];
    }
}
