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

package org.dinky.function;

import org.dinky.function.data.model.UDF;
import org.dinky.function.util.Reflections;

import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.table.catalog.FunctionLanguage;
import org.apache.flink.table.functions.UserDefinedFunction;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

public class FlinkUDFDiscover {

    private static final List<UDF> JAVA_STATIC_UDF_LIST = getCustomStaticUDFs();

    public static List<UDF> getCustomStaticUDFs() {
        if (CollectionUtils.isNotEmpty(JAVA_STATIC_UDF_LIST)) {
            return JAVA_STATIC_UDF_LIST;
        }
        Collection<URL> urls = new ArrayList<>();
        String javaClassPath = System.getProperty("java.class.path");
        if (javaClassPath != null) {
            for (String path : javaClassPath.split(File.pathSeparator)) {
                if (path.contains("/*")) {
                    continue;
                }
                if (!path.contains("extends") && !path.contains("customJar")) {
                    continue;
                }

                try {
                    urls.add(new File(path).toURI().toURL());
                } catch (Exception e) {
                    if (Reflections.log != null) {
                        Reflections.log.warn("Could not get URL", e);
                    }
                }
            }
        }

        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(urls));
        Set<Class<?>> operations =
                reflections.get(Scanners.SubTypes.of(UserDefinedFunction.class).asClass());
        return operations.stream()
                .filter(operation ->
                        !operation.isInterface() && !operation.getName().startsWith("org.apache"))
                .map(operation -> UDF.builder()
                        .className(operation.getName())
                        .functionLanguage(FunctionLanguage.JAVA)
                        .build())
                .collect(Collectors.toList());
    }
}
