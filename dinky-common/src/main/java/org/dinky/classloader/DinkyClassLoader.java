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
import java.util.LinkedList;
import java.util.List;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @since 0.7.0
 */
@Slf4j
public class DinkyClassLoader extends URLClassLoader {

    FlinkUdfPathContextHolder udfPathContextHolder = new FlinkUdfPathContextHolder();

    public DinkyClassLoader(URL[] urls, ClassLoader parent) {
        this(urls, parent, null);
    }

    public DinkyClassLoader(Collection<File> fileSet, ClassLoader parent) {
        this(convertFilesToUrls(fileSet), parent, null);
    }

    public DinkyClassLoader(URL[] urls) {
        this(urls, Thread.currentThread().getContextClassLoader(), null);
    }

    public DinkyClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    // class factory method with urls parameters
    public static DinkyClassLoader build(URL... urls) {
        return new DinkyClassLoader(urls);
    }

    public static DinkyClassLoader build(URL[] urls, ClassLoader parent) {
        return new DinkyClassLoader(urls, parent);
    }

    public static DinkyClassLoader build(ClassLoader parent) {
        return new DinkyClassLoader(new URL[] {}, parent);
    }

    // return udfPathContextHolder
    public FlinkUdfPathContextHolder getUdfPathContextHolder() {
        return udfPathContextHolder;
    }

    public void addURLs(URL... urls) {
        for (URL url : urls) {
            super.addURL(url);
        }
    }

    public void addURLs(Collection<File> fileSet) {
        URL[] urls = convertFilesToUrls(fileSet);
        addURLs(urls);
    }

    private static URL[] convertFilesToUrls(Collection<File> fileSet) {
        return fileSet.stream()
                .map(x -> {
                    try {
                        return x.toURI().toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toArray(URL[]::new);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> loadedClass = findLoadedClass(name);

            if (loadedClass == null) {
                try {
                    // try to use this classloader to load
                    loadedClass = this.findClass(name);
                } catch (ClassNotFoundException e) {
                    // maybe is system class, try parents delegate
                    return super.loadClass(name, false);
                }
            }

            if (resolve) {
                resolveClass(loadedClass);
            }
            return loadedClass;
        }
    }

    public static List<File> getJarFiles(String[] paths, List<String> notExistsFiles) {
        List<File> result = new LinkedList<>();
        for (String path : paths) {
            File file = new File(path);
            if (file.isDirectory()) {
                FileUtil.walkFiles(file, f -> {
                    if (FileUtil.getSuffix(f).equals("jar")) {
                        result.add(f);
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
            result.add(file);
        }
        return result;
    }
}
