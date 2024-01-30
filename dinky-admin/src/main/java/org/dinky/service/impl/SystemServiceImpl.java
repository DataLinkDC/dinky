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

package org.dinky.service.impl;

import org.dinky.data.constant.DirConstant;
import org.dinky.data.dto.TreeNodeDTO;
import org.dinky.service.SystemService;
import org.dinky.utils.DirUtil;
import org.dinky.utils.TreeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.map.MapUtil;
import cn.hutool.system.JavaRuntimeInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * SystemServiceImpl
 *
 * @since 2022/10/15 19:17
 */
@Service
@Slf4j
public class SystemServiceImpl implements SystemService {
    private static final Map<String, List<String>> JAR_GROUP_MAP = MapUtil.builder(new HashMap<String, List<String>>())
            .put("Dinky", Arrays.asList("org.dinky"))
            .put("Flink", Arrays.asList("org.apache.flink"))
            .put("CDC", Arrays.asList("com.ververica"))
            .build();

    @Override
    public List<TreeNodeDTO> listLogDir() {
        File systemLogFiles = TreeUtil.getFilesOfDir(DirConstant.LOG_DIR_PATH);
        return TreeUtil.treeNodeData(systemLogFiles, false);
    }

    @Override
    public String readFile(String path) {
        return DirUtil.readFile(path);
    }

    /**
     * @return
     */
    @Override
    public Map<String, List<String>> queryAllClassLoaderJarFiles() {
        Map<String, List<String>> result = new LinkedHashMap<>();
        filterJarGroup(result);
        return result;
    }

    private static void filterJarGroup(Map<String, List<String>> result) {
        List<String> pathList =
                Arrays.asList(Singleton.get(JavaRuntimeInfo.class).getClassPathArray());
        result.put("System", pathList);
        List<File> jarList = pathList.stream()
                .map(FileUtil::file)
                .filter(x -> FileUtil.pathEndsWith(x, ".jar"))
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(jarList)) {
            for (File file : jarList) {
                try (JarFile jarFile = new JarFile(file)) {
                    Manifest manifest = jarFile.getManifest();
                    if (manifest == null) {
                        continue;
                    }
                    Attributes attributes = manifest.getMainAttributes();
                    if (attributes != null) {
                        String groupId = attributes.getValue("Implementation-Vendor-Id");
                        JAR_GROUP_MAP.forEach((k, v) -> {
                            if (v.contains(groupId)) {
                                result.computeIfAbsent(k, key -> new ArrayList<>());
                                result.get(k).add(file.getAbsolutePath());
                            }
                        });
                    }
                } catch (Exception e) {
                    log.error("get All ClassLoaderJarFiles error", e);
                }
            }
        }
    }
}
