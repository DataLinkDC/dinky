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

package com.dlink.context;

import com.dlink.classloader.DinkyClassLoader;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ZackYoung
 * @since 0.7.0
 */
@Slf4j
public class DinkyClassLoaderContextHolder {

    private static final ThreadLocal<DinkyClassLoader> CLASS_LOADER_CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<ClassLoader> INIT_CLASS_LOADER_CONTEXT = new ThreadLocal<>();

    public static void set(DinkyClassLoader classLoader) {
        CLASS_LOADER_CONTEXT.set(classLoader);
        INIT_CLASS_LOADER_CONTEXT.set(Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    public static DinkyClassLoader get() {
        return CLASS_LOADER_CONTEXT.get();
    }

    public static void clear() {
        DinkyClassLoader dinkyClassLoader = get();
        CLASS_LOADER_CONTEXT.remove();
        try {
            dinkyClassLoader.close();
        } catch (IOException e) {
            log.error("卸载类失败，reason: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        dinkyClassLoader = null;
        Thread.currentThread().setContextClassLoader(INIT_CLASS_LOADER_CONTEXT.get());
        INIT_CLASS_LOADER_CONTEXT.remove();
    }
}
