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

import com.dlink.assertion.Asserts;
import com.dlink.exception.BusException;
import com.dlink.model.FileNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.StrUtil;

/**
 * DirUtil
 *
 * @author wenmo
 * @since 2022/10/14 21:43
 */
public class DirUtil {

    public static List<FileNode> listDirByPath(String path) {
        List<FileNode> dirList = new ArrayList<>();
        File logDir = new File(path);
        if (logDir.isFile()) {
            throw new BusException(StrUtil.format("Directory path {} is a file.", path));
        }
        File[] files = logDir.listFiles();
        if (Asserts.isNull(files)) {
            throw new BusException(StrUtil.format("Directory path {} does not exist.", path));
        }
        for (File file : files) {
            FileNode fileNode = new FileNode(file.getName(), file.isDirectory(), 0, file.getAbsolutePath());
            if (!fileNode.isDir()) {
                fileNode.setSize(file.length());
            }
            dirList.add(fileNode);
        }
        return dirList;
    }

    public static String readFile(String path) {
        StringBuilder builder = new StringBuilder();
        File file = new File(path);
        if (!file.isFile()) {
            throw new BusException(StrUtil.format("File path {} is not a file.", path));
        }
        try (
                InputStreamReader inputStreamReader =
                        new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String content = "";
            while ((content = bufferedReader.readLine()) != null) {
                builder.append("\n");
                builder.append(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
