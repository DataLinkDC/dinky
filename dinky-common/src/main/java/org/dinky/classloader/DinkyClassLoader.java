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

package org.dinky.classloader;

import org.dinky.context.FlinkUdfPathContextHolder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.Collection;
import java.util.List;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

/** @since 0.7.0 */
@Slf4j
public class DinkyClassLoader extends URLClassLoader {

    public DinkyClassLoader(URL[] urls, ClassLoader parent) {
        super(new URL[] {}, parent);
    }

    public DinkyClassLoader(Collection<File> fileSet, ClassLoader parent) {
        super(new URL[] {}, parent);
        addURL(fileSet);
    }

    public DinkyClassLoader(URL[] urls) {
        super(new URL[] {});
    }

    public DinkyClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(new URL[] {}, parent, factory);
    }

    public void addURL(URL... urls) {
        for (URL url : urls) {
            super.addURL(url);
        }
    }

    public void addURL(Collection<File> fileSet) {
        URL[] urls =
                fileSet.stream()
                        .map(
                                x -> {
                                    try {
                                        return x.toURI().toURL();
                                    } catch (MalformedURLException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                        .toArray(URL[]::new);
        addURL(urls);
    }

    public void addURL(String[] paths, List<String> notExistsFiles) {
        for (String path : paths) {
            File file = new File(path);
            try {
                if (file.isDirectory()) {
                    FileUtil.walkFiles(
                            file,
                            f -> {
                                if (FileUtil.getSuffix(f).equals("jar")) {
                                    try {
                                        super.addURL(f.toURI().toURL());
                                    } catch (MalformedURLException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                    continue;
                }
                if (!file.exists()) {
                    if (notExistsFiles != null && !notExistsFiles.isEmpty()) {
                        notExistsFiles.add(file.getAbsolutePath());
                    }
                    continue;
                }
                super.addURL(file.toURI().toURL());
                FlinkUdfPathContextHolder.addOtherPlugins(file);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addURL(String... paths) {
        this.addURL(paths, null);
    }
}
