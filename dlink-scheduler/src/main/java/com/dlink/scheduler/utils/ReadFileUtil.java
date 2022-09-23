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

package com.dlink.scheduler.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author 郑文豪
 */
public class ReadFileUtil {

    private static final Logger logger = LoggerFactory.getLogger(ReadFileUtil.class);

    public static String taskDefinition(Map<String, Object> maps) {
        InputStream in = ReadFileUtil.class.getResourceAsStream("/json/taskDefinition.json");
        String readFile = readFile(in);
        if (maps == null || maps.isEmpty()) {
            return readFile;
        }
        return StrUtil.format(readFile, maps);
    }

    public static String taskRelation(Map<String, Object> maps) {
        InputStream in = ReadFileUtil.class.getResourceAsStream("/json/taskRelation.json");
        String readFile = readFile(in);
        if (maps == null || maps.isEmpty()) {
            return readFile;
        }
        return StrUtil.format(readFile, maps);
    }

    public static String createTaskDefinition(Map<String, Object> maps) {
        InputStream in = ReadFileUtil.class.getResourceAsStream("/json/createTaskDefinition.json");
        String readFile = readFile(in);
        if (maps == null || maps.isEmpty()) {
            return readFile;
        }
        return StrUtil.format(readFile, maps);
    }

    /**
     * 读取文件
     */
    public static String readFile(InputStream inputStream) {
        try {
            BufferedReader reader = IoUtil.getUtf8Reader(inputStream);
            int ch;
            StringBuilder sb = new StringBuilder();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            IoUtil.close(reader);
            return sb.toString();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

}

